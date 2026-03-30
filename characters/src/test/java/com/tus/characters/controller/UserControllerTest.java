package com.tus.characters.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.tus.characters.audit.AuditAwareImpl;
import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.service.IUserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuditAwareImpl.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IUserService userService;

	@Test
	void createUser_ValidRequest_ReturnsCreated() throws Exception {

		User mockSavedUser = new User();
		mockSavedUser.setUserId(1L);
		mockSavedUser.setUsername("tester");
		mockSavedUser.setEmail("test@example.com");

		when(userService.createUser(any(UserDto.class))).thenReturn(mockSavedUser);

		String json = """
				    {
				        "username": "tester",
				        "email": "test@example.com",
				        "password": "securePassword123"
				    }
				""";

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("tester"))
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
		String json = "{\"username\": \"bob\", \"email\": \"not-an-email\", \"password\": \"pass123\"}";

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.email").exists());
	}

	@Test
	void getUser_ReturnsOk() throws Exception {
		User user = new User();
		user.setUserId(1L);
		user.setUsername("tester");
		user.setEmail("test@example.com");

		when(userService.getUserById(1L)).thenReturn(user);

		mockMvc.perform(get("/api/users/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("tester"))
				.andExpect(jsonPath("$.email").value("test@example.com"));

		verify(userService).getUserById(1L);
	}

	@Test
	void getUser_NotFound_Returns404() throws Exception {
		when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", "id", "999"));

		mockMvc.perform(get("/api/users/999")).andExpect(status().isNotFound());

		verify(userService).getUserById(999L);
	}

	@Test
	void getAllUsers_ReturnsOk() throws Exception {
		UserDto dto = new UserDto();
		dto.setUsername("tester");

		when(userService.getAllUsers()).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/users")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username").value("tester"));

		verify(userService).getAllUsers();
	}

	@Test
	void getAllUsers_EmptyList_ReturnsOk() throws Exception {
		when(userService.getAllUsers()).thenReturn(List.of());

		mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());

		verify(userService).getAllUsers();
	}

	@Test
	void deleteUser_ReturnsOk() throws Exception {
		mockMvc.perform(delete("/api/users/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.statusCode").value(200))
				.andExpect(jsonPath("$.statusMsg").value("User deleted successfully"));

		verify(userService).deleteUser(1L);
	}

	@Test
	void deleteUser_NotFound_Returns404() throws Exception {
		doThrow(new ResourceNotFoundException("User", "id", "999")).when(userService).deleteUser(999L);

		mockMvc.perform(delete("/api/users/999")).andExpect(status().isNotFound());

		verify(userService).deleteUser(999L);
	}

	@Test
	void updateUser_ReturnsOk() throws Exception {
		User updatedUser = new User();
		updatedUser.setUserId(1L);
		updatedUser.setUsername("updated");
		updatedUser.setEmail("updated@example.com");

		when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUser);

		String json = """
				    {
				        "username": "updated",
				        "email": "updated@example.com",
				        "password": "newpass123"
				    }
				""";

		mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("updated"));

		verify(userService).updateUser(eq(1L), any(UserDto.class));
	}

	@Test
	void updateUser_InvalidEmail_ReturnsBadRequest() throws Exception {
		String json = """
				    {
				        "username": "updated",
				        "email": "bad-email",
				        "password": "pass123"
				    }
				""";

		mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());

		verify(userService, never()).updateUser(any(), any());
	}
}