package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.model.PlagiarismCase;
import com.academic.plagiarism.model.Submission;
import com.academic.plagiarism.repository.UserRepository;
import com.academic.plagiarism.repository.SubmissionRepository;
import com.academic.plagiarism.repository.PlagiarismCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final PlagiarismCaseRepository caseRepository;

    @Transactional(readOnly = true)
    public Dto.DashboardStats getDashboardStats() {
        return Dto.DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalSubmissions(submissionRepository.count())
                .totalCases(caseRepository.count())
                .pendingSubmissions(submissionRepository.countByStatus(Submission.SubmissionStatus.PENDING))
                .flaggedSubmissions(submissionRepository.countByStatus(Submission.SubmissionStatus.FLAGGED))
                .plagiarizedSubmissions(submissionRepository.countByStatus(Submission.SubmissionStatus.PLAGIARIZED))
                .openCases(caseRepository.countByStatus(PlagiarismCase.CaseStatus.OPEN))
                .resolvedCases(caseRepository.countByStatus(PlagiarismCase.CaseStatus.RESOLVED))
                .build();
    }
}
