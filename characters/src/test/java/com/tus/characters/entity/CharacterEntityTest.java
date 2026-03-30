package com.tus.characters.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.Test;

public class CharacterEntityTest {

	@Test
	public void prePersist_SetsDate_WhenNull() {
	    Character character = new Character();
	    character.setCreationDate(null);

	    character.prePersist();

	    assertNotNull(character.getCreationDate());
	}
	
	@Test
	public void prePersist_DoesNotOverride_WhenAlreadySet() {
	    Character character = new Character();
	    LocalDate date = LocalDate.of(2020, 1, 1);
	    character.setCreationDate(date);

	    character.prePersist();

	    assertEquals(date, character.getCreationDate());
	}
}
