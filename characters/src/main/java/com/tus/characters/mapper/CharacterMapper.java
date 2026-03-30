package com.tus.characters.mapper;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.entity.Character;
import com.tus.characters.entity.User;

/**
 * Converts Character Entity to Data Transfer Object and vice versa
 */
public class CharacterMapper {

	//Convert Character to DTO
	public static CharacterDto mapToCharacterDto(Character character) {
	    CharacterDto dto = new CharacterDto();
	    dto.setCharacterId(character.getCharacterId());
	    dto.setUserId(character.getUser().getUserId());
	    dto.setCharacterClass(character.getCharacterClass());
	    dto.setCharacterRace(character.getCharacterRace());
	    dto.setLevel(character.getLevel());
	    return dto;
	}

	//Convert DTO to Character
	public static Character mapToCharacter(CharacterDto dto, User user) {
		Character character = new Character();
		character.setUser(user);
		character.setCharacterClass(dto.getCharacterClass());
		character.setCharacterRace(dto.getCharacterRace());
		character.setLevel(dto.getLevel());
	    return character;
	}
}