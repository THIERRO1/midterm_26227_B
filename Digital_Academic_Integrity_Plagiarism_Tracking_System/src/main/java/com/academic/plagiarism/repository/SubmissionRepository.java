package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SubmissionRepository
 * Spring Data JPA's Page<T> and Pageable support built-in pagination and sorting
 * Just pass a Pageable object and JPA handles LIMIT/OFFSET + COUNT queries automatically
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Pageable parameter enables pagination + sorting in ONE method
    Page<Submission> findAll(Pageable pageable);

    Page<Submission> findByUserId(Long userId, Pageable pageable);

    Page<Submission> findByCourseId(Long courseId, Pageable pageable);

    Page<Submission> findByStatus(Submission.SubmissionStatus status, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.similarityScore >= :threshold")
    Page<Submission> findBySimilarityScoreGreaterThanEqual(Double threshold, Pageable pageable);

    List<Submission> findByUserId(Long userId);

    long countByStatus(Submission.SubmissionStatus status);
}
