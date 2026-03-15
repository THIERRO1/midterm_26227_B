package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.DuplicateResourceException;
import com.academic.plagiarism.exception.ResourceNotFoundException;
import com.academic.plagiarism.model.PlagiarismCase;
import com.academic.plagiarism.model.Submission;
import com.academic.plagiarism.model.User;
import com.academic.plagiarism.repository.PlagiarismCaseRepository;
import com.academic.plagiarism.repository.SubmissionRepository;
import com.academic.plagiarism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlagiarismCaseService {

    private final PlagiarismCaseRepository caseRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public Dto.PlagiarismCaseResponse createCase(Dto.PlagiarismCaseRequest request) {
        if (caseRepository.existsByCaseNumber(request.caseNumber)) {
            throw new DuplicateResourceException("Case number '" + request.caseNumber + "' already exists");
        }

        User investigator = null;
        if (request.investigatorId != null) {
            investigator = userRepository.findById(request.investigatorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.investigatorId));
        }

        PlagiarismCase plagiarismCase = PlagiarismCase.builder()
                .caseNumber(request.caseNumber)
                .description(request.description)
                .severity(request.severity != null ? request.severity : PlagiarismCase.Severity.MEDIUM)
                .status(request.status != null ? request.status : PlagiarismCase.CaseStatus.OPEN)
                .investigator(investigator)
                .submissions(new ArrayList<>())
                .build();

        // REQUIREMENT #4: Many-to-Many - add submissions via helper method
        if (request.submissionIds != null && !request.submissionIds.isEmpty()) {
            for (Long submissionId : request.submissionIds) {
                Submission submission = submissionRepository.findById(submissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
                plagiarismCase.addSubmission(submission);
            }
        }

        return mapToResponse(caseRepository.save(plagiarismCase));
    }

    @Transactional(readOnly = true)
    public List<Dto.PlagiarismCaseResponse> getAllCases() {
        return caseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.PlagiarismCaseResponse getCaseById(Long id) {
        PlagiarismCase pc = caseRepository.findByIdWithSubmissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlagiarismCase", id));
        return mapToResponse(pc);
    }

    @Transactional(readOnly = true)
    public Dto.PageResponse<Dto.PlagiarismCaseResponse> getCasesPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<PlagiarismCase> pageResult = caseRepository.findAll(PageRequest.of(page, size, sort));
        return Dto.PageResponse.<Dto.PlagiarismCaseResponse>builder()
                .content(pageResult.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .hasNext(pageResult.hasNext())
                .hasPrevious(pageResult.hasPrevious())
                .build();
    }

    public Dto.PlagiarismCaseResponse addSubmissionToCase(Long caseId, Long submissionId) {
        PlagiarismCase pc = caseRepository.findByIdWithSubmissions(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("PlagiarismCase", caseId));
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
        pc.addSubmission(submission);
        return mapToResponse(caseRepository.save(pc));
    }

    public Dto.PlagiarismCaseResponse removeSubmissionFromCase(Long caseId, Long submissionId) {
        PlagiarismCase pc = caseRepository.findByIdWithSubmissions(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("PlagiarismCase", caseId));
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
        pc.removeSubmission(submission);
        return mapToResponse(caseRepository.save(pc));
    }

    public Dto.PlagiarismCaseResponse updateCaseStatus(Long id, PlagiarismCase.CaseStatus status) {
        PlagiarismCase pc = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlagiarismCase", id));
        pc.setStatus(status);
        return mapToResponse(caseRepository.save(pc));
    }

    public void deleteCase(Long id) {
        if (!caseRepository.existsById(id)) throw new ResourceNotFoundException("PlagiarismCase", id);
        caseRepository.deleteById(id);
    }

    // Inlined mapper — avoids circular dependency with SubmissionService
    private Dto.SubmissionResponse mapSubmission(Submission s) {
        return Dto.SubmissionResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .content(s.getContent())
                .similarityScore(s.getSimilarityScore())
                .status(s.getStatus())
                .submittedAt(s.getSubmittedAt())
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .userName(s.getUser() != null ? s.getUser().getFullName() : null)
                .courseId(s.getCourse() != null ? s.getCourse().getId() : null)
                .courseName(s.getCourse() != null ? s.getCourse().getTitle() : null)
                .build();
    }

    private Dto.PlagiarismCaseResponse mapToResponse(PlagiarismCase pc) {
        List<Dto.SubmissionResponse> submissions = pc.getSubmissions() != null
                ? pc.getSubmissions().stream().map(this::mapSubmission).collect(Collectors.toList())
                : List.of();

        return Dto.PlagiarismCaseResponse.builder()
                .id(pc.getId())
                .caseNumber(pc.getCaseNumber())
                .description(pc.getDescription())
                .severity(pc.getSeverity())
                .status(pc.getStatus())
                .createdAt(pc.getCreatedAt())
                .investigatorName(pc.getInvestigator() != null ? pc.getInvestigator().getFullName() : null)
                .submissions(submissions)
                .submissionCount(submissions.size())
                .build();
    }
}
