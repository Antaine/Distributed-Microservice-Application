package com.tus.characters.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for Character Entity
 * Abstract and prevents exposing Database
 * Sets Character Details and basic validation
 */
@Data
public class CharacterDto {

	private Long characterId;
    @NotNull(message = "UserId cannot be null")
    private Long userId;

    @NotEmpty(message = "Character class cannot be null or empty")
    private String characterClass;

    @NotEmpty(message = "Character race cannot be null or empty")
    private String characterRace;

    //Level >0 & <21
    @Min(1)@Max(20)
    private int level;
    
    private LocalDate creationDate;
}