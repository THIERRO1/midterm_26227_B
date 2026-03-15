package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.DuplicateResourceException;
import com.academic.plagiarism.exception.ResourceNotFoundException;
import com.academic.plagiarism.model.Course;
import com.academic.plagiarism.model.Department;
import com.academic.plagiarism.repository.CourseRepository;
import com.academic.plagiarism.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public Dto.CourseResponse createCourse(Dto.CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.courseCode)) {
            throw new DuplicateResourceException("Course code '" + request.courseCode + "' already exists");
        }
        Department dept = departmentRepository.findById(request.departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId));

        Course course = Course.builder()
                .title(request.title)
                .courseCode(request.courseCode.toUpperCase())
                .credits(request.credits)
                .department(dept)
                .build();

        // REQUIREMENT #5: One-to-Many - use helper method to maintain relationship
        dept.addCourse(course);

        return mapToResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public List<Dto.CourseResponse> getAllCourses() {
        return courseRepository.findAllWithDepartment().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.CourseResponse getCourseById(Long id) {
        return mapToResponse(courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id)));
    }

    @Transactional(readOnly = true)
    public List<Dto.CourseResponse> getCoursesByDepartment(Long deptId) {
        return courseRepository.findByDepartmentId(deptId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Dto.CourseResponse updateCourse(Long id, Dto.CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        Department dept = departmentRepository.findById(request.departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId));
        course.setTitle(request.title);
        course.setCourseCode(request.courseCode.toUpperCase());
        course.setCredits(request.credits);
        course.setDepartment(dept);
        return mapToResponse(courseRepository.save(course));
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) throw new ResourceNotFoundException("Course", id);
        courseRepository.deleteById(id);
    }

    private Dto.CourseResponse mapToResponse(Course c) {
        return Dto.CourseResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .courseCode(c.getCourseCode())
                .credits(c.getCredits())
                .departmentId(c.getDepartment() != null ? c.getDepartment().getId() : null)
                .departmentName(c.getDepartment() != null ? c.getDepartment().getName() : null)
                .build();
    }
}
