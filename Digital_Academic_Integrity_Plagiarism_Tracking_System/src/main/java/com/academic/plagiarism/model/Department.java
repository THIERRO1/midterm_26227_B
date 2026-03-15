package com.academic.plagiarism.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Department Entity - One-to-Many relationship with Course
 * One department can have many courses
 */
@Entity
@Table(name = "department")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Department code is required")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * ONE-TO-MANY: One department has many courses
     * mappedBy = "department" refers to the 'department' field in Course entity
     * cascade = ALL means any operation on Department cascades to its Courses
     * orphanRemoval = true means if a Course is removed from this list, it gets deleted from DB
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Prevents infinite recursion in JSON serialization
    @ToString.Exclude
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    /**
     * Helper method to maintain bidirectional relationship consistency
     */
    public void addCourse(Course course) {
        courses.add(course);
        course.setDepartment(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.setDepartment(null);
    }
}
