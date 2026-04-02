package com.tus.characters.service.impl;

import com.tus.characters.client.UserServiceClient;
import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.GameCharacter;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.mapper.CharacterMapper;
import com.tus.characters.repository.CharactersRepository;
import com.tus.characters.service.ICharacterService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// ✅ Resilience4j annotations
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

// ✅ Resilience4j core classes (IMPORTANT)
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterServiceImpl implements ICharacterService {

    private final CharactersRepository charactersRepository;
    @Autowired
    private RetryRegistry retryRegistry;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @Autowired
    private UserServiceClient userClient;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("characterId", "characterClass", "characterRace", "level", "creationDate");

    // Create Character
    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    @Retry(name = "userService", fallbackMethod = "fallbackUser")
    public CharacterDto createCharacter(CharacterDto characterDto) {
        log.info("Calling User Service for userId: {}", characterDto.getUserId());

        UserDto user;
        try {
            // Validate user exists via Feign client
            user = userClient.getUserById(characterDto.getUserId());
        } catch (Exception e) {
            // Wrap any exception into RuntimeException so Resilience4j triggers fallback
            log.error("Feign call to User Service failed for userId {}: {}", characterDto.getUserId(), e.getMessage());
            throw new RuntimeException("User service unavailable", e);
        }

        if (user == null) {
            throw new ResourceNotFoundException("User", "userId", String.valueOf(characterDto.getUserId()));
        }

        // Map DTO to Character entity
        GameCharacter character = CharacterMapper.mapToCharacter(characterDto, user);

        // Save Character
        GameCharacter savedCharacter = charactersRepository.save(character);

        // Map back to DTO
        return CharacterMapper.mapToCharacterDto(savedCharacter);
    }
    
 // Fallback method
    public CharacterDto fallbackUser(CharacterDto characterDto, Throwable t) {
    	log.warn("Fallback triggered (User Service DOWN): {}", t.getMessage());

        CharacterDto fallbackCharacter = new CharacterDto();
        fallbackCharacter.setUserId(characterDto.getUserId() != null ? characterDto.getUserId() : -1L);
        fallbackCharacter.setCharacterClass("Unknown");
        fallbackCharacter.setCharacterRace("Unknown");
        fallbackCharacter.setLevel(1);

        return fallbackCharacter;
    }

    // Return all Characters
    @Override
    public List<CharacterDto> getAllCharacters() {
        return charactersRepository.findAll()
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }

    // Get Characters by User ID
    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUserList")
    @Retry(name = "userService", fallbackMethod = "fallbackUserList")
    public List<CharacterDto> getCharactersByUserId(Long userId) {

        log.info("Fetching characters for userId: {}", userId);

        UserDto user;
        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            log.error("User service failed for userId {}: {}", userId, e.getMessage());
            throw new RuntimeException("User service unavailable", e);
        }

        if (user == null) {
            throw new ResourceNotFoundException("User", "userId", String.valueOf(userId));
        }

        return charactersRepository.findByUserId(userId)
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }
    
    public List<CharacterDto> fallbackUserList(Long userId, Throwable t) {
        log.error("Fallback triggered for getCharactersByUserId: {}", t.getMessage());

        // Return empty list instead of crashing
        return Collections.emptyList();
    }

    // Delete Character
    @Override
    public void deleteCharacter(Long characterId, Long userId) {

        GameCharacter character = charactersRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "characterId", characterId.toString()));

        // Check ownership
        if (!character.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Character does not belong to user");
        }

        charactersRepository.delete(character);

        log.info("Character {} deleted by user {}", characterId, userId);
    }
    // Get Characters In Date Range
    @Override
    public List<CharacterDto> getCharactersByDateRange(LocalDate startDate, LocalDate endDate) {
        return charactersRepository.findByCreationDateBetween(startDate, endDate)
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }

    // Get Character by Character ID
    @Override
    public CharacterDto getCharacterById(Long characterId) {
        GameCharacter character = charactersRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "characterId", characterId.toString()));
        return CharacterMapper.mapToCharacterDto(character);
    }

    // Pagination
    @Override
    public Page<CharacterDto> getCharactersPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return charactersRepository.findAll(pageable).map(CharacterMapper::mapToCharacterDto);
    }

    @Override
    public Page<CharacterDto> getCharactersPage(int page, int size, String sortBy, String direction) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }

        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return charactersRepository.findAll(pageable).map(CharacterMapper::mapToCharacterDto);
    }
    
    @PostConstruct
    public void registerResilienceLogs() {

        // Retry logging
        io.github.resilience4j.retry.Retry retry = retryRegistry.retry("userService");

        retry.getEventPublisher()
                .onRetry(event ->
                        log.warn("Retry attempt #: {}", event.getNumberOfRetryAttempts()))
                .onError(event ->
                        log.error("Retry error: {}", event.getLastThrowable().getMessage()));

        // CircuitBreaker logging (FULLY QUALIFIED to avoid wrong import)
        io.github.resilience4j.circuitbreaker.CircuitBreaker cb =
                circuitBreakerRegistry.circuitBreaker("userService");

        cb.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("CircuitBreaker state changed: {}", event.getStateTransition()))
                .onCallNotPermitted(event ->
                        log.error("Call blocked because CircuitBreaker is OPEN"));
    }
}