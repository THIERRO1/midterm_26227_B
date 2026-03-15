package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.DuplicateResourceException;
import com.academic.plagiarism.exception.ResourceNotFoundException;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    public Dto.DepartmentResponse createDepartment(Dto.DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code)) {
            throw new DuplicateResourceException("Department code '" + request.code + "' already exists");
        }
        Department dept = Department.builder()
                .name(request.name)
                .code(request.code.toUpperCase())
                .description(request.description)
                .build();
        return mapToResponse(departmentRepository.save(dept));
    }

    @Transactional(readOnly = true)
    public List<Dto.DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.DepartmentResponse getDepartmentById(Long id) {
        return mapToResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id)));
    }

    public Dto.DepartmentResponse updateDepartment(Long id, Dto.DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
        dept.setName(request.name);
        dept.setCode(request.code.toUpperCase());
        dept.setDescription(request.description);
        return mapToResponse(departmentRepository.save(dept));
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) throw new ResourceNotFoundException("Department", id);
        departmentRepository.deleteById(id);
    }

    private Dto.DepartmentResponse mapToResponse(Department d) {
        int courseCount = courseRepository.findByDepartmentId(d.getId()).size();
        return Dto.DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .courseCount(courseCount)
                .build();
    }
}
