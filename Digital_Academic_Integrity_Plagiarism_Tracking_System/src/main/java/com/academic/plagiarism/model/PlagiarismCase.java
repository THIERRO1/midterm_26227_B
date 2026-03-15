package com.academic.plagiarism.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PlagiarismCase Entity
 * MANY-TO-MANY (owner side) with Submission
 * Many-to-One with User (investigator)
 *
 * The @JoinTable creates a join table: case_submissions
 * - joinColumns: the FK column for THIS entity (PlagiarismCase)
 * - inverseJoinColumns: the FK column for the OTHER entity (Submission)
 */
@Entity
@Table(name = "plagiarism_case")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Case number is required")
    @Column(nullable = false, unique = true)
    private String caseNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Severity severity = Severity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CaseStatus status = CaseStatus.OPEN;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * MANY-TO-MANY (owner side): Creates the join table
     * @JoinTable(name = "case_submissions") - name of the join table in DB
     * joinColumns = @JoinColumn(name = "plagiarism_case_id") - FK for PlagiarismCase
     * inverseJoinColumns = @JoinColumn(name = "submission_id") - FK for Submission
     */
    @ManyToMany
    @JoinTable(
        name = "case_submissions",
        joinColumns = @JoinColumn(name = "plagiarism_case_id"),
        inverseJoinColumns = @JoinColumn(name = "submission_id")
    )
    @ToString.Exclude
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigator_id")
    @ToString.Exclude
    private User investigator;

    /**
     * Helper methods for Many-to-Many relationship management
     */
    public void addSubmission(Submission submission) {
        this.submissions.add(submission);
        submission.getPlagiarismCases().add(this);
    }

    public void removeSubmission(Submission submission) {
        this.submissions.remove(submission);
        submission.getPlagiarismCases().remove(this);
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum CaseStatus {
        OPEN, UNDER_REVIEW, RESOLVED, DISMISSED
    }
}
