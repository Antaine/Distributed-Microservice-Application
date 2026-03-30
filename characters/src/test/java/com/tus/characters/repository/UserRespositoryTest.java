package com.tus.characters.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import com.tus.characters.entity.Character;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.tus.characters.audit.AuditAwareImpl;
import com.tus.characters.entity.User;

@DataJpaTest
@Import(AuditAwareImpl.class)
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class UserRespositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private CharactersRepository characterRepository;

	@Test
    void createUser_Success() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("password");
        User saved = entityManager.persistAndFlush(user);

        assertNotNull(saved.getUserId(), "User should be saved and have an ID");
    }
	
	@Test
    void testDeleteUser_CascadesToDeleteCharacters() {
        User user = new User();
        user.setUsername("cascadeUser");
        user.setEmail("cascade@test.com");
        user.setPassword("password");
        user = entityManager.persistAndFlush(user);

	     Character c1 = new Character();
	     c1.setLevel(1);
	     c1.setCharacterClass("Warrior");
	     c1.setCharacterRace("Human");
	     c1.setUser(user);
	     entityManager.persistAndFlush(c1);

        entityManager.remove(user);
        entityManager.flush();
        entityManager.clear(); 

        List<Character> remainingChars = characterRepository.findByUser(user);
        assertTrue(remainingChars.isEmpty(), "Characters should be deleted when their User is deleted");
    }
}
