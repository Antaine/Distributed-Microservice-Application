package com.tus.characters.repository;

import com.tus.characters.entity.GameCharacter;
import com.tus.characters.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * JPA Repository grants automatic methods such as save(), delete()
 * Custom method to find username added
 * List<Character> findByUser(User user); becomes SELECT * FROM characters WHERE user_id = ?
 * Pagination handled here.
 */
public interface CharactersRepository extends JpaRepository<GameCharacter, Long> {

    // For date range (all results)
    List<GameCharacter> findByCreationDateBetween(LocalDate startDate, LocalDate endDate);

    // For paginated date range
    List<GameCharacter> findByCreationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Pagination for all characters
    Page<GameCharacter> findAll(Pageable pageable);

    // Get all characters for a user with pagination
    Page<GameCharacter> findByUserId(Long userId, Pageable pageable);
    
    List<GameCharacter> findByUserId(Long userId);
    
}