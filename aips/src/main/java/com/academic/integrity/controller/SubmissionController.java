package com.academic.integrity.controller;

import com.academic.integrity.entity.Submission;
import com.academic.integrity.repository.CourseRepository;
import com.academic.integrity.repository.UserRepository;
import com.academic.integrity.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SubmissionController - handles assignment submissions
 */
@Controller
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * List submissions with pagination and sorting
     */
    @GetMapping
    public String listSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Page<Submission> submissionsPage;

        if (search != null && !search.isEmpty()) {
            submissionsPage = submissionService.searchSubmissions(search, page, size);
            model.addAttribute("search", search);
        } else {
            submissionsPage = submissionService.getSubmissionsPage(page, size, sortBy, dir);
        }

        model.addAttribute("submissions", submissionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", submissionsPage.getTotalPages());
        model.addAttribute("totalElements", submissionsPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);

        return "submissions/list";
    }

    @GetMapping("/new")
    public String showUploadForm(Model model) {
        model.addAttribute("submission", new Submission());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("students", userRepository.findByRoleRoleName("STUDENT",
            org.springframework.data.domain.PageRequest.of(0, 100)).getContent());
        return "submissions/form";
    }

    @PostMapping("/upload")
    public String uploadSubmission(
            @ModelAttribute Submission submission,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        try {
            submissionService.saveSubmission(submission, file);
            redirectAttributes.addFlashAttribute("success", "Assignment uploaded and analyzed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/submissions";
    }

    @GetMapping("/{id}")
    public String viewSubmission(@PathVariable Long id, Model model) {
        model.addAttribute("submission", submissionService.getSubmissionById(id));
        return "submissions/detail";
    }
}
