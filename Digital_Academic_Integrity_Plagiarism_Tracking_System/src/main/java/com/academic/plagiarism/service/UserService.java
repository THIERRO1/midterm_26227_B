package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.DuplicateResourceException;
import com.academic.plagiarism.exception.ResourceNotFoundException;
import com.academic.plagiarism.model.User;
import com.academic.plagiarism.repository.LocationRepository;
import com.academic.plagiarism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public Dto.UserResponse createUser(Dto.UserRequest request) {
        log.debug("Creating user with email: {}", request.email);

        // REQUIREMENT #7: existsBy() checks before saving
        if (userRepository.existsByEmail(request.email)) {
            throw new DuplicateResourceException("User with email '" + request.email + "' already exists");
        }
        if (userRepository.existsByStudentId(request.studentId)) {
            throw new DuplicateResourceException("User with student ID '" + request.studentId + "' already exists");
        }

        User user = User.builder()
                .firstName(request.firstName)
                .lastName(request.lastName)
                .email(request.email)
                .studentId(request.studentId)
                .role(request.role != null ? request.role : User.Role.STUDENT)
                .build();

        User saved = userRepository.save(user);
        log.debug("User created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<Dto.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToResponse(user);
    }

    public Dto.UserResponse updateUser(Long id, Dto.UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // Check duplicates, but EXCLUDE current user (existsByEmailAndIdNot)
        if (userRepository.existsByEmailAndIdNot(request.email, id)) {
            throw new DuplicateResourceException("Email '" + request.email + "' is already used by another user");
        }
        if (userRepository.existsByStudentIdAndIdNot(request.studentId, id)) {
            throw new DuplicateResourceException("Student ID '" + request.studentId + "' is already used by another user");
        }

        user.setFirstName(request.firstName);
        user.setLastName(request.lastName);
        user.setEmail(request.email);
        user.setStudentId(request.studentId);
        if (request.role != null) user.setRole(request.role);

        return mapToResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
        log.debug("Deleted user with ID: {}", id);
    }

    private Dto.UserResponse mapToResponse(User user) {
        Dto.LocationResponse locationResponse = null;
        try {
            var loc = locationRepository.findByUserId(user.getId());
            if (loc.isPresent()) {
                var l = loc.get();
                locationResponse = Dto.LocationResponse.builder()
                        .id(l.getId())
                        .address(l.getAddress())
                        .city(l.getCity())
                        .provinceName(l.getProvinceName())
                        .provinceCode(l.getProvinceCode())
                        .country(l.getCountry())
                        .build();
            }
        } catch (Exception ignored) {}

        return Dto.UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .location(locationResponse)
                .build();
    }
}
