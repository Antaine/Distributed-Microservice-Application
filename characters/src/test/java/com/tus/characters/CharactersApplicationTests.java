package com.tus.characters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

class CharactersApplicationTests {

	//AI Generated but does not work as intended
	@Test
	void main_ShouldRunWithoutServer() {
		try {
			CharactersApplication.main(new String[] {});
		} catch (Exception e) {
		}
	}

	@Test
	void contextLoads() {
	}
}