package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Course Entity - represents academic courses/modules
 *
 * Relationships:
 * - Many-to-Many with Users (Students enrolled in courses)
 * - One-to-Many with Submissions
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode; // e.g., "CS301"

    @Column(name = "course_name", nullable = false, length = 150)
    private String courseName; // e.g., "Database Systems"

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "credit_hours")
    private Integer creditHours;

    @Column(name = "semester", length = 20)
    private String semester; // e.g., "2026-S1"

    /**
     * Many-to-Many inverse side
     * mappedBy = "courses" in User entity
     */
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<User> enrolledStudents = new HashSet<>();

    /**
     * One-to-Many: One course can have many submissions
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Submission> submissions;
}
