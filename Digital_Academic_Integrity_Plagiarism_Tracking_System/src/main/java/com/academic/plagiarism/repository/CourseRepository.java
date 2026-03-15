// CourseRepository.java
package com.academic.plagiarism.repository;

import com.academic.plagiarism.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDepartmentId(Long departmentId);
    boolean existsByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c JOIN FETCH c.department")
    List<Course> findAllWithDepartment();
}
