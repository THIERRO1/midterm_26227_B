package com.academic.plagiarism.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Course Entity
 * Many-to-One with Department (many courses belong to one department)
 * One-to-Many with Submission
 */
@Entity
@Table(name = "course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Course code is required")
    @Column(nullable = false, unique = true, length = 10)
    private String courseCode;

    @Column
    private Integer credits;

    /**
     * MANY-TO-ONE: Many courses belong to one department
     * @JoinColumn creates a foreign key column 'department_id' in the course table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @ToString.Exclude
    @NotNull(message = "Department is required")
    private Department department;

    /**
     * ONE-TO-MANY: One course has many submissions
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();
}
