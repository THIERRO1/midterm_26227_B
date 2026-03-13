package com.academic.integrity.service;

import com.academic.integrity.entity.User;
import com.academic.integrity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserService - business logic layer for User operations
 *
 * Demonstrates:
 * - existsBy() usage
 * - Province filtering
 * - Pagination + Sorting
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Save a new user after checking for duplicates
     *
     * existsBy() usage:
     * Before inserting, we check if email already exists.
     * This prevents duplicate records in the database.
     *
     * Without this check: a second user with same email would cause a DB constraint error.
     * With this check: we give a friendly message instead.
     */
    public User saveUser(User user) {
        // existsByEmail() checks if email exists WITHOUT loading the full user object
        // This is efficient - Spring Data JPA translates it to: SELECT COUNT(*) > 0 FROM users WHERE email = ?
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        if (user.getStudentId() != null && userRepository.existsByStudentId(user.getStudentId())) {
            throw new RuntimeException("Student ID already exists: " + user.getStudentId());
        }

        return userRepository.save(user);
    }

    /**
     * Retrieve all users from a province by CODE
     *
     * Province query logic:
     * Uses a custom JPQL query in the repository.
     * Navigates the User → Province relationship using dot notation.
     * This is a One-to-Many query: find the 'many' side (users) using the 'one' side (province).
     */
    public List<User> getUsersByProvinceCode(String provinceCode) {
        return userRepository.findUsersByProvinceCode(provinceCode);
    }

    /**
     * Retrieve all users from a province by NAME
     * Uses case-insensitive LIKE search for flexibility.
     */
    public List<User> getUsersByProvinceName(String provinceName) {
        return userRepository.findUsersByProvinceName(provinceName);
    }

    /**
     * Get all students with PAGINATION and SORTING
     *
     * Pagination explanation:
     * Instead of loading ALL users at once (expensive),
     * we load a small page of results at a time.
     *
     * PageRequest.of(pageNumber, pageSize, Sort.by(field).direction())
     * - pageNumber: 0-indexed page (0 = first page)
     * - pageSize: number of records per page
     * - Sort: how to order the results
     *
     * This improves performance significantly when there are thousands of records.
     *
     * @param page     - page number (0-based)
     * @param size     - records per page
     * @param sortBy   - field to sort by (e.g., "fullName", "email")
     * @param direction - "asc" or "desc"
     */
    public Page<User> getStudentsWithPaginationAndSorting(int page, int size, String sortBy, String direction) {
        // Build the Sort object
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // Build the Pageable object combining page, size, and sort
        Pageable pageable = PageRequest.of(page, size, sort);

        // Returns a Page object containing:
        // - the records for this page
        // - total number of pages
        // - total number of records
        // - current page number
        return userRepository.findByRoleRoleName("STUDENT", pageable);
    }

    public Page<User> searchUsers(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return userRepository.searchByName(name, pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long countStudents() {
        return userRepository.countByRoleRoleName("STUDENT");
    }

    public long countLecturers() {
        return userRepository.countByRoleRoleName("LECTURER");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
