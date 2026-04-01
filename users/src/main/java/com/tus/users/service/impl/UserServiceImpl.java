package com.tus.users.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tus.users.dto.UserDto;
import com.tus.users.entity.User;
import com.tus.users.exceptions.ResourceNotFoundException;
import com.tus.users.mapper.UserMapper;
import com.tus.users.repository.UserRepository;
import com.tus.users.service.IUserService;

import lombok.RequiredArgsConstructor;

/**
 * Implements inherited methods from IUserService
 * Basic CRUD Functionality and validation
 */
@Service@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

	private static final String USER = "User";
	private static final String USER_ID = "userId";
    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();
        UserMapper.mapToUser(userDto, user);
        return userRepository.save(user);
    }		

    //AI Example 4
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User",
                "userId",
                String.valueOf(userId)
            ));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
        	throw new ResourceNotFoundException(USER,USER_ID,String.valueOf(userId));
        }
        userRepository.deleteById(userId);
    }
    
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setMobileNumber(user.getMobileNumber());
            dto.setPassword(user.getPassword());
            return dto;
        }).toList();
    }
    
    public User updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
        		.orElseThrow(() -> new ResourceNotFoundException(USER,USER_ID,String.valueOf(userId)));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());
        existingUser.setMobileNumber(userDto.getMobileNumber());

        return userRepository.save(existingUser);
    }


}