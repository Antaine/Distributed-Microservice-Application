package com.tus.characters.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.LocalDate;

/**
 * Simple Character Entity that is mapped to a User Entity
 * Many Characters to One User reality
 * Lazy FetchType to only load object when need, e.g. .getUser()
 * Includes Character class, race, level and creation date
 */
@Entity@Table(name = "characters")@Getter@Setter@NoArgsConstructor@AllArgsConstructor@ToString
public class Character extends BaseEntity {

	//Primary Key
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)@Column(name = "character_id")
    private Long characterId;
    
    @Column(name = "character_class", nullable = false, length = 50)
    private String characterClass;

    @Column(name = "character_race", nullable = false, length = 50)
    private String characterRace;
    
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 20, message = "Level cannot exceed 20")
    @Column(nullable = false)
    private int level;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    // Many characters belong to one user, Foreign KEY to User table, Loaded only when needed
    @ManyToOne(fetch = FetchType.LAZY)@JoinColumn(name = "user_id", nullable = false)
    private User user;

   //Sets Creation Date to Today
    @PrePersist
    public void prePersist() {if (creationDate == null) {creationDate = LocalDate.now();}}
}