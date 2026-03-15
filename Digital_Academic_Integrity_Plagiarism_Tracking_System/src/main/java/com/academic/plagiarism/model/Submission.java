package com.academic.plagiarism.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Submission Entity
 * Many-to-One with User and Course
 * Many-to-Many with PlagiarismCase (inverse side)
 */
@Entity
@Table(name = "submission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Double similarityScore = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Course course;

    /**
     * MANY-TO-MANY (inverse side): mappedBy points to 'submissions' field in PlagiarismCase
     * This side does NOT own the relationship - it just references it
     */
    @ManyToMany(mappedBy = "submissions")
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<PlagiarismCase> plagiarismCases = new ArrayList<>();

    public enum SubmissionStatus {
        PENDING, CLEAN, FLAGGED, PLAGIARIZED
    }
}
