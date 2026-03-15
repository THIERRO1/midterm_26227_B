package com.academic.plagiarism.controller;

import com.academic.plagiarism.dto.ApiResponse;
import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.service.CourseService;
import com.academic.plagiarism.service.DepartmentService;
import com.academic.plagiarism.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ==================== DEPARTMENT CONTROLLER ====================
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management (One-to-Many with Courses)")
@CrossOrigin(origins = "*")
class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<ApiResponse<Dto.DepartmentResponse>> create(@Valid @RequestBody Dto.DepartmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(departmentService.createDepartment(req), "Department created"));
    }

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<ApiResponse<List<Dto.DepartmentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getAllDepartments()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<Dto.DepartmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getDepartmentById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<ApiResponse<Dto.DepartmentResponse>> update(
            @PathVariable Long id, @Valid @RequestBody Dto.DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.updateDepartment(id, req), "Updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }
}

// ==================== COURSE CONTROLLER ====================
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management (Many-to-One with Department)")
@CrossOrigin(origins = "*")
class CourseController {
    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create course (links to Department - One-to-Many)")
    public ResponseEntity<ApiResponse<Dto.CourseResponse>> create(@Valid @RequestBody Dto.CourseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.createCourse(req), "Course created"));
    }

    @GetMapping
    @Operation(summary = "Get all courses with department info")
    public ResponseEntity<ApiResponse<List<Dto.CourseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Dto.CourseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(id)));
    }

    @GetMapping("/department/{deptId}")
    @Operation(summary = "Get courses by department (One-to-Many demo)")
    public ResponseEntity<ApiResponse<List<Dto.CourseResponse>>> getByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCoursesByDepartment(deptId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Dto.CourseResponse>> update(
            @PathVariable Long id, @Valid @RequestBody Dto.CourseRequest req) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(id, req), "Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }
}

// ==================== STATS CONTROLLER ====================
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Dashboard Stats", description = "Dashboard statistics")
@CrossOrigin(origins = "*")
class StatsController {
    private final StatsService statsService;

    @GetMapping
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<Dto.DashboardStats>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(statsService.getDashboardStats(), "Dashboard stats"));
    }
}
