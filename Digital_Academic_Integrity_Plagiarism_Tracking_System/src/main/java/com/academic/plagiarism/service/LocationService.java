package com.academic.plagiarism.service;

import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.exception.DuplicateResourceException;
import com.academic.plagiarism.exception.ResourceNotFoundException;
import com.academic.plagiarism.model.Location;
import com.academic.plagiarism.model.User;
import com.academic.plagiarism.repository.LocationRepository;
import com.academic.plagiarism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    /**
     * REQUIREMENT #2: Save Location
     * @Transactional ensures atomicity - either both user and location are saved, or neither
     * The bidirectional sync (user.setLocation) keeps the in-memory object graph consistent
     */
    @Transactional
    public Dto.LocationResponse saveLocation(Dto.LocationRequest request) {
        log.debug("Saving location for user ID: {}", request.userId);

        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", request.userId));

        // Check if user already has a location (unique constraint)
        locationRepository.findByUserId(request.userId).ifPresent(existing -> {
            throw new DuplicateResourceException("User already has a location. Use update instead.");
        });

        Location location = Location.builder()
                .address(request.address)
                .city(request.city)
                .provinceName(request.provinceName)
                .provinceCode(request.provinceCode.toUpperCase())
                .country(request.country != null ? request.country : "Rwanda")
                .user(user)
                .build();

        // Bidirectional sync: set user.location so in-memory object is consistent
        user.setLocation(location);

        Location saved = locationRepository.save(location);
        log.debug("Location saved with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public Dto.LocationResponse updateLocation(Long id, Dto.LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        location.setAddress(request.address);
        location.setCity(request.city);
        location.setProvinceName(request.provinceName);
        location.setProvinceCode(request.provinceCode.toUpperCase());
        if (request.country != null) location.setCountry(request.country);

        return mapToResponse(locationRepository.save(location));
    }

    @Transactional(readOnly = true)
    public List<Dto.LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.LocationResponse getLocationByUserId(Long userId) {
        Location location = locationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found for user ID: " + userId));
        return mapToResponse(location);
    }

    /**
     * REQUIREMENT #8: Get users by province code
     * Uses JOIN FETCH query to avoid N+1 problem
     */
    @Transactional(readOnly = true)
    public Dto.ProvinceUserResponse getUsersByProvinceCode(String code) {
        log.debug("Fetching users by province code: {}", code);

        // JOIN FETCH loads Location + User in ONE SQL query (no N+1)
        List<Location> locations = locationRepository.findByProvinceCodeWithUser(code.toUpperCase());

        if (locations.isEmpty()) {
            return Dto.ProvinceUserResponse.builder()
                    .provinceCode(code.toUpperCase())
                    .provinceName("Unknown")
                    .users(List.of())
                    .totalUsers(0)
                    .build();
        }

        List<Dto.ProvinceUserResponse.UserSummary> users = locations.stream()
                .map(loc -> Dto.ProvinceUserResponse.UserSummary.builder()
                        .userId(loc.getUser().getId())
                        .fullName(loc.getUser().getFullName())
                        .email(loc.getUser().getEmail())
                        .studentId(loc.getUser().getStudentId())
                        .city(loc.getCity())
                        .build())
                .collect(Collectors.toList());

        return Dto.ProvinceUserResponse.builder()
                .provinceCode(locations.get(0).getProvinceCode())
                .provinceName(locations.get(0).getProvinceName())
                .users(users)
                .totalUsers(users.size())
                .build();
    }

    /**
     * REQUIREMENT #8: Get users by province name (partial, case-insensitive)
     */
    @Transactional(readOnly = true)
    public Dto.ProvinceUserResponse getUsersByProvinceName(String name) {
        log.debug("Fetching users by province name: {}", name);

        List<Location> locations = locationRepository.findByProvinceNameWithUser(name);

        if (locations.isEmpty()) {
            return Dto.ProvinceUserResponse.builder()
                    .provinceName(name)
                    .users(List.of())
                    .totalUsers(0)
                    .build();
        }

        List<Dto.ProvinceUserResponse.UserSummary> users = locations.stream()
                .map(loc -> Dto.ProvinceUserResponse.UserSummary.builder()
                        .userId(loc.getUser().getId())
                        .fullName(loc.getUser().getFullName())
                        .email(loc.getUser().getEmail())
                        .studentId(loc.getUser().getStudentId())
                        .city(loc.getCity())
                        .build())
                .collect(Collectors.toList());

        return Dto.ProvinceUserResponse.builder()
                .provinceCode(locations.get(0).getProvinceCode())
                .provinceName(locations.get(0).getProvinceName())
                .users(users)
                .totalUsers(users.size())
                .build();
    }

    private Dto.LocationResponse mapToResponse(Location location) {
        return Dto.LocationResponse.builder()
                .id(location.getId())
                .address(location.getAddress())
                .city(location.getCity())
                .provinceName(location.getProvinceName())
                .provinceCode(location.getProvinceCode())
                .country(location.getCountry())
                .build();
    }
}
