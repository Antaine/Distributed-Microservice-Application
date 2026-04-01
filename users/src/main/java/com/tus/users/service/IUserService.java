package com.tus.users.service;

import java.util.List;

import com.tus.users.dto.UserDto;
import com.tus.users.entity.User;

public interface IUserService {
   // User createUser(User user);
    User getUserById(Long userId);
    User updateUser(Long userId, UserDto userDto);
    List<UserDto> getAllUsers();
	User createUser(UserDto userDto);
	void deleteUser(Long userId);
    
    
}