package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * LocationRepository - Requirement #8: Query users by province
 *
 * The @Query with JOIN FETCH solves the N+1 problem:
 * Without JOIN FETCH: 1 query for locations + N queries for each user = N+1 queries
 * With JOIN FETCH: 1 query fetches everything at once using SQL JOIN
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Simple derived query by province code
    List<Location> findByProvinceCode(String provinceCode);

    // Case-insensitive partial search by province name
    List<Location> findByProvinceNameContainingIgnoreCase(String provinceName);

    // @Query with JOIN FETCH avoids N+1 problem - fetches Location AND User in ONE SQL query
    @Query("SELECT l FROM Location l JOIN FETCH l.user WHERE l.provinceCode = :code")
    List<Location> findByProvinceCodeWithUser(@Param("code") String code);

    @Query("SELECT l FROM Location l JOIN FETCH l.user WHERE LOWER(l.provinceName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Location> findByProvinceNameWithUser(@Param("name") String name);

    Optional<Location> findByUserId(Long userId);
}
