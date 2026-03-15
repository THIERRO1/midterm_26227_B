package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.PlagiarismCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlagiarismCaseRepository extends JpaRepository<PlagiarismCase, Long> {

    Optional<PlagiarismCase> findByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String caseNumber);

    Page<PlagiarismCase> findAll(Pageable pageable);

    List<PlagiarismCase> findByStatus(PlagiarismCase.CaseStatus status);

    List<PlagiarismCase> findBySeverity(PlagiarismCase.Severity severity);

    @Query("SELECT pc FROM PlagiarismCase pc JOIN FETCH pc.submissions WHERE pc.id = :id")
    Optional<PlagiarismCase> findByIdWithSubmissions(Long id);

    long countByStatus(PlagiarismCase.CaseStatus status);
}
