package com.academic.integrity.controller;

import com.academic.integrity.entity.User;
import com.academic.integrity.repository.ProvinceRepository;
import com.academic.integrity.repository.RoleRepository;
import com.academic.integrity.repository.UserRepository;
import com.academic.integrity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * UserController - handles all user management operations
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final RoleRepository roleRepository;

    /**
     * List all users with pagination and sorting
     *
     * @param page    - current page (default 0)
     * @param size    - records per page (default 10)
     * @param sortBy  - sort field (default fullName)
     * @param dir     - sort direction asc/desc
     * @param search  - optional search term
     */
    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Page<User> usersPage;

        if (search != null && !search.isEmpty()) {
            usersPage = userService.searchUsers(search, page, size);
            model.addAttribute("search", search);
        } else {
            usersPage = userService.getStudentsWithPaginationAndSorting(page, size, sortBy, dir);
        }

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);
        model.addAttribute("provinces", provinceRepository.findAll());

        return "users/list";
    }

    /**
     * Get users by province (province filtering feature)
     *
     * @param code - province code (e.g., "KGL")
     * @param name - province name (e.g., "Kigali")
     */
    @GetMapping("/by-province")
    public String getUsersByProvince(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            Model model
    ) {
        List<User> users;
        String filterUsed;

        if (code != null && !code.isEmpty()) {
            // Query by province CODE
            users = userService.getUsersByProvinceCode(code);
            filterUsed = "Province Code: " + code;
        } else if (name != null && !name.isEmpty()) {
            // Query by province NAME
            users = userService.getUsersByProvinceName(name);
            filterUsed = "Province Name: " + name;
        } else {
            users = userRepository.findAll();
            filterUsed = "All Provinces";
        }

        model.addAttribute("users", users);
        model.addAttribute("filterUsed", filterUsed);
        model.addAttribute("provinces", provinceRepository.findAll());
        model.addAttribute("selectedCode", code);
        model.addAttribute("selectedName", name);

        return "users/by-province";
    }

    /**
     * Show user creation form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("provinces", provinceRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "users/form";
    }

    /**
     * Save new user
     * Uses existsBy() to prevent duplicate emails
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    /**
     * View user details
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "users/detail";
    }
}
