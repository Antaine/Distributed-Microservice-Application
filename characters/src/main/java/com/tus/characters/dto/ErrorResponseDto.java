package com.tus.characters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

/**
 * Structures Error Message Responses
 * Returns an JSON format useful information
 * Example
 * {
 * 		"apiPath": "/api/users",
 *		"errorCode": "400",
 * 		"errorMessage": "Email invalid",
 *		"errorTime": "2026-03-03T12:00"
	}
 */
@Data@AllArgsConstructor
public class ErrorResponseDto {

	// The endpoint that caused the error
    private String apiPath;  
    // HTTP status code
    private HttpStatus errorCode; 
    // Detailed error message
    private String errorMessage;      
    // Timestamp of the error
    private LocalDateTime errorTime;  
}