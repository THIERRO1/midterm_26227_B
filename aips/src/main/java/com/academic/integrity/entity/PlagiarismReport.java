package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PlagiarismReport Entity - stores similarity analysis results
 *
 * One-to-One relationship with Submission:
 * Each submission has EXACTLY ONE plagiarism report.
 * The foreign key (submission_id) lives in this table.
 */
@Entity
@Table(name = "plagiarism_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlagiarismReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Similarity percentage (0.0 to 100.0)
    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore;

    // Risk classification based on score
    @Column(name = "similarity_status", length = 30)
    private String similarityStatus; // LOW, MODERATE, HIGH, CRITICAL

    @Column(name = "matched_sources")
    private Integer matchedSources; // number of matched documents

    @Column(name = "analysis_notes", length = 1000)
    private String analysisNotes;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        // Automatically classify similarity status
        if (similarityScore != null) {
            if (similarityScore < 20) similarityStatus = "LOW";
            else if (similarityScore < 40) similarityStatus = "MODERATE";
            else if (similarityScore < 70) similarityStatus = "HIGH";
            else similarityStatus = "CRITICAL";
        }
    }

    /**
     * One-to-One: This report belongs to exactly ONE submission
     * @JoinColumn creates submission_id FK in plagiarism_reports table
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", referencedColumnName = "id", unique = true)
    private Submission submission;

    /**
     * One-to-One with MisconductCase (if plagiarism is high)
     */
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MisconductCase misconductCase;
}
