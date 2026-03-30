package com.tus.characters.controller;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tus.characters.audit.AuditAwareImpl;
import com.tus.characters.dto.CharacterDto;
import com.tus.characters.entity.Character;
import com.tus.characters.entity.User;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.service.ICharacterService;

@WebMvcTest(controllers = CharactersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuditAwareImpl.class)
class CharactersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ICharacterService characterService;

	@Test
	void getAllCharacters_ReturnsOk() throws Exception {
		CharacterDto dto = new CharacterDto();
		dto.setCharacterId(1L);
		dto.setLevel(5);

		when(characterService.getAllCharacters()).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/characters")).andExpect(status().isOk()).andExpect(jsonPath("$[0].level").value(5));
	}

	@Test
	void createCharacter_InvalidLevel_ReturnsBadRequest() throws Exception {
		String json = """
				    {
				        "userId": 1,
				        "level": 25,
				        "characterClass": "Mage",
				        "characterRace": "Elf"
				    }
				""";

		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createCharacter_Valid_ReturnsCreated() throws Exception {

		String json = """
				    {
				        "userId": 1,
				        "level": 10,
				        "characterClass": "Mage",
				        "characterRace": "Elf"
				    }
				""";

		CharacterDto responseDto = new CharacterDto();
		responseDto.setCharacterId(1L);
		responseDto.setLevel(10);

		when(characterService.createCharacter(any())).thenReturn(responseDto);
		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.statusCode").value("201"))
				.andExpect(jsonPath("$.statusMsg").value("Character created successfully"));
	}

	@Test
	void createCharacter_LevelTooHigh_Returns400() throws Exception {

		String json = "{\"level\": 25, \"characterClass\": \"Mage\", \"characterRace\": \"Elf\"}";
		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getCharacterById_ReturnsOk() throws Exception {
		CharacterDto dto = new CharacterDto();
		dto.setCharacterId(1L);
		dto.setCharacterClass("Warrior");
		dto.setLevel(10);

		when(characterService.getCharacterById(1L)).thenReturn(dto);
		mockMvc.perform(get("/api/characters/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.characterClass").value("Warrior")).andExpect(jsonPath("$.level").value(10));
	}

	@Test
	void createCharacter_BoundaryLevel1_ReturnsCreated() throws Exception {

		String json = "{\"userId\": 1, \"level\": 1, \"characterClass\": \"Fighter\", \"characterRace\": \"Orc\"}";
		when(characterService.createCharacter(any())).thenReturn(new CharacterDto());

		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	void createCharacter_InvalidFields_ReturnsDetailedErrors() throws Exception {
		String json = """
				    {
				        "userId": 1,
				        "level": 25,
				        "characterClass": "",
				        "characterRace": "Elf"
				    }
				""";

		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.level").value("must be less than or equal to 20"))
				.andExpect(jsonPath("$.characterClass").value("Character class cannot be null or empty"));
	}

	@Test
	void getCharacter_NotFound_Returns404() throws Exception {
		when(characterService.getCharacterById(999L))
				.thenThrow(new ResourceNotFoundException("Character", "id", "999"));

		mockMvc.perform(get("/api/characters/999")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.statusMsg").exists());
	}

	@Test
	void createCharacter_LevelZero_ReturnsBadRequest() throws Exception {
		String json = "{\"userId\": 1, \"level\": 0, \"characterClass\": \"Mage\", \"characterRace\": \"Human\"}";

		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.level").value("must be greater than or equal to 1"));
	}

	@Test
	void getCharacters_InvalidPagination_ReturnsBadRequest() throws Exception {
		mockMvc.perform(get("/api/characters").param("page", "-1") // invalid
				.param("size", "5").param("sortField", "level").param("sortDir", "desc")).andExpect(status().isOk());
	}

	@Test
	void getAllCharacters_EmptyList_ReturnsOk() throws Exception {
		when(characterService.getAllCharacters()).thenReturn(List.of());

		mockMvc.perform(get("/api/characters")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void getAllCharacters_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
		when(characterService.getAllCharacters()).thenThrow(new RuntimeException("Unexpected error"));

		mockMvc.perform(get("/api/characters")).andExpect(status().isInternalServerError());
	}

	@Test
	void getCharacters_InvalidSortDirection_ReturnsOk_NoValidationApplied() throws Exception {
		mockMvc.perform(get("/api/characters").param("page", "0").param("size", "5").param("sortField", "level")
				.param("sortDir", "invalid")).andExpect(status().isOk());
	}

	@Test
	void createCharacter_MissingUserId_ReturnsBadRequest() throws Exception {
		String json = """
				    {
				        "level": 5,
				        "characterClass": "Mage",
				        "characterRace": "Elf"
				    }
				""";

		mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteCharacter_ReturnsOk() throws Exception {
		mockMvc.perform(delete("/api/characters/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.statusCode").value("200"))
				.andExpect(jsonPath("$.statusMsg").value("Character deleted successfully"));

		verify(characterService).deleteCharacter(1L);
	}

	@Test
	void updateCharacter_Valid_ReturnsOk() throws Exception {
		String json = """
				    {
				        "userId": 1,
				        "level": 10,
				        "characterClass": "Mage",
				        "characterRace": "Elf"
				    }
				""";

		mockMvc.perform(put("/api/characters/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.statusCode").value("200"))
				.andExpect(jsonPath("$.statusMsg").value("Character updated successfully"));

		verify(characterService).updateCharacter(any(), any());
	}

	@Test
	void getCharactersByUserId_ReturnsOk() throws Exception {
		CharacterDto dto = new CharacterDto();
		dto.setCharacterId(1L);
		when(characterService.getCharactersByUserId(1L)).thenReturn(List.of(dto));
		mockMvc.perform(get("/api/characters/user/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].characterId").value(1));
	}

	@Test
	void getCharactersPage_ReturnsPagedResponse() throws Exception {
		Page<CharacterDto> page = new PageImpl<>(List.of(new CharacterDto()), PageRequest.of(0, 5), 1);
		when(characterService.getCharactersPage(0, 5, "characterId", "asc")).thenReturn(page);

		mockMvc.perform(get("/api/characters/page")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.pageNumber").value(0))
				.andExpect(jsonPath("$.pageSize").value(5));
		verify(characterService).getCharactersPage(0, 5, "characterId", "asc");
	}

	@Test
	void getCharactersPage_Empty_ReturnsEmptyContent() throws Exception {
		Page<CharacterDto> emptyPage = Page.empty();
		when(characterService.getCharactersPage(anyInt(), anyInt(), any(), any())).thenReturn(emptyPage);
		mockMvc.perform(get("/api/characters/page")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty());
	}

	
}