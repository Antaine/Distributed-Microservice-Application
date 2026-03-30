package com.tus.characters.mapper;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.entity.Character;
import com.tus.characters.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterMapperTest {

    @Test
    void mapToCharacterDto_shouldMapAllFields() {
        User user = new User();
        user.setUserId(1L);

        Character character = new Character();
        character.setCharacterId(10L);
        character.setCharacterClass("Mage");
        character.setCharacterRace("Elf");
        character.setLevel(5);
        character.setUser(user);

        CharacterDto dto = CharacterMapper.mapToCharacterDto(character);

        assertEquals(10L, dto.getCharacterId());
        assertEquals(1L, dto.getUserId());
        assertEquals("Mage", dto.getCharacterClass());
        assertEquals("Elf", dto.getCharacterRace());
        assertEquals(5, dto.getLevel());
    }

    @Test
    void mapToCharacter_shouldMapFieldsCorrectly() {
        User user = new User();
        user.setUserId(1L);

        CharacterDto dto = new CharacterDto();
        dto.setCharacterClass("Warrior");
        dto.setCharacterRace("Human");
        dto.setLevel(10);

        Character character = CharacterMapper.mapToCharacter(dto, user);

        assertEquals("Warrior", character.getCharacterClass());
        assertEquals("Human", character.getCharacterRace());
        assertEquals(10, character.getLevel());
        assertEquals(user, character.getUser());
    }
    
    @Test
    void testMapToCharacterDto_BoundaryLevel1() {
        User user = new User();
        user.setUserId(1L);
        Character character = new Character();
        character.setLevel(1);
        character.setUser(user);
        CharacterDto dto = CharacterMapper.mapToCharacterDto(character);
        assertEquals(1, dto.getLevel());
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void testMapToCharacterDto_BoundaryLevel20() {
        User user = new User();
        user.setUserId(1L);
        Character character = new Character();
        character.setLevel(20);
        character.setUser(user);
        CharacterDto dto = CharacterMapper.mapToCharacterDto(character);
        assertEquals(20, dto.getLevel());
        assertEquals(1L, dto.getUserId());
    }
    
    @Test
    void testMapToCharacterDto_InvalidLevelBelowMin() {
        User user = new User();
        user.setUserId(1L);
        Character character = new Character();
        character.setLevel(0);
        character.setUser(user);
        CharacterDto dto = CharacterMapper.mapToCharacterDto(character);
        assertEquals(0, dto.getLevel());
        assertEquals(1L, dto.getUserId());
    }
    
    @Test
    void testMapToCharacterDto_InvalidLevelAboveMax() {
        User user = new User();
        user.setUserId(1L);
        Character character = new Character();
        character.setLevel(21);
        character.setUser(user);
        CharacterDto dto = CharacterMapper.mapToCharacterDto(character);
        assertEquals(21, dto.getLevel());
        assertEquals(1L, dto.getUserId());
    }
}