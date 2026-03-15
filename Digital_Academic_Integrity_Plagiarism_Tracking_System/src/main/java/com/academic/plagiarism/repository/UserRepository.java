// ========================= UserRepository.java =========================
package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 *
 * existsByEmail() and existsByStudentId() use Spring Data JPA's
 * method name derivation to auto-generate: SELECT COUNT(*) > 0 FROM user WHERE email = ?
 * Much more efficient than fetching the full entity just to check existence.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // CHECK 7: existsBy() methods - prevent duplicate email/studentId
    boolean existsByEmail(String email);

    boolean existsByStudentId(String studentId);

    // For UPDATE validation: check if email belongs to ANOTHER user (not self)
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByStudentIdAndIdNot(String studentId, Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentId(String studentId);
}
