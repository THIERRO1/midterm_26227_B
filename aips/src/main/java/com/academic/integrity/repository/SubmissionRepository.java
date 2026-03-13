package com.academic.integrity.repository;

import com.academic.integrity.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SubmissionRepository - handles all database operations for Submission
 *
 * Demonstrates:
 * - Pagination (Page<Submission> with Pageable)
 * - Sorting (via Pageable with Sort)
 * - Custom JPQL queries
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Get all submissions with PAGINATION and SORTING
     *
     * Pagination example:
     * PageRequest.of(0, 10) → page 1, 10 records
     * PageRequest.of(1, 10) → page 2, next 10 records
     *
     * Sorting example:
     * Sort.by("submissionDate").descending() → newest first
     * Sort.by("title").ascending() → alphabetical order
     *
     * Combined:
     * PageRequest.of(0, 10, Sort.by("submissionDate").descending())
     */
    Page<Submission> findAll(Pageable pageable);

    /**
     * Get submissions by student with pagination
     */
    Page<Submission> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Get submissions by course with pagination
     */
    Page<Submission> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Get submissions by status with pagination
     */
    Page<Submission> findByStatus(String status, Pageable pageable);

    /**
     * Search submissions by title
     */
    @Query("SELECT s FROM Submission s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Submission> searchByTitle(@Param("title") String title, Pageable pageable);

    /**
     * Get all submissions ordered by plagiarism similarity score (highest first)
     * Useful for finding most suspicious submissions quickly
     */
    @Query("SELECT s FROM Submission s LEFT JOIN s.plagiarismReport pr ORDER BY pr.similarityScore DESC")
    List<Submission> findAllOrderBySimilarityScore();

    /**
     * Count submissions by status
     */
    long countByStatus(String status);

    /**
     * Count total submissions for a student
     */
    long countByStudentId(Long studentId);
}
