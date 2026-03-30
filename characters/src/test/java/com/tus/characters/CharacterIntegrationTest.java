package com.tus.characters;

import com.tus.characters.dto.CharacterDto;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;
import com.tus.characters.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate; 
	@Autowired
	private UserRepository userRepository;

	@Test
	void fullApplicationFlow_CreateCharacter_ReturnsCreated() {

		User user = new User();
		user.setUsername("integrationUser");
		user.setEmail("int@test.com");
		user.setPassword("password");
		userRepository.save(user);

		CharacterDto newChar = new CharacterDto();
		newChar.setCharacterClass("Rogue");
		newChar.setCharacterRace("Human");
		newChar.setLevel(5);
		newChar.setUserId(user.getUserId()); // Link to the real ID

		ResponseEntity<String> response = restTemplate.postForEntity("/api/characters", newChar, String.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"Should return 201 Created. Response body: " + response.getBody());
	}

	@Test
	void endToEnd_UserCreationToCharacterAssignment_Flow() {

		UserDto userDto = new UserDto();
		userDto.setUsername("IntegrationHero");
		userDto.setEmail("hero@test.com");
		userDto.setPassword("SecurePass123!");

		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
		assertEquals(HttpStatus.CREATED, userResponse.getStatusCode(), "User creation failed!");
		Long userId = userResponse.getBody().getUserId();

		CharacterDto charDto = new CharacterDto();
		charDto.setUserId(userId);
		charDto.setCharacterClass("Paladin");
		charDto.setCharacterRace("Dwarf");
		charDto.setLevel(1);

		ResponseEntity<String> charResponse = restTemplate.postForEntity("/api/characters", charDto, String.class);
		assertEquals(HttpStatus.CREATED, charResponse.getStatusCode(), "Character creation failed!");
		ResponseEntity<List> listResponse = restTemplate.getForEntity("/api/characters", List.class);
		assertTrue(listResponse.getBody().size() > 0, "Character list should not be empty");
	}

	@Test
	void verifyAuditingFields_ArePopulated() {
		User user = new User();
		user.setUsername("auditUser");
		user.setEmail("audit@test.com");
		user.setPassword("password");
		userRepository.save(user);
		User savedUser = userRepository.findById(user.getUserId()).get();

		assertNotNull(savedUser.getCreatedAt(), "CreatedAt should be auto-populated");
		assertNotNull(savedUser.getCreatedBy(), "CreatedBy should be auto-populated");
		assertEquals("CHARACTERS_MS", savedUser.getCreatedBy()); 
	}

	@Test
	void getCharacters_WithPagination_ReturnsSuccess() {
		
		String url = "/api/characters?page=0&size=5&sortField=level&sortDir=desc";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"Expected 200 but got 500. Check console logs! Response: " + response.getBody());
	}

	@Test
	void testAuditing_PopulatesCreatedBy() {
		User user = new User();
		user.setUsername("AuditTest");
		user.setEmail("audit@test.com");
		user.setPassword("pass123");
		User saved = userRepository.save(user);
		assertNotNull(saved.getCreatedAt());
	}

	@Test
	void testAuditing_UpdatesLastModifiedFields() throws InterruptedException {
		User user = new User();
		user.setUsername("updater");
		user.setEmail("update@test.com");
		user.setPassword("pass123");
		user.setCharacters(new ArrayList<>());

		User saved = userRepository.saveAndFlush(user);
		LocalDateTime firstModified = saved.getUpdatedAt();
		Thread.sleep(100);

		saved.setMobileNumber("0871234567");
		User updated = userRepository.saveAndFlush(saved);
		assertTrue(updated.getUpdatedAt().isAfter(firstModified));
	}
	
	@Test
	void createCharacter_InvalidUser_ReturnsBadRequest() {
	    CharacterDto dto = new CharacterDto();
	    dto.setUserId(999L); // Non-existent user
	    dto.setLevel(5);
	    dto.setCharacterClass("Mage");
	    dto.setCharacterRace("Elf");

	    ResponseEntity<String> response =
	            restTemplate.postForEntity("/api/characters", dto, String.class);

	    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	void createCharacter_ThenRetrieve_VerifyPersistence() {

	    // 1. Arrange: Create a real user (required for foreign key relationship)
	    User user = new User();
	    user.setUsername("integrationUser");
	    user.setEmail("integration@test.com");
	    user.setPassword("password");

	    userRepository.save(user);

	    // Create Character DTO linked to real user
	    CharacterDto characterDto = new CharacterDto();
	    characterDto.setUserId(user.getUserId());
	    characterDto.setCharacterClass("Mage");
	    characterDto.setCharacterRace("Elf");
	    characterDto.setLevel(5);

	    // 2. Act: Send POST request to create character
	    ResponseEntity<String> postResponse =
	            restTemplate.postForEntity("/api/characters", characterDto, String.class);

	    // 3. Assert POST response
	    assertEquals(HttpStatus.CREATED, postResponse.getStatusCode(),
	            "Character creation should return 201 CREATED");

	    // 4. Act: Send GET request to retrieve all characters
	    ResponseEntity<List> getResponse =
	            restTemplate.getForEntity("/api/characters", List.class);

	    // 5. Assert GET response
	    assertEquals(HttpStatus.OK, getResponse.getStatusCode(),
	            "GET request should return 200 OK");

	    assertNotNull(getResponse.getBody(), "Response body should not be null");

	    assertTrue(getResponse.getBody().size() > 0,
	            "Character list should contain at least one entry");

	    // 6. Optional deeper validation (if structure known)
	    // You can cast and check fields if needed
	 // Assert that the specific data we sent was saved
	    List<CharacterDto> body = getResponse.getBody();
	    // Use ObjectMapper or a Map to check the first entry
	    assertTrue(body.toString().contains("Mage"), "The retrieved character should be the 'Mage' we created.");
	}
	
	@Test
	void getCharacters_WhenEmpty_ReturnsEmptyList() {
	    ResponseEntity<List> response =
	            restTemplate.getForEntity("/api/characters", List.class);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertNotNull(response.getBody());
	    assertEquals(0, response.getBody().size());
	}
}