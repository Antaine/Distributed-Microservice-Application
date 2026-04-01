package com.tus.users.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.tus.users.dto.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 404 Error
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResponseDto> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("404", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}	 

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
		ResponseDto response = new ResponseDto("400", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	 //500 Generic
	@ExceptionHandler(Exception.class) public ResponseEntity<ResponseDto>
	handleGeneric(Exception ex) { return
	ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) .body(new
	ResponseDto("500", "Something went wrong")); 
	}
}