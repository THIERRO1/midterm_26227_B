package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.ResourceNotFoundException;
import com.academic.plagiarism.model.Course;
import com.academic.plagiarism.model.Submission;
import com.academic.plagiarism.model.User;
import com.academic.plagiarism.repository.CourseRepository;
import com.academic.plagiarism.repository.SubmissionRepository;
import com.academic.plagiarism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public Dto.SubmissionResponse createSubmission(Dto.SubmissionRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", request.userId));

        Course course = null;
        if (request.courseId != null) {
            course = courseRepository.findById(request.courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course", request.courseId));
        }

        Submission submission = Submission.builder()
                .title(request.title)
                .content(request.content)
                .similarityScore(request.similarityScore != null ? request.similarityScore : 0.0)
                .status(request.status != null ? request.status : Submission.SubmissionStatus.PENDING)
                .user(user)
                .course(course)
                .build();

        return mapToResponse(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public List<Dto.SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.SubmissionResponse getSubmissionById(Long id) {
        return mapToResponse(submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", id)));
    }

    /**
     * REQUIREMENT #3: Sorting only
     * Sort.by(direction, sortBy) creates the ORDER BY clause
     */
    @Transactional(readOnly = true)
    public List<Dto.SubmissionResponse> getAllSubmissionsSorted(String sortBy, String direction) {
        log.debug("Getting sorted submissions: sortBy={}, direction={}", sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return submissionRepository.findAll(sort).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * REQUIREMENT #3: Pagination only
     * PageRequest.of(page, size) creates LIMIT/OFFSET automatically
     * Returns Page<T> which contains both the data AND metadata (total pages, total elements, etc.)
     */
    @Transactional(readOnly = true)
    public Dto.PageResponse<Dto.SubmissionResponse> getSubmissionsPaginated(int page, int size) {
        log.debug("Getting paginated submissions: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Submission> pageResult = submissionRepository.findAll(pageable);
        return mapToPageResponse(pageResult);
    }

    /**
     * REQUIREMENT #3: Pagination + Sorting combined
     * PageRequest.of(page, size, sort) combines both in ONE query
     */
    @Transactional(readOnly = true)
    public Dto.PageResponse<Dto.SubmissionResponse> getSubmissionsWithPaginationAndSorting(
            int page, int size, String sortBy, String direction) {
        log.debug("Getting paginated+sorted submissions: page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Submission> pageResult = submissionRepository.findAll(pageable);
        return mapToPageResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public Dto.PageResponse<Dto.SubmissionResponse> getSubmissionsByStatus(
            Submission.SubmissionStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Submission> pageResult = submissionRepository.findByStatus(status, pageable);
        return mapToPageResponse(pageResult);
    }

    public Dto.SubmissionResponse updateSubmission(Long id, Dto.SubmissionRequest request) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", id));
        submission.setTitle(request.title);
        if (request.content != null) submission.setContent(request.content);
        if (request.similarityScore != null) submission.setSimilarityScore(request.similarityScore);
        if (request.status != null) submission.setStatus(request.status);
        return mapToResponse(submissionRepository.save(submission));
    }

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) throw new ResourceNotFoundException("Submission", id);
        submissionRepository.deleteById(id);
    }

    private Dto.PageResponse<Dto.SubmissionResponse> mapToPageResponse(Page<Submission> page) {
        return Dto.PageResponse.<Dto.SubmissionResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public Dto.SubmissionResponse mapToResponse(Submission s) {
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
}
