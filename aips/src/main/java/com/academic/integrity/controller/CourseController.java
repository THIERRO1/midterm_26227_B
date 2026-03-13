package com.academic.integrity.controller;

import com.academic.integrity.entity.Course;
import com.academic.integrity.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CourseController - handles course management
 */
@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;

    @GetMapping
    public String listCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseName") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            Model model
    ) {
        Sort sort = dir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> coursesPage = courseRepository.findAll(pageable);

        model.addAttribute("courses", coursesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursesPage.getTotalPages());
        model.addAttribute("totalElements", coursesPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);
        return "courses/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {
        // existsBy() check - prevent duplicate course codes
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            redirectAttributes.addFlashAttribute("error", "Course code already exists: " + course.getCourseCode());
            return "redirect:/courses/new";
        }
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("success", "Course created successfully!");
        return "redirect:/courses";
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found")));
        return "courses/detail";
    }
}
