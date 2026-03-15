package com.academic.plagiarism.controller;

import com.academic.plagiarism.dto.ApiResponse;
import com.academic.plagiarism.dto.Dto;
import com.academic.plagiarism.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Location management and province queries API")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Save location for a user (Requirement #2)")
    public ResponseEntity<ApiResponse<Dto.LocationResponse>> saveLocation(@Valid @RequestBody Dto.LocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(locationService.saveLocation(request), "Location saved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all locations")
    public ResponseEntity<ApiResponse<List<Dto.LocationResponse>>> getAllLocations() {
        return ResponseEntity.ok(ApiResponse.success(locationService.getAllLocations()));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get location by user ID")
    public ResponseEntity<ApiResponse<Dto.LocationResponse>> getLocationByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(locationService.getLocationByUserId(userId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update location")
    public ResponseEntity<ApiResponse<Dto.LocationResponse>> updateLocation(
            @PathVariable Long id, @Valid @RequestBody Dto.LocationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(locationService.updateLocation(id, request), "Location updated"));
    }

    /**
     * REQUIREMENT #8: Retrieve users by province CODE
     * GET /api/locations/province/code/KIG/users
     */
    @GetMapping("/province/code/{code}/users")
    @Operation(summary = "Get all users from a province by province code (Requirement #8)")
    public ResponseEntity<ApiResponse<Dto.ProvinceUserResponse>> getUsersByProvinceCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(locationService.getUsersByProvinceCode(code),
                "Users retrieved by province code: " + code));
    }

    /**
     * REQUIREMENT #8: Retrieve users by province NAME
     * GET /api/locations/province/name/Kigali/users
     */
    @GetMapping("/province/name/{name}/users")
    @Operation(summary = "Get all users from a province by province name (Requirement #8)")
    public ResponseEntity<ApiResponse<Dto.ProvinceUserResponse>> getUsersByProvinceName(@PathVariable String name) {
        return ResponseEntity.ok(ApiResponse.success(locationService.getUsersByProvinceName(name),
                "Users retrieved by province name: " + name));
    }
}
