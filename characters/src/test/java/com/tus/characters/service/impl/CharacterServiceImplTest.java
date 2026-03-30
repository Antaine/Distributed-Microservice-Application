package com.tus.characters.service.impl;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.entity.Character;
import com.tus.characters.entity.User;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.repository.CharactersRepository;
import com.tus.characters.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceImplTest {

	// Generate Fake Database
	@Mock
	private CharactersRepository characterRepository;
	@Mock
	private UserRepository userRepository;
	// Add to Service
	@InjectMocks
	private CharacterServiceImpl characterService;

	// Helper Methods
	private Character createCharacterWithUser(Long userId, int level) {
		User user = new User();
		user.setUserId(userId);
		Character character = new Character();
		character.setCharacterId(1L);
		character.setLevel(level);
		character.setUser(user);
		return character;
	}

	// Positive Testing
	@Test
	void testCreateCharacter_Success() {

		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(10);
		User user = new User();
		user.setUserId(1L);
		Character character = createCharacterWithUser(1L, 10);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(characterRepository.save(any(Character.class))).thenReturn(character);
		CharacterDto result = characterService.createCharacter(dto);
		assertNotNull(result);
		assertEquals(10, result.getLevel());
		assertEquals(1L, result.getUserId());
		verify(userRepository, times(1)).findById(1L);
		verify(characterRepository, times(1)).save(any(Character.class));
	}

	@Test
	void testGetCharacterById_Success() {

		Character character = createCharacterWithUser(1L, 5);
		when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
		CharacterDto result = characterService.getCharacterById(1L);
		assertNotNull(result);
		assertEquals(5, result.getLevel());
		assertEquals(1L, result.getUserId());
		verify(characterRepository, times(1)).findById(1L);
	}

	@Test
	void testGetCharactersPage() {

		Character character = createCharacterWithUser(1L, 5);
		List<Character> list = List.of(character);
		Page<Character> page = new PageImpl<>(list);
		when(characterRepository.findAll(any(Pageable.class))).thenReturn(page);
		Page<CharacterDto> result = characterService.getCharactersPage(0, 5, "characterId", "asc");
		assertEquals(5, result.getContent().get(0).getLevel());
		assertEquals(1L, result.getContent().get(0).getUserId());
		verify(characterRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	void testDeleteCharacter_Success() {
		Character character = createCharacterWithUser(1L, 5);

		when(characterRepository.findById(1L)).thenReturn(Optional.of(character));

		characterService.deleteCharacter(1L);

		verify(characterRepository, times(1)).delete(character);
	}

	@Test
	void testGetCharactersByUser() {
		Long userId = 1L;

		User user = new User();
		user.setUserId(userId);

		Character character1 = createCharacterWithUser(userId, 5);
		Character character2 = createCharacterWithUser(userId, 10);

		List<Character> characters = List.of(character1, character2);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(characterRepository.findByUser(user)).thenReturn(characters);

		// Act
		List<CharacterDto> result = characterService.getCharactersByUserId(userId);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(5, result.get(0).getLevel());
		assertEquals(10, result.get(1).getLevel());
		assertEquals(userId, result.get(0).getUserId());
		assertEquals(userId, result.get(1).getUserId());

		verify(userRepository, times(1)).findById(userId);
		verify(characterRepository, times(1)).findByUser(user);
	}

	@Test
	void testUpdateCharacter_Success() {
		Character existing = createCharacterWithUser(1L, 5);

		User user = new User();
		user.setUserId(1L);

		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(10);
		dto.setCharacterClass("Mage");
		dto.setCharacterRace("Elf");

		when(characterRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		characterService.updateCharacter(1L, dto);

		assertEquals(10, existing.getLevel());
		assertEquals("Mage", existing.getCharacterClass());
		assertEquals("Elf", existing.getCharacterRace());
		verify(characterRepository).save(existing);
	}

	@Test
	void testGetAllCharacters() {
		Character character = createCharacterWithUser(1L, 5);
		when(characterRepository.findAll()).thenReturn(List.of(character));

		List<CharacterDto> result = characterService.getAllCharacters();

		assertEquals(1, result.size());
		assertEquals(5, result.get(0).getLevel());
		assertEquals(1L, result.get(0).getUserId());
		verify(characterRepository, times(1)).findAll();
	}

	@Test
	void testGetCharactersByDateRange() {
		Character character = createCharacterWithUser(1L, 5);

		when(characterRepository.findByCreationDateBetween(any(), any())).thenReturn(List.of(character));

		List<CharacterDto> result = characterService.getCharactersByDateRange(null, null);

		assertEquals(1, result.size());
		assertEquals(5, result.get(0).getLevel());
		verify(characterRepository, times(1)).findByCreationDateBetween(any(), any());
	}

	@Test
	void testGetAllCharacters_Empty() {
		when(characterRepository.findAll()).thenReturn(List.of());

		List<CharacterDto> result = characterService.getAllCharacters();

		assertTrue(result.isEmpty());
		verify(characterRepository).findAll();
	}

	@Test
	void testUpdateCharacter_SameData_NoUnexpectedChanges() {
		// Arrange
		Character existing = createCharacterWithUser(1L, 10);
		existing.setCharacterClass("Warrior");
		existing.setCharacterRace("Human");

		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(10);
		dto.setCharacterClass("Warrior");
		dto.setCharacterRace("Human");

		when(characterRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(userRepository.findById(1L)).thenReturn(Optional.of(existing.getUser()));

		// Act
		characterService.updateCharacter(1L, dto);

		// Assert
		assertEquals(10, existing.getLevel());
		assertEquals("Warrior", existing.getCharacterClass());
		assertEquals("Human", existing.getCharacterRace());
		assertEquals(1L, existing.getUser().getUserId());

		verify(characterRepository).save(existing);
	}

	@Test
	void createCharacter_CallsUserRepositoryFindById() {
		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(5);

		User user = new User();
		user.setUserId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(characterRepository.save(any())).thenReturn(createCharacterWithUser(1L, 5));

		characterService.createCharacter(dto);

		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void createCharacter_CallsCharacterRepositorySave() {
		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(5);

		User user = new User();
		user.setUserId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(characterRepository.save(any())).thenReturn(createCharacterWithUser(1L, 5));

		characterService.createCharacter(dto);

		verify(characterRepository, times(1)).save(any(Character.class));
	}

	// Negative Testing
	@Test
	void testCreateCharacter_UserNotFound() {
		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);
		dto.setLevel(10);
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			characterService.createCharacter(dto);
		});
		verify(userRepository, times(1)).findById(1L);
		verify(characterRepository, never()).save(any());
	}

	@Test
	void testGetCharacterById_InvalidId() {

		when(characterRepository.findById(99L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			characterService.getCharacterById(99L);
		});
		verify(characterRepository, times(1)).findById(99L);
	}

	@Test
	void testGetCharacterById_NotFound() {

		when(characterRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> characterService.getCharacterById(1L));
		verify(characterRepository, times(1)).findById(1L);
	}

	@Test
	void testDeleteCharacter_NotFound() {
		when(characterRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> characterService.deleteCharacter(1L));

		verify(characterRepository, never()).delete(any());
	}

	@Test
	void testUpdateCharacter_CharacterNotFound() {
		when(characterRepository.findById(1L)).thenReturn(Optional.empty());

		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);

		assertThrows(ResourceNotFoundException.class, () -> characterService.updateCharacter(1L, dto));
	}

	@Test
	void testUpdateCharacter_UserNotFound() {
		Character existing = createCharacterWithUser(1L, 5);

		when(characterRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		CharacterDto dto = new CharacterDto();
		dto.setUserId(1L);

		assertThrows(ResourceNotFoundException.class, () -> characterService.updateCharacter(1L, dto));
	}

	@Test
	void testGetCharactersPage_InvalidSortField() {
		assertThrows(IllegalArgumentException.class, () -> characterService.getCharactersPage(0, 5, "invalid", "asc"));
	}

	@Test
	void testGetCharactersPage_InvalidDirection() {
		assertThrows(IllegalArgumentException.class,
				() -> characterService.getCharactersPage(0, 5, "characterId", "wrong"));
	}

	@Test
	void testGetCharactersByUserId_UserNotFound() {
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> characterService.getCharactersByUserId(userId));
		verify(characterRepository, never()).findByUser(any());
	}

	// Update Project code for this
	/*
	 * @Test void testUpdateCharacter_InvalidLevel_ThrowsException() { // Arrange
	 * Character existing = createCharacterWithUser(1L, 10); CharacterDto dto = new
	 * CharacterDto(); dto.setUserId(1L); dto.setLevel(-5); // invalid level
	 * dto.setCharacterClass("Warrior"); dto.setCharacterRace("Human");
	 * 
	 * when(characterRepository.findById(1L)).thenReturn(Optional.of(existing));
	 * when(userRepository.findById(1L)).thenReturn(Optional.of(existing.getUser()))
	 * ;
	 * 
	 * // Act & Assert IllegalArgumentException ex =
	 * assertThrows(IllegalArgumentException.class, () ->
	 * characterService.updateCharacter(1L, dto));
	 * 
	 * assertEquals("Character level must be between 1 and 20", ex.getMessage());
	 * 
	 * // Verify repository save is never called verify(characterRepository,
	 * never()).save(any()); }
	 */

	@Test
	void deleteCharacter_CallsRepositoryDelete() {
		Character character = createCharacterWithUser(1L, 5);

		when(characterRepository.findById(1L)).thenReturn(Optional.of(character));

		characterService.deleteCharacter(1L);

		verify(characterRepository, times(1)).delete(character);
	}
	
	@Test
	void getCharactersPage_ReturnsPagedResults() {
	    Pageable pageable = PageRequest.of(0, 2);

	    Character character = createCharacterWithUser(1L, 5);
	    Page<Character> page = new PageImpl<>(List.of(character));

	    when(characterRepository.findAll(pageable)).thenReturn(page);

	    Page<CharacterDto> result = characterService.getCharactersPage(0, 2);

	    assertEquals(1, result.getContent().size());
	    verify(characterRepository).findAll(pageable);
	}
	

	
	@Test
	void getCharactersPage_InvalidDirection_ThrowsException() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        characterService.getCharactersPage(0, 10, "level", "wrong");
	    });
	}
	
	@Test
	void getCharactersPage_ValidSortAscending_BypassesIfsAndReturnsPage() {
	    // 1. Arrange: Create the inputs
	    int page = 0;
	    int size = 10;
	    String sortBy = "characterId"; // Use a field in your ALLOWED_SORT_FIELDS
	    String direction = "asc";

	    // 2. Mock the behavior: Create a fake page to be returned by the repo
	    Character character = createCharacterWithUser(1L, 5);
	    Page<Character> mockPage = new PageImpl<>(List.of(character));

	    // This is where the 'any()' is safe because characterRepository is a @Mock here
	    when(characterRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

	    // 3. Act: Call the service
	    Page<CharacterDto> result = characterService.getCharactersPage(page, size, sortBy, direction);

	    // 4. Assert: Verify we got data back and the repo was called
	    assertNotNull(result);
	    assertEquals(1, result.getContent().size());
	    verify(characterRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	void getCharactersPage_DescendingDirection_CoversTernaryBranch() {
	    // Arrange
	    String sortBy = "level";
	    String direction = "desc"; // This triggers the .descending() branch
	    
	    Page<Character> mockPage = new PageImpl<>(List.of(createCharacterWithUser(1L, 10)));
	    when(characterRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

	    // Act
	    Page<CharacterDto> result = characterService.getCharactersPage(0, 10, sortBy, direction);

	    // Assert
	    assertNotNull(result);
	    verify(characterRepository).findAll(any(Pageable.class));
	}

}