package com.tus.characters.service.impl;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.entity.Character;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.mapper.CharacterMapper;
import com.tus.characters.repository.CharactersRepository;
import com.tus.characters.service.ICharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service@RequiredArgsConstructor
public class CharacterServiceImpl implements ICharacterService {

	//Constants
    private final CharactersRepository charactersRepository;
//    private final UserRepository userRepository;
    private static final String USER = "User";
    private static final String USER_ID = "userId";
    private static final String CHARACTER = "Character";
    private static final String CHARACTER_ID = "characterId";

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("characterId", "characterClass", "characterRace", "level", "creationDate");

    //Create Character
/*    @Override
    public CharacterDto createCharacter(CharacterDto characterDto) {
        User user = userRepository.findById(characterDto.getUserId())
                .orElseThrow(() ->new ResourceNotFoundException("User", "userId",String.valueOf(characterDto.getUserId())));

        Character character = CharacterMapper.mapToCharacter(characterDto, user);
        Character savedCharacter = charactersRepository.save(character);
        return CharacterMapper.mapToCharacterDto(savedCharacter);
    }*/

    //Return all Characters
    @Override
    public List<CharacterDto> getAllCharacters() {
        return charactersRepository.findAll()
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }

/*    @Override
    public List<CharacterDto> getCharactersByUserId(Long userId) {
        User user = userRepository.findById(userId)
        		.orElseThrow(() ->new ResourceNotFoundException("User", "userId",userId.toString()));

        return charactersRepository.findByUser(user)
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }*/

    //Delete Character
    @Override
    public void deleteCharacter(Long characterId) {
        Character character = charactersRepository.findById(characterId)
        		.orElseThrow(() ->new ResourceNotFoundException("Character","characterId",characterId.toString()));
        charactersRepository.delete(character);
    }

    //Get Characters In sorted Date Range
    @Override
    public List<CharacterDto> getCharactersByDateRange(LocalDate startDate,LocalDate endDate) {
        return charactersRepository
                .findByCreationDateBetween(startDate, endDate)
                .stream()
                .map(CharacterMapper::mapToCharacterDto)
                .collect(Collectors.toList());
    }

    //Update Character
  /*  @Override
    public void updateCharacter(Long characterId,CharacterDto characterDto) {

        Character existingCharacter =
                charactersRepository.findById(characterId)
                .orElseThrow(() ->new ResourceNotFoundException("Character","characterId",characterId.toString()));

        User user = userRepository.findById(characterDto.getUserId())
                .orElseThrow(() ->new ResourceNotFoundException("User","userId",String.valueOf(characterDto.getUserId())));

        existingCharacter.setCharacterClass(characterDto.getCharacterClass());
        existingCharacter.setCharacterRace(characterDto.getCharacterRace());
        existingCharacter.setLevel(characterDto.getLevel());
        existingCharacter.setUser(user);
        charactersRepository.save(existingCharacter);
    }*/

    //Get Character by Character ID
    @Override
    public CharacterDto getCharacterById(Long characterId) {
        Character character =
                charactersRepository.findById(characterId)
                .orElseThrow(() ->new ResourceNotFoundException("Character","characterId",characterId.toString()));
        return CharacterMapper.mapToCharacterDto(character);
    }

    //Get Page of Characters
    @Override
    public Page<CharacterDto> getCharactersPage(int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        return charactersRepository.findAll(pageable).map(CharacterMapper::mapToCharacterDto);
    }

    //
    @Override
    public Page<CharacterDto> getCharactersPage(int page,int size,String sortBy,String direction) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {throw new IllegalArgumentException("Invalid sort field: " + sortBy);}

        if (!direction.equalsIgnoreCase("asc") &&!direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }
        Sort sort = direction.equalsIgnoreCase("desc")? Sort.by(sortBy).descending(): Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return charactersRepository.findAll(pageable).map(CharacterMapper::mapToCharacterDto);
    }
}