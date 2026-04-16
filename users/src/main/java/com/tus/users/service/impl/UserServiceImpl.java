package com.tus.users.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.tus.users.dto.CharacterDto;
import com.tus.users.dto.UserDto;
import com.tus.users.entity.Role;
import com.tus.users.entity.User;
import com.tus.users.exceptions.ResourceNotFoundException;
import com.tus.users.mapper.UserMapper;
import com.tus.users.repository.UserRepository;
import com.tus.users.service.IUserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final String USER = "User";
    private static final String USER_ID = "userId";

    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto.getUsername());

        User user = new User();
        user.setRole(Role.USER);
        UserMapper.mapToUser(userDto, user);

        User savedUser = userRepository.save(user);

        log.info("User saved with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER,
                        USER_ID,
                        String.valueOf(userId)
                ));
    }

    @Override
    public void deleteUser(Long userId) {
        log.warn("Deleting user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(USER, USER_ID, String.valueOf(userId));
        }

        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();
        log.info("Total users found: {}", users.size());

        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setMobileNumber(user.getMobileNumber());
            dto.setRole(user.getRole());
            return dto;
        }).toList();
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        log.info("Updating user with ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER, USER_ID, String.valueOf(userId)));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());
        existingUser.setMobileNumber(userDto.getMobileNumber());

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated: {}", updatedUser.getUsername());

        return updatedUser;
    }

    public List<CharacterDto> getCharactersByUserId(Long userId) {
        log.info("Calling character-service for userId: {}", userId);

        return webClientBuilder.build()
                .get()
                .uri("http://character-service/api/characters/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(CharacterDto.class)
                .collectList()
                .block();
    }

    @Override
    @CircuitBreaker(name = "characterService", fallbackMethod = "fallbackCharacters")
    public UserDto getUserWithCharacters(Long userId) {
        log.info("Fetching user + characters for userId {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER, USER_ID, String.valueOf(userId)
                ));

        List<CharacterDto> characters = getCharactersByUserId(userId);

        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole());
        dto.setCharacters(characters);

        log.info("Successfully fetched user + characters for {}", userId);

        return dto;
    }

    public User createUser(User user) {
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public UserDto fallbackCharacters(Long userId, Throwable ex) {
        log.error("Character service FAILED for user {}: {}", userId, ex.getMessage());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole());
        dto.setCharacters(new ArrayList<>());

        log.info("Returning fallback response for user {}", userId);

        return dto;
    }
}