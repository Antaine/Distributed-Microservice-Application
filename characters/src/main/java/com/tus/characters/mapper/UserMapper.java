package com.tus.characters.mapper;

import java.util.List;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;

/**
 * Converts User Entity to Data Transfer Object and vice versa
 */
public class UserMapper {

	//Convert User to DTO
	 public static UserDto mapToUserDto(User user, UserDto userDto) {
		 
		 	userDto.setUserId(user.getUserId());  
	        userDto.setUsername(user.getUsername());
	        userDto.setEmail(user.getEmail());
	        userDto.setPassword(user.getPassword());
	        userDto.setMobileNumber(user.getMobileNumber());
	        
	        if (user.getCharacters() != null) {
		        List<CharacterDto> charDtos = user.getCharacters().stream().map(c -> {
		            CharacterDto cdto = new CharacterDto();
		            cdto.setCharacterId(c.getCharacterId());
		            cdto.setUserId(user.getUserId());
		            cdto.setCharacterClass(c.getCharacterClass());
		            cdto.setCharacterRace(c.getCharacterRace());
		            cdto.setLevel(c.getLevel());
		            return cdto;
		        }).toList();
		        userDto.setCharacters(charDtos);
	        }
	        
	        return userDto;
	    }

	 //Convert DTO to Character
	    public static User mapToUser(UserDto userDto, User user) {
	        user.setUsername(userDto.getUsername());
	        user.setEmail(userDto.getEmail());
	        user.setPassword(userDto.getPassword());
	        user.setMobileNumber(userDto.getMobileNumber());
	        return user;
	    }
}