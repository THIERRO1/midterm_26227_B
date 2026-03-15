package com.academic.plagiarism.controller;

import com.academic.plagiarism.dto.ApiResponse;
import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.model.PlagiarismCase;
import com.academic.plagiarism.service.PlagiarismCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Plagiarism Cases", description = "Plagiarism case management with Many-to-Many relationships")
@CrossOrigin(origins = "*")
public class PlagiarismCaseController {

    private final PlagiarismCaseService caseService;

    @PostMapping
    @Operation(summary = "Create plagiarism case with submissions (Many-to-Many)")
    public ResponseEntity<ApiResponse<Dto.PlagiarismCaseResponse>> createCase(
            @Valid @RequestBody Dto.PlagiarismCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(caseService.createCase(request), "Case created"));
    }

    @GetMapping
    @Operation(summary = "Get all cases")
    public ResponseEntity<ApiResponse<List<Dto.PlagiarismCaseResponse>>> getAllCases() {
        return ResponseEntity.ok(ApiResponse.success(caseService.getAllCases()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get case by ID with all linked submissions")
    public ResponseEntity<ApiResponse<Dto.PlagiarismCaseResponse>> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(caseService.getCaseById(id)));
    }

    @GetMapping("/page")
    @Operation(summary = "Get cases with pagination and sorting")
    public ResponseEntity<ApiResponse<Dto.PageResponse<Dto.PlagiarismCaseResponse>>> getCasesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(caseService.getCasesPaginated(page, size, sortBy, direction)));
    }

    @PostMapping("/{caseId}/submissions/{submissionId}")
    @Operation(summary = "Add submission to case (Many-to-Many addSubmission helper)")
    public ResponseEntity<ApiResponse<Dto.PlagiarismCaseResponse>> addSubmission(
            @PathVariable Long caseId, @PathVariable Long submissionId) {
        return ResponseEntity.ok(ApiResponse.success(
                caseService.addSubmissionToCase(caseId, submissionId), "Submission added to case"));
    }

    @DeleteMapping("/{caseId}/submissions/{submissionId}")
    @Operation(summary = "Remove submission from case (Many-to-Many removeSubmission helper)")
    public ResponseEntity<ApiResponse<Dto.PlagiarismCaseResponse>> removeSubmission(
            @PathVariable Long caseId, @PathVariable Long submissionId) {
        return ResponseEntity.ok(ApiResponse.success(
                caseService.removeSubmissionFromCase(caseId, submissionId), "Submission removed from case"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update case status")
    public ResponseEntity<ApiResponse<Dto.PlagiarismCaseResponse>> updateStatus(
            @PathVariable Long id, @RequestParam PlagiarismCase.CaseStatus status) {
        return ResponseEntity.ok(ApiResponse.success(caseService.updateCaseStatus(id, status), "Status updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete case")
    public ResponseEntity<ApiResponse<Void>> deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Case deleted"));
    }
}
