package com.academic.integrity.repository;

import com.academic.integrity.entity.MisconductCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MisconductCaseRepository extends JpaRepository<MisconductCase, Long> {

    boolean existsByCaseReference(String caseReference);

    Page<MisconductCase> findAll(Pageable pageable);

    Page<MisconductCase> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);
}
