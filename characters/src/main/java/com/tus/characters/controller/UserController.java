package com.tus.characters.controller;

import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;
import com.tus.characters.mapper.UserMapper;
import com.tus.characters.service.IUserService;
import com.tus.characters.service.impl.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        // Create user entity
        User user = userService.createUser(userDto);
        // Map back to DTO for response
        UserDto response = UserMapper.mapToUserDto(user, new UserDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserDto userDto = UserMapper.mapToUserDto(user, new UserDto());
        return ResponseEntity.ok(userDto);
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    
    //DELETE user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId); // calls your existing service method
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("statusMsg", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto userDto) {

        User updatedUser = userService.updateUser(userId, userDto);
        UserDto updatedUserDto = UserMapper.mapToUserDto(updatedUser, new UserDto());

        return ResponseEntity.ok(updatedUserDto);
    }
}