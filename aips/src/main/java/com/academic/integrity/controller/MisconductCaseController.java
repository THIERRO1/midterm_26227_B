package com.academic.integrity.controller;

import com.academic.integrity.entity.MisconductCase;
import com.academic.integrity.repository.MisconductCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cases")
@RequiredArgsConstructor
public class MisconductCaseController {

    private final MisconductCaseRepository caseRepository;

    @GetMapping
    public String listCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "openedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String status,
            Model model
    ) {
        Sort sort = dir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        var casesPage = (status != null && !status.isEmpty())
                ? caseRepository.findByStatus(status, pageable)
                : caseRepository.findAll(pageable);

        model.addAttribute("cases", casesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", casesPage.getTotalPages());
        model.addAttribute("totalElements", casesPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("pendingCount",       caseRepository.countByStatus("PENDING"));
        model.addAttribute("investigationCount", caseRepository.countByStatus("UNDER_INVESTIGATION"));
        model.addAttribute("resolvedCount",      caseRepository.countByStatus("RESOLVED"));
        return "cases/list";
    }

    @GetMapping("/{id}")
    public String viewCase(@PathVariable Long id, Model model) {
        model.addAttribute("case", caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found")));
        return "cases/detail";
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam String status,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        MisconductCase mc = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        mc.setStatus(status);
        if (notes != null && !notes.isEmpty()) mc.setResolutionNotes(notes);
        if (status.equals("RESOLVED")) mc.setResolvedAt(java.time.LocalDateTime.now());
        caseRepository.save(mc);
        redirectAttributes.addFlashAttribute("success", "Case status updated to " + status);
        return "redirect:/cases/" + id;
    }
}
