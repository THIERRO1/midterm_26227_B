package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Submission Entity - represents student assignment uploads
 *
 * Relationships:
 * - Many-to-One with User (student)
 * - Many-to-One with Course
 * - One-to-One with PlagiarismReport (each submission has one report)
 */
@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "file_path", nullable = false)
    private String filePath; // path to uploaded file

    @Column(name = "file_type", length = 10)
    private String fileType; // PDF, DOCX

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "status", length = 30)
    private String status; // PENDING, PROCESSED, FLAGGED

    @PrePersist
    protected void onCreate() {
        submissionDate = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    /**
     * Many-to-One: Many submissions belong to one Student (User)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private User student;

    /**
     * Many-to-One: Many submissions belong to one Course
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    /**
     * One-to-One: Each submission has exactly ONE plagiarism report
     * cascade = ALL: saving/deleting submission also saves/deletes its report
     */
    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlagiarismReport plagiarismReport;
}
