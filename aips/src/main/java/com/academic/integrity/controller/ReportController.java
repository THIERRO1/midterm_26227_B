package com.academic.integrity.controller;

import com.academic.integrity.repository.PlagiarismReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PlagiarismReportRepository reportRepository;

    @GetMapping
    public String listReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "similarityScore") String sortBy,
            @RequestParam(defaultValue = "desc") String dir,
            Model model
    ) {
        Sort sort = dir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var reportsPage = reportRepository.findAll(pageable);

        model.addAttribute("reports", reportsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportsPage.getTotalPages());
        model.addAttribute("totalElements", reportsPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);
        model.addAttribute("criticalCount", reportRepository.countBySimilarityStatus("CRITICAL"));
        model.addAttribute("highCount",     reportRepository.countBySimilarityStatus("HIGH"));
        model.addAttribute("moderateCount", reportRepository.countBySimilarityStatus("MODERATE"));
        model.addAttribute("lowCount",      reportRepository.countBySimilarityStatus("LOW"));
        return "reports/list";
    }
}
