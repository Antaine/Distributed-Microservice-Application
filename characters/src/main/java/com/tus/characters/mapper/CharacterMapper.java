package com.tus.characters.mapper;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.GameCharacter;

public class CharacterMapper {

    // Convert GameCharacter entity → CharacterDto
    public static CharacterDto mapToCharacterDto(GameCharacter character) {
        if (character == null) return null;

        CharacterDto dto = new CharacterDto();
        dto.setCharacterId(character.getCharacterId());
        dto.setCharacterClass(character.getCharacterClass());
        dto.setCharacterRace(character.getCharacterRace());
        dto.setLevel(character.getLevel());
        dto.setUserId(character.getUserId()); // link to the user
        dto.setCreationDate(character.getCreationDate());
        return dto;
    }

    // Convert CharacterDto → GameCharacter entity
    public static GameCharacter mapToCharacter(CharacterDto dto, UserDto user) {
        if (dto == null) return null;

        GameCharacter character = new GameCharacter();
        character.setCharacterClass(dto.getCharacterClass());
        character.setCharacterRace(dto.getCharacterRace());
        character.setLevel(dto.getLevel());

        // Validate that user exists before mapping
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User must exist to create a character");
        }
        character.setUserId(user.getUserId());

        // Creation date is handled by @PrePersist in the entity
        return character;
    }
}