package com.tus.users.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * User Entity for the Customer of the Product
 * The user creates characters and stores then in the database
 * Cascading Prevents the need to manually save child entities, e.g. delete user, it should delete their characters
 * Updates database
 */
@Entity@Table(name = "users")@Getter@Setter@NoArgsConstructor@AllArgsConstructor@ToString
public class User extends BaseEntity {

	//Primary Key
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "user_id")
    private Long userId;
    
    //Needed Info
    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    //Optional User Data
    @Column(name = "mobile_number", length = 20, nullable = true) 
    private String mobileNumber;

    // One to Many Relationship
 /*   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Character> characters;*/
    
    
}