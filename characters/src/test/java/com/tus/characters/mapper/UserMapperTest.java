package com.tus.characters.mapper;

import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapToUserDto_shouldMapBasicFields() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setMobileNumber("1234567890");

        UserDto dto = UserMapper.mapToUserDto(user, new UserDto());

        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("password", dto.getPassword());
        assertEquals("1234567890", dto.getMobileNumber());
    }

    @Test
    void mapToUserDto_shouldMapCharactersList() {
        User user = new User();
        user.setUserId(1L);

        com.tus.characters.entity.Character character = new com.tus.characters.entity.Character();
        character.setCharacterId(10L);
        character.setCharacterClass("Mage");
        character.setCharacterRace("Elf");
        character.setLevel(5);
        character.setUser(user);

        user.setCharacters(List.of(character));

        UserDto dto = UserMapper.mapToUserDto(user, new UserDto());

        assertNotNull(dto.getCharacters());
        assertEquals(1, dto.getCharacters().size());
        assertEquals("Mage", dto.getCharacters().get(0).getCharacterClass());
    }

    @Test
    void mapToUser_shouldMapFieldsCorrectly() {
        UserDto dto = new UserDto();
        dto.setUsername("newuser");
        dto.setEmail("new@test.com");
        dto.setPassword("pass123");
        dto.setMobileNumber("1111111111");

        User user = new User();

        User result = UserMapper.mapToUser(dto, user);

        assertEquals("newuser", result.getUsername());
        assertEquals("new@test.com", result.getEmail());
        assertEquals("pass123", result.getPassword());
        assertEquals("1111111111", result.getMobileNumber());
    }
    
    @Test
    void testMapUserToDto_VerifyFields() {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("TestUser");
        user.setEmail("test@test.com");
        user.setMobileNumber("1234567890");
      //  user.setPassword("pass123");

        UserDto dto = new UserDto();
        UserMapper.mapToUserDto(user, dto);

        assertEquals("TestUser", dto.getUsername());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobileNumber());
    }

    @Test
    void testMapToUserDto_NullCharactersList() {
        User user = new User();
        user.setUserId(1L);
        user.setCharacters(null);

        UserDto dto = new UserDto();
        UserMapper.mapToUserDto(user, dto);
        assertNotNull(dto);
        assertNull(dto.getCharacters()); 
    }
}