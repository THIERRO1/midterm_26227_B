package com.academic.integrity.repository;

import com.academic.integrity.entity.PlagiarismReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlagiarismReportRepository extends JpaRepository<PlagiarismReport, Long> {

    Optional<PlagiarismReport> findBySubmissionId(Long submissionId);

    boolean existsBySubmissionId(Long submissionId);

    List<PlagiarismReport> findBySimilarityStatus(String status);

    Page<PlagiarismReport> findAll(Pageable pageable);

    /** Get reports with similarity above threshold */
    @Query("SELECT pr FROM PlagiarismReport pr WHERE pr.similarityScore >= :threshold ORDER BY pr.similarityScore DESC")
    List<PlagiarismReport> findHighSimilarityReports(double threshold);

    long countBySimilarityStatus(String status);
}
