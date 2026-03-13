package com.academic.integrity.repository;

import com.academic.integrity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Spring Data JPA repository for User entity
 *
 * Spring Data JPA automatically generates SQL queries from method names.
 * No implementation needed — Spring handles it.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (for login)
     * Generated SQL: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * existsBy() - checks if a user with this email already exists
     * Used to prevent duplicate registrations
     * Returns true/false without loading the full entity (efficient)
     *
     * Generated SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);

    /**
     * existsBy() for student ID
     */
    boolean existsByStudentId(String studentId);

    /**
     * Retrieve all users from a given province using province CODE
     * This is a JPQL query (uses entity field names, not column names)
     *
     * @param provinceCode - the code of the province (e.g., "KGL")
     */
    @Query("SELECT u FROM User u WHERE u.province.provinceCode = :provinceCode")
    List<User> findUsersByProvinceCode(@Param("provinceCode") String provinceCode);

    /**
     * Retrieve all users from a given province using province NAME
     * Case-insensitive search using LOWER()
     *
     * @param provinceName - the full name of the province (e.g., "Kigali")
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.province.provinceName) LIKE LOWER(CONCAT('%', :provinceName, '%'))")
    List<User> findUsersByProvinceName(@Param("provinceName") String provinceName);

    /**
     * Find users by role name with Pagination support
     *
     * Pageable parameter enables:
     * - Pagination (page number, page size)
     * - Sorting (by any field, ascending or descending)
     *
     * Usage: PageRequest.of(0, 10, Sort.by("fullName").ascending())
     */
    Page<User> findByRoleRoleName(String roleName, Pageable pageable);

    /**
     * Search users by name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Count users by role
     */
    long countByRoleRoleName(String roleName);
}
