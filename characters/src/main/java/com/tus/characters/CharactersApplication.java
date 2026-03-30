package com.tus.characters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CharactersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharactersApplication.class, args);
	}

}
