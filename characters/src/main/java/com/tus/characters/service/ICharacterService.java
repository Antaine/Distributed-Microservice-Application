package com.tus.characters.service;

import com.tus.characters.dto.CharacterDto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

public interface ICharacterService {

	//Create New Character
    CharacterDto createCharacter(CharacterDto characterDto);
    
    //Get All Characters
    List<CharacterDto> getAllCharacters();
    
    //Get User Characters
    List<CharacterDto> getCharactersByUserId(Long userId);
    
    //Delete Character
    void deleteCharacter(Long characterId);
    
    //Get Character by Date Range
    List<CharacterDto> getCharactersByDateRange(LocalDate startDate,LocalDate endDate);
    
    //Update Character
    void updateCharacter(Long characterId,CharacterDto characterDto);
    
    //Get Character by Character ID
    CharacterDto getCharacterById(Long characterId);
    
    //Page Options
    Page<CharacterDto> getCharactersPage(int page,int size);
    
    //Sorted Page
    Page<CharacterDto> getCharactersPage(int page,int size,String sortBy,String direction);
}