package com.academic.plagiarism.dto;

import com.academic.plagiarism.model.PlagiarismCase;
import com.academic.plagiarism.model.Submission;
import com.academic.plagiarism.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class Dto {

    // ==================== USER ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserRequest {
        @NotBlank(message = "First name required") public String firstName;
        @NotBlank(message = "Last name required") public String lastName;
        @Email @NotBlank(message = "Email required") public String email;
        @NotBlank(message = "Student ID required") public String studentId;
        public User.Role role;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserResponse {
        public Long id;
        public String firstName;
        public String lastName;
        public String fullName;
        public String email;
        public String studentId;
        public User.Role role;
        public LocalDateTime createdAt;
        public LocationResponse location;
    }

    // ==================== LOCATION ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LocationRequest {
        public String address;
        @NotBlank(message = "City required") public String city;
        @NotBlank(message = "Province name required") public String provinceName;
        @NotBlank(message = "Province code required") public String provinceCode;
        public String country;
        @NotNull(message = "User ID required") public Long userId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LocationResponse {
        public Long id;
        public String address;
        public String city;
        public String provinceName;
        public String provinceCode;
        public String country;
    }

    // ==================== PROVINCE USER RESPONSE ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProvinceUserResponse {
        public String provinceName;
        public String provinceCode;
        public List<UserSummary> users;
        public int totalUsers;

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class UserSummary {
            public Long userId;
            public String fullName;
            public String email;
            public String studentId;
            public String city;
        }
    }

    // ==================== SUBMISSION ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubmissionRequest {
        @NotBlank(message = "Title required") public String title;
        public String content;
        public Double similarityScore;
        public Submission.SubmissionStatus status;
        @NotNull(message = "User ID required") public Long userId;
        public Long courseId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubmissionResponse {
        public Long id;
        public String title;
        public String content;
        public Double similarityScore;
        public Submission.SubmissionStatus status;
        public LocalDateTime submittedAt;
        public String userName;
        public Long userId;
        public String courseName;
        public Long courseId;
    }

    // ==================== PLAGIARISM CASE ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PlagiarismCaseRequest {
        @NotBlank(message = "Case number required") public String caseNumber;
        public String description;
        public PlagiarismCase.Severity severity;
        public PlagiarismCase.CaseStatus status;
        public Long investigatorId;
        public List<Long> submissionIds;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PlagiarismCaseResponse {
        public Long id;
        public String caseNumber;
        public String description;
        public PlagiarismCase.Severity severity;
        public PlagiarismCase.CaseStatus status;
        public LocalDateTime createdAt;
        public String investigatorName;
        public List<SubmissionResponse> submissions;
        public int submissionCount;
    }

    // ==================== DEPARTMENT ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DepartmentRequest {
        @NotBlank(message = "Name required") public String name;
        @NotBlank(message = "Code required") public String code;
        public String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DepartmentResponse {
        public Long id;
        public String name;
        public String code;
        public String description;
        public int courseCount;
    }

    // ==================== COURSE ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CourseRequest {
        @NotBlank(message = "Title required") public String title;
        @NotBlank(message = "Course code required") public String courseCode;
        public Integer credits;
        @NotNull(message = "Department ID required") public Long departmentId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CourseResponse {
        public Long id;
        public String title;
        public String courseCode;
        public Integer credits;
        public Long departmentId;
        public String departmentName;
    }

    // ==================== PAGE WRAPPER ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PageResponse<T> {
        public List<T> content;
        public int pageNumber;
        public int pageSize;
        public long totalElements;
        public int totalPages;
        public boolean first;
        public boolean last;
        public boolean hasNext;
        public boolean hasPrevious;
    }

    // ==================== STATS ====================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DashboardStats {
        public long totalUsers;
        public long totalSubmissions;
        public long totalCases;
        public long pendingSubmissions;
        public long flaggedSubmissions;
        public long plagiarizedSubmissions;
        public long openCases;
        public long resolvedCases;
    }
}
