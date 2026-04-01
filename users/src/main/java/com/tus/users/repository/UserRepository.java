package com.tus.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tus.users.entity.User;

import java.util.Optional;

/**
 * JPA Repository grants automatic methods such as save(), delete()
 * Custom method to find username added
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}