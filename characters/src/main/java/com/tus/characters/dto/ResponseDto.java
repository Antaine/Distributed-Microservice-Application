package com.tus.characters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Basic variables for status codes and status messages
 * Used for Success Responses
 *  */
@Data@AllArgsConstructor
public class ResponseDto {
	
	// e.g., "200" or "201"
    private String statusCode; 
    // e.g., "User created successfully"
    private String statusMsg;  
}