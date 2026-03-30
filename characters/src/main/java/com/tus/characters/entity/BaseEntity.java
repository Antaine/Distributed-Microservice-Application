package com.tus.characters.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Super for all Entities being created
 * Contains basic variables the User and Character Entities will need.
 * Contains Entity Listeners for Spring Data auditing
 */
@MappedSuperclass@EntityListeners(AuditingEntityListener.class)@Getter @Setter @ToString
public class BaseEntity {
	//Unchangeable Variables
    @CreatedDate@Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    @CreatedBy@Column(name = "created_by", updatable = false, nullable = false, length = 50)
    private String createdBy;

    //Modifiable Variables
    @LastModifiedDate@Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy@Column(name = "updated_by",length = 50)
    private String updatedBy;
    
    //Runs before data is inserted into database
    @PrePersist
    protected void onCreate() {createdAt = LocalDateTime.now();}

    @PreUpdate
    protected void onUpdate() {updatedAt = LocalDateTime.now();}
}