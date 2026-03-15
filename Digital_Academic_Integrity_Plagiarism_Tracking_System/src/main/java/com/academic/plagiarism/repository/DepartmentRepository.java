// ========================= DepartmentRepository.java =========================
package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByCode(String code);
    boolean existsByName(String name);
    Optional<Department> findByCode(String code);
}
