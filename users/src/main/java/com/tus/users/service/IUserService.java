package com.tus.users.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;

import com.tus.users.dto.UserDto;
import com.tus.users.entity.User;

@FeignClient(name = "user-service")
public interface IUserService {
   // User createUser(User user);
    User getUserById(Long userId);
    User updateUser(Long userId, UserDto userDto);
    List<UserDto> getAllUsers();
	User createUser(UserDto userDto);
	void deleteUser(Long userId);
	UserDto getUserWithCharacters(Long userId);
    
    
}