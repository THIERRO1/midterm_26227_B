package com.academic.plagiarism.controller;

import com.academic.plagiarism.dto.ApiResponse;
import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.model.Submission;
import com.academic.plagiarism.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Submission management with Sorting and Pagination")
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    @Operation(summary = "Create submission")
    public ResponseEntity<ApiResponse<Dto.SubmissionResponse>> createSubmission(
            @Valid @RequestBody Dto.SubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(submissionService.createSubmission(request), "Submission created"));
    }

    @GetMapping
    @Operation(summary = "Get all submissions (no pagination)")
    public ResponseEntity<ApiResponse<List<Dto.SubmissionResponse>>> getAllSubmissions() {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getAllSubmissions()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get submission by ID")
    public ResponseEntity<ApiResponse<Dto.SubmissionResponse>> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getSubmissionById(id)));
    }

    /**
     * REQUIREMENT #3: Sorting only
     * GET /api/submissions/sorted?sortBy=similarityScore&direction=desc
     */
    @GetMapping("/sorted")
    @Operation(summary = "Get all submissions sorted (Requirement #3 - Sorting)")
    public ResponseEntity<ApiResponse<List<Dto.SubmissionResponse>>> getSorted(
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getAllSubmissionsSorted(sortBy, direction),
                "Sorted by " + sortBy + " " + direction));
    }

    /**
     * REQUIREMENT #3: Pagination only
     * GET /api/submissions/paginated?page=0&size=5
     */
    @GetMapping("/paginated")
    @Operation(summary = "Get submissions with pagination (Requirement #3 - Pagination)")
    public ResponseEntity<ApiResponse<Dto.PageResponse<Dto.SubmissionResponse>>> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getSubmissionsPaginated(page, size),
                "Page " + page + " of results"));
    }

    /**
     * REQUIREMENT #3: Pagination + Sorting combined
     * GET /api/submissions/page?page=0&size=5&sortBy=similarityScore&direction=desc
     */
    @GetMapping("/page")
    @Operation(summary = "Get submissions with pagination AND sorting combined (Requirement #3 - Both)")
    public ResponseEntity<ApiResponse<Dto.PageResponse<Dto.SubmissionResponse>>> getPagedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getSubmissionsWithPaginationAndSorting(page, size, sortBy, direction),
                "Page " + page + ", sorted by " + sortBy + " " + direction));
    }

    /**
     * Filter by status with pagination
     * GET /api/submissions/status/FLAGGED?page=0&size=5
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get submissions by status with pagination")
    public ResponseEntity<ApiResponse<Dto.PageResponse<Dto.SubmissionResponse>>> getByStatus(
            @PathVariable Submission.SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getSubmissionsByStatus(status, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update submission")
    public ResponseEntity<ApiResponse<Dto.SubmissionResponse>> updateSubmission(
            @PathVariable Long id, @Valid @RequestBody Dto.SubmissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.updateSubmission(id, request), "Updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete submission")
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}
