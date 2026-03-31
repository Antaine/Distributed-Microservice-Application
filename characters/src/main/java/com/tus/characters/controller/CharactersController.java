package com.tus.characters.controller;

import com.tus.characters.constants.CharacterConstants;
import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.ResponseDto;
import com.tus.characters.service.ICharacterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharactersController {
	private final ICharacterService characterService;

    // Create a new character
/*	@Operation(summary = "Create new character")
    @PostMapping
    public ResponseEntity<ResponseDto> createCharacter(@Valid @RequestBody CharacterDto characterDto) {
        characterService.createCharacter(characterDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CharacterConstants.STATUS_201, CharacterConstants.MESSAGE_201_CREATE));
    }*/

    // Get all characters
    @GetMapping
    public ResponseEntity<List<CharacterDto>> getAllCharacters() {
        List<CharacterDto> characters = characterService.getAllCharacters();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(characters);
    }

    // Get characters by user ID
 /*   @GetMapping("/user/{userId}")
    public ResponseEntity<List<CharacterDto>> getCharactersByUserId(@PathVariable Long userId) {
        List<CharacterDto> characters = characterService.getCharactersByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(characters);
    }*/

    // Delete a character by ID
    @DeleteMapping("/{characterId}")
    public ResponseEntity<ResponseDto> deleteCharacter(@PathVariable Long characterId) {
        characterService.deleteCharacter(characterId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CharacterConstants.STATUS_200, "Character deleted successfully"));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CharacterDto>> getCharactersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(characterService.getCharactersByDateRange(startDate, endDate));
    }
 /*   @Operation(summary = "Update character")
    @PutMapping("/{characterId}")
    public ResponseEntity<ResponseDto> updateCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody CharacterDto characterDto) {

        characterService.updateCharacter(characterId, characterDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CharacterConstants.STATUS_200,"Character updated successfully"));
    }*/
    
    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterDto> getCharacterById(@PathVariable Long characterId){

        return ResponseEntity.ok(characterService.getCharacterById(characterId));
    }
    
    @Operation(summary = "Get paginated characters")
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getCharactersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "characterId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<CharacterDto> pageCharacters =characterService.getCharactersPage(page, size, sortBy, direction);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageCharacters.getContent());
        response.put("pageNumber", pageCharacters.getNumber());
        response.put("pageSize", pageCharacters.getSize());
        response.put("totalElements", pageCharacters.getTotalElements());
        response.put("totalPages", pageCharacters.getTotalPages());
        return ResponseEntity.ok(response);
    }
}
