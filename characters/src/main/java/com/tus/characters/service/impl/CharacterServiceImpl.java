package com.tus.characters.service.impl;

import com.tus.characters.client.UserServiceClient;
import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.GameCharacter;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.mapper.CharacterMapper;
import com.tus.characters.repository.CharactersRepository;
import com.tus.characters.service.ICharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements ICharacterService {

    private final CharactersRepository charactersRepository;

    @Autowired
    private UserServiceClient userClient;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("characterId", "characterClass", "characterRace", "level", "creationDate");

    // Create Character
    @Override
    public CharacterDto createCharacter(CharacterDto characterDto) {
        // Validate user exists via Feign client
        UserDto user = userClient.getUserById(characterDto.getUserId());
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