package com.academic.integrity.service;

import com.academic.integrity.entity.PlagiarismReport;
import com.academic.integrity.entity.Submission;
import com.academic.integrity.repository.PlagiarismReportRepository;
import com.academic.integrity.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Random;

/**
 * SubmissionService - handles assignment uploads and plagiarism analysis
 *
 * Demonstrates:
 * - File upload handling
 * - Pagination + Sorting in action
 * - One-to-One relationship (Submission ↔ PlagiarismReport)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final PlagiarismReportRepository plagiarismReportRepository;

    private final String uploadDir = "./uploads/";

    /**
     * Save a submission (stores file and creates plagiarism report)
     *
     * Data saving explanation:
     * 1. The file is saved to the filesystem (./uploads/)
     * 2. A Submission entity is created with file metadata
     * 3. submissionRepository.save(submission) calls Spring Data JPA
     * 4. JPA generates: INSERT INTO submissions (...) VALUES (...)
     * 5. After saving, a PlagiarismReport is automatically generated
     * 6. The One-to-One relationship links report to submission
     */
    public Submission saveSubmission(Submission submission, MultipartFile file) throws IOException {
        // Save file to disk
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            submission.setFilePath(filePath.toString());
            submission.setFileSize(file.getSize());

            // Determine file type
            String originalName = file.getOriginalFilename();
            if (originalName != null) {
                if (originalName.endsWith(".pdf")) submission.setFileType("PDF");
                else if (originalName.endsWith(".docx")) submission.setFileType("DOCX");
                else throw new RuntimeException("File type not supported. Use PDF or DOCX.");
            }
        }

        // Save submission entity via Spring Data JPA
        Submission saved = submissionRepository.save(submission);

        // Automatically generate plagiarism report (One-to-One)
        generatePlagiarismReport(saved);

        return saved;
    }

    /**
     * Generate plagiarism report for a submission
     * In a real system, this would call an external plagiarism API (Turnitin, etc.)
     * Here we simulate with random scores for demonstration.
     */
    private void generatePlagiarismReport(Submission submission) {
        // Simulate plagiarism detection
        Random random = new Random();
        double score = Math.round(random.nextDouble() * 100 * 10.0) / 10.0;

        PlagiarismReport report = PlagiarismReport.builder()
                .submission(submission)
                .similarityScore(score)
                .matchedSources(random.nextInt(5) + 1)
                .analysisNotes("Automated similarity analysis completed.")
                .build();

        // @PrePersist in PlagiarismReport will set the similarityStatus automatically
        plagiarismReportRepository.save(report);

        // Update submission status
        submission.setStatus("PROCESSED");
        submissionRepository.save(submission);
    }

    /**
     * Get submissions with pagination and sorting
     *
     * How Sorting works:
     * - Sort.by("submissionDate").descending() → newest submissions first
     * - Sort.by("title").ascending() → alphabetical order A-Z
     * - Sort.by("plagiarismReport.similarityScore").descending() → highest risk first
     *
     * How Pagination works:
     * - PageRequest.of(0, 10) = page 1, show 10 records
     * - PageRequest.of(1, 10) = page 2, show next 10 records
     * - Page object contains: content, totalPages, totalElements, pageNumber
     */
    public Page<Submission> getSubmissionsPage(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return submissionRepository.findAll(pageable);
    }

    public Page<Submission> searchSubmissions(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submissionDate").descending());
        return submissionRepository.searchByTitle(title, pageable);
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + id));
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll(Sort.by("submissionDate").descending());
    }

    public long countPending() {
        return submissionRepository.countByStatus("PENDING");
    }

    public long countTotal() {
        return submissionRepository.count();
    }
}
