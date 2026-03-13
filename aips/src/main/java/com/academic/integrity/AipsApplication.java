package com.academic.integrity;

import com.academic.integrity.entity.*;
import com.academic.integrity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * AIPS - Academic Integrity & Plagiarism System
 * Spring Boot Application Entry Point
 */
@SpringBootApplication
@RequiredArgsConstructor
public class AipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AipsApplication.class, args);
    }

    /**
     * CommandLineRunner seeds the database with initial data on startup.
     * This runs once when the application starts.
     */
    @Bean
    CommandLineRunner seedDatabase(
            ProvinceRepository provinceRepo,
            RoleRepository roleRepo,
            UserRepository userRepo,
            CourseRepository courseRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed Provinces (Rwanda)
            Province kigali   = provinceRepo.save(Province.builder().provinceCode("KGL").provinceName("Kigali").description("Capital city province").build());
            Province eastern  = provinceRepo.save(Province.builder().provinceCode("EST").provinceName("Eastern Province").description("Eastern Rwanda").build());
            Province western  = provinceRepo.save(Province.builder().provinceCode("WST").provinceName("Western Province").description("Western Rwanda").build());
            Province northern = provinceRepo.save(Province.builder().provinceCode("NTH").provinceName("Northern Province").description("Northern Rwanda").build());
            Province southern = provinceRepo.save(Province.builder().provinceCode("STH").provinceName("Southern Province").description("Southern Rwanda").build());

            // Seed Roles
            Role adminRole    = roleRepo.save(Role.builder().roleName("ADMIN").description("System Administrator").build());
            Role lecturerRole = roleRepo.save(Role.builder().roleName("LECTURER").description("Course Lecturer").build());
            Role studentRole  = roleRepo.save(Role.builder().roleName("STUDENT").description("Enrolled Student").build());

            // Seed Admin User
            userRepo.save(User.builder()
                    .fullName("System Administrator")
                    .email("admin@aips.ac.rw")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .province(kigali)
                    .isActive(true)
                    .build());

            // Seed Lecturers
            User lecturer1 = userRepo.save(User.builder()
                    .fullName("Dr. Jean Baptiste Nkusi")
                    .email("jb.nkusi@aips.ac.rw")
                    .password(passwordEncoder.encode("lecturer123"))
                    .role(lecturerRole)
                    .province(kigali)
                    .isActive(true)
                    .build());

            // Seed Students
            User student1 = userRepo.save(User.builder()
                    .fullName("Alice Uwimana")
                    .email("alice.uwimana@student.aips.ac.rw")
                    .password(passwordEncoder.encode("student123"))
                    .studentId("STU-2024-001")
                    .role(studentRole)
                    .province(kigali)
                    .isActive(true)
                    .build());

            User student2 = userRepo.save(User.builder()
                    .fullName("Bob Habimana")
                    .email("bob.habimana@student.aips.ac.rw")
                    .password(passwordEncoder.encode("student123"))
                    .studentId("STU-2024-002")
                    .role(studentRole)
                    .province(eastern)
                    .isActive(true)
                    .build());

            User student3 = userRepo.save(User.builder()
                    .fullName("Claire Mukamana")
                    .email("claire.mukamana@student.aips.ac.rw")
                    .password(passwordEncoder.encode("student123"))
                    .studentId("STU-2024-003")
                    .role(studentRole)
                    .province(northern)
                    .isActive(true)
                    .build());

            // Seed Courses
            Course cs301 = courseRepo.save(Course.builder()
                    .courseCode("CS301")
                    .courseName("Database Systems")
                    .description("Relational databases, SQL, and data modeling")
                    .creditHours(3)
                    .semester("2026-S1")
                    .build());

            Course cs401 = courseRepo.save(Course.builder()
                    .courseCode("CS401")
                    .courseName("Software Engineering")
                    .description("Software design patterns and project management")
                    .creditHours(4)
                    .semester("2026-S1")
                    .build());

            Course cs201 = courseRepo.save(Course.builder()
                    .courseCode("CS201")
                    .courseName("Data Structures & Algorithms")
                    .description("Core algorithms and data structures")
                    .creditHours(3)
                    .semester("2026-S1")
                    .build());

            System.out.println("✅ Database seeded successfully!");
            System.out.println("🔐 Admin login: admin@aips.ac.rw / admin123");
        };
    }
}
