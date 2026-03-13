package com.academic.integrity.controller;

import com.academic.integrity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController - handles the main dashboard view
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final PlagiarismReportRepository reportRepository;
    private final MisconductCaseRepository caseRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Dashboard overview - aggregates statistics for display
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistics for dashboard cards
        model.addAttribute("totalStudents",    userRepository.countByRoleRoleName("STUDENT"));
        model.addAttribute("totalLecturers",   userRepository.countByRoleRoleName("LECTURER"));
        model.addAttribute("totalSubmissions", submissionRepository.count());
        model.addAttribute("totalCourses",     courseRepository.count());
        model.addAttribute("flaggedCases",     caseRepository.countByStatus("PENDING") + caseRepository.countByStatus("UNDER_INVESTIGATION"));
        model.addAttribute("criticalReports",  reportRepository.countBySimilarityStatus("CRITICAL"));
        model.addAttribute("highReports",      reportRepository.countBySimilarityStatus("HIGH"));

        // Recent submissions (latest 5)
        model.addAttribute("recentSubmissions", submissionRepository.findAll(
            org.springframework.data.domain.PageRequest.of(0, 5,
            org.springframework.data.domain.Sort.by("submissionDate").descending())
        ).getContent());

        // High-risk reports
        model.addAttribute("highRiskReports", reportRepository.findHighSimilarityReports(60.0));

        return "dashboard";
    }
}
