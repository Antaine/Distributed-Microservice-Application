package com.tus.characters.mapper;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.Character;

/**
 * Converts Character Entity to Data Transfer Object and vice versa
 */
public class CharacterMapper {

    // Convert Character entity to DTO
	 public static CharacterDto mapToCharacterDto(Character character) {
	        CharacterDto dto = new CharacterDto();
	        dto.setCharacterId(character.getCharacterId());
	        dto.setCharacterClass(character.getCharacterClass());
	        dto.setCharacterRace(character.getCharacterRace());
	        dto.setLevel(character.getLevel());
	        dto.setUserId(character.getUserId()); // now works
	        //dto.setCreationDate(character.getCreationDate());
	        return dto;
	    }

	    // Convert DTO → Character entity
	    public static Character mapToCharacter(CharacterDto dto, UserDto user) {
	        Character character = new Character();
	        character.setCharacterClass(dto.getCharacterClass());
	        character.setCharacterRace(dto.getCharacterRace());
	        character.setLevel(dto.getLevel());
	        character.setUserId(user.getUserId());
	        // Creation date will be set automatically by @PrePersist
	        return character;
	    }
}