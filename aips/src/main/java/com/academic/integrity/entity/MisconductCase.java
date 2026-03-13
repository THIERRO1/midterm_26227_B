package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MisconductCase Entity - tracks academic violations
 * Created when plagiarism is HIGH or CRITICAL
 */
@Entity
@Table(name = "misconduct_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MisconductCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_reference", unique = true, nullable = false, length = 30)
    private String caseReference; // e.g., "CASE-2026-001"

    @Column(name = "description", length = 1000)
    private String description;

    // PENDING, UNDER_INVESTIGATION, RESOLVED, DISMISSED
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        openedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    /**
     * One-to-One: Each misconduct case links to one PlagiarismReport
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", referencedColumnName = "id", unique = true)
    private PlagiarismReport report;
}
