package com.tus.characters.service.impl;

import com.tus.characters.dto.UserDto;
import com.tus.characters.entity.User;
import com.tus.characters.exceptions.ResourceNotFoundException;
import com.tus.characters.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	// Generate Fake Database
	@Mock
	private UserRepository userRepository;
	// Add to Service
	@InjectMocks
	private UserServiceImpl userService;

	// Positive Testing
	@Test
	void testGetUserById_Success() {
		// Create User
		User user = new User();
		user.setUserId(1L);
		user.setEmail("test@test.com");
		// Simulate Service & Get Result
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		User result = userService.getUserById(1L);
		// Check Object is not null
		assertNotNull(result);
		assertEquals("test@test.com", result.getEmail());
		// Verify it was called once
		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void testCreateUser() {
		
		UserDto dto = new UserDto();
		dto.setEmail("test@test.com");
		User savedUser = new User();
		savedUser.setUserId(1L);
		savedUser.setEmail("test@test.com");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		User result = userService.createUser(dto);
		assertNotNull(result);
		assertEquals(1L, result.getUserId());
		assertEquals("test@test.com", result.getEmail());
		verify(userRepository, times(1)).save(any(User.class));

	}

	@Test
	void testUpdateUser_Success() {
		
		Long userId = 1L;
		UserDto userDto = new UserDto();
		userDto.setUsername("newusername");
		userDto.setEmail("newemail@test.com");
		userDto.setPassword("newpassword");
		userDto.setMobileNumber("1234567890");
		User existingUser = new User();
		existingUser.setUserId(userId);
		existingUser.setUsername("oldusername");
		existingUser.setEmail("oldemail@test.com");
		existingUser.setPassword("oldpassword");
		existingUser.setMobileNumber("0987654321");
		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		User updatedUser = userService.updateUser(userId, userDto);
		assertNotNull(updatedUser);
		assertEquals(userId, updatedUser.getUserId());
		assertEquals("newusername", updatedUser.getUsername());
		assertEquals("newemail@test.com", updatedUser.getEmail());
		assertEquals("newpassword", updatedUser.getPassword());
		assertEquals("1234567890", updatedUser.getMobileNumber());
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, times(1)).save(existingUser);
	}

	@Test
	void testDeleteUser_Success() {
		
		Long userId = 1L;
		when(userRepository.existsById(userId)).thenReturn(true);
		userService.deleteUser(userId);
		verify(userRepository, times(1)).existsById(userId);
		verify(userRepository, times(1)).deleteById(userId);
	}

	@Test
	void testGetAllUsers_Success() {
		
		User user1 = new User();
		user1.setUserId(1L);
		user1.setUsername("userOne");
		user1.setEmail("one@test.com");
		user1.setPassword("pass123");
		user1.setMobileNumber("1111111111");

		User user2 = new User();
		user2.setUserId(2L);
		user2.setUsername("userTwo");
		user2.setEmail("two@test.com");
		user2.setPassword("pass456");
		user2.setMobileNumber("2222222222");

		when(userRepository.findAll()).thenReturn(List.of(user1, user2));
		List<UserDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).getUserId());
		assertEquals("userOne", result.get(0).getUsername());
		assertEquals("one@test.com", result.get(0).getEmail());
		assertEquals("pass123", result.get(0).getPassword());
		assertEquals("1111111111", result.get(0).getMobileNumber());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testGetAllUsers_EmptyList() {

		when(userRepository.findAll()).thenReturn(List.of());
		List<UserDto> result = userService.getAllUsers();
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(userRepository, times(1)).findAll();
	}

	void testDeleteUser_NotFound_DoesNotThrow() {
		
		Long userId = 2L;
		when(userRepository.existsById(userId)).thenReturn(false);
		assertDoesNotThrow(() -> userService.deleteUser(userId));
		verify(userRepository, times(1)).existsById(userId);
		verify(userRepository, never()).deleteById(any());
	}

	@Test
	void testDeleteUser_Twice_ThrowsOnSecondCall() {
		
		Long userId = 1L;
		when(userRepository.existsById(userId)).thenReturn(true);
		userService.deleteUser(userId);
		verify(userRepository, times(1)).deleteById(userId);
		when(userRepository.existsById(userId)).thenReturn(false);
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.deleteUser(userId));
		assertEquals("User not found with userId : '1'", ex.getMessage());
	}

	// Negative Testing

	// AI Example 4
	@Test
	void testGetUserById_NotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
		assertEquals("User not found with userId : '1'", ex.getMessage());
		verify(userRepository).findById(1L);
	}

	@Test
	void testCreateUser_MissingEmail() {
		
		UserDto dto = new UserDto();
		dto.setEmail(null);
		when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Email is required"));
		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
		assertEquals("Email is required", exception.getMessage());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testUpdateUser_NotFound() {

		Long userId = 1L;
		UserDto userDto = new UserDto();
		userDto.setUsername("newusername");
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, userDto));
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testDeleteUser_NotFound() {
		Long userId = 1L;
		when(userRepository.existsById(userId)).thenReturn(false);
		assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
		verify(userRepository, times(1)).existsById(userId);
		verify(userRepository, never()).deleteById(any());
	}

	@Test
	void testCreateUser_DuplicateEmail() {
		
		UserDto dto = new UserDto();
		dto.setEmail("test@test.com");
		when(userRepository.save(any())).thenThrow(new RuntimeException("Duplicate email"));
		assertThrows(RuntimeException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_CallsUserRepositorySave() {
		
		UserDto dto = new UserDto();
		dto.setEmail("test@test.com");
		when(userRepository.save(any(User.class))).thenReturn(new User());
		userService.createUser(dto);
		verify(userRepository, times(1)).save(any(User.class));
	}

}