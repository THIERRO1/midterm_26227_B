package com.academic.integrity.repository;

import com.academic.integrity.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    /** existsBy() - prevents duplicate course codes */
    boolean existsByCourseCode(String courseCode);

    Page<Course> findAll(Pageable pageable);

    Page<Course> findBySemester(String semester, Pageable pageable);
}
