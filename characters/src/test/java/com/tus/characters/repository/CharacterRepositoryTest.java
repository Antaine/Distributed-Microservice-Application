package com.tus.characters.repository;

import com.tus.characters.audit.AuditAwareImpl;
import com.tus.characters.entity.Character;
import com.tus.characters.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AuditAwareImpl.class)
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
class CharacterRepositoryTest {

	@Autowired
	private CharactersRepository characterRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void findByUser_ReturnsCharacters() {
		User user = new User();
		user.setUsername("test");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = entityManager.persistAndFlush(user);

		Character character = new Character();
		character.setLevel(5);
		character.setCharacterClass("Mage");
		character.setCharacterRace("Elf");
		character.setUser(user);
		entityManager.persistAndFlush(character);

		List<Character> result = characterRepository.findByUser(user);
		assertEquals(1, result.size());
	}

	@Test
	void deleteCharacter_CallsRepositoryDelete() {
		User user = new User();
		user.setUsername("testUser");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = entityManager.persistAndFlush(user);

		Character character = new Character();
		character.setLevel(5);
		character.setCharacterClass("Mage");
		character.setCharacterRace("Elf");
		character.setUser(user);
		character = entityManager.persistAndFlush(character);

		characterRepository.deleteById(character.getCharacterId());
		boolean exists = characterRepository.existsById(character.getCharacterId());
		assertFalse(exists, "Character should be deleted");
	}

	@Test
	void saveAndRetrieveCharacter() {
		User user = new User();
		user.setUsername("tester");
		user.setEmail("tester@example.com");
		user.setPassword("password");
		user = entityManager.persistAndFlush(user);

		Character character = new Character();
		character.setLevel(7);
		character.setCharacterClass("Mage");
		character.setCharacterRace("Elf");
		character.setUser(user);
		character = entityManager.persistAndFlush(character);

		Character retrieved = characterRepository.findById(character.getCharacterId()).orElse(null);
		assertNotNull(retrieved);
		assertEquals(7, retrieved.getLevel());
		assertEquals("Mage", retrieved.getCharacterClass());
	}

	@Test
	void createCharacter_NullClassOrRace_ThrowsException() {
		// Arrange
		User user = new User();
		user.setUsername("tester");
		user.setEmail("tester@example.com");
		user.setPassword("password");
		user = entityManager.persistAndFlush(user);

		Character character = new Character();
		character.setLevel(5);
		character.setCharacterClass(null);
		character.setCharacterRace(null);
		character.setUser(user);
		Exception exception = assertThrows(Exception.class, () -> {
			entityManager.persistAndFlush(character);
		});

		String message = exception.getMessage().toLowerCase();
		assertTrue(message.contains("character_class") || message.contains("null"),
				"Should mention the column name or null violation. Actual message: " + message);
	}

	@Test
	void getCharactersFromEmptyRepository_ReturnsEmptyList() {
		List<Character> characters = characterRepository.findAll();
		assertTrue(characters.isEmpty(), "Repository should return empty list if no characters exist");
	}

	@Test
	void testSaveCharacter_InvalidLevel_ThrowsException() {
		User user = new User();
		user.setUsername("validUser");
		user.setEmail("valid@test.com");
		user.setPassword("password");
		entityManager.persistAndFlush(user);

		Character invalidChar = new Character();
		invalidChar.setLevel(25);
		invalidChar.setCharacterClass("Mage");
		invalidChar.setCharacterRace("Elf");
		invalidChar.setUser(user);

		assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
			entityManager.persistAndFlush(invalidChar);
		});
	}

	@Test
	void createCharacter_Valid_PersistsSuccessfully() {
		User user = new User();
		user.setUsername("tester");
		user.setEmail("tester@example.com");
		user.setPassword("password");
		user = entityManager.persistAndFlush(user);

		Character character = new Character();
		character.setLevel(5);
		character.setCharacterClass("Mage");
		character.setCharacterRace("Elf");
		character.setUser(user);

		Character saved = entityManager.persistAndFlush(character);

		assertNotNull(saved.getCharacterId());
	}
	
	
}