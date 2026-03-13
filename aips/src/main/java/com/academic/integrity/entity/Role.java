package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role Entity - defines user roles in the system
 * Examples: ADMIN, LECTURER, STUDENT
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName; // e.g., "ADMIN", "LECTURER", "STUDENT"

    @Column(name = "description")
    private String description;
}
