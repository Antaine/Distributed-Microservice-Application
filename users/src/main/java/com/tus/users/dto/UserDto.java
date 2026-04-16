package com.tus.users.dto;

import java.util.List;

import com.tus.users.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for User Entity
 * Prevents exposing database entity directly
 */
@Data
public class UserDto {

    private Long userId;

    @NotEmpty(message = "Name cannot be null or empty")
    @Size(min = 5, max = 30, message = "Name length must be between 5 and 30")
    private String username;

    @NotEmpty(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotEmpty(message = "Password cannot be null or empty")
    @Size(min = 6, max = 50, message = "Password length must be between 6 and 20")
    private String password;

    private List<CharacterDto> characters;

    // ✅ Correct role field
    private Role role;
}