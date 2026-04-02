package com.tus.characters.service.impl;

import com.tus.characters.client.UserServiceClient;
import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.GameCharacter;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.mapper.CharacterMapper;
import com.tus.characters.repository.CharactersRepository;
import com.tus.characters.service.ICharacterService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterServiceImpl implements ICharacterService {

    private final CharactersRepository charactersRepository;

    @Autowired
    private UserServiceClient userClient;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("characterId", "characterClass", "characterRace", "level", "creationDate");

    // Create Character
    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
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
        log.error("Fallback triggered for createCharacter: User Service unavailable. Reason: {}", t.getMessage());

        // Optional: return partial/default character to keep service responsive
        CharacterDto fallbackCharacter = new CharacterDto();
        fallbackCharacter.setUserId(characterDto.getUserId());
        fallbackCharacter.setCharacterClass("Unknown");
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
    public List<CharacterDto> getCharactersByUserId(Long userId) {
        // Validate user exists
        UserDto user = userClient.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User", "userId", String.valueOf(userId));
        }

        // Fetch all characters with this userId
        return charactersRepository.findByUserId(userId)
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }

    // Delete Character
    @Override
    public void deleteCharacter(Long characterId) {
        GameCharacter character = charactersRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "characterId", characterId.toString()));
        charactersRepository.delete(character);
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
}