package com.academic.plagiarism.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Academic Integrity Plagiarism Tracker API")
                        .description("""
                                ## Digital Academic Integrity & Plagiarism Tracking System
                                
                                A comprehensive Spring Boot REST API demonstrating:
                                - **ERD with 5 Tables**: Department, Course, User, Submission, PlagiarismCase, Location
                                - **One-to-One**: User ↔ Location
                                - **One-to-Many**: Department → Courses
                                - **Many-to-One**: Course → Department
                                - **Many-to-Many**: PlagiarismCase ↔ Submission (via case_submissions join table)
                                - **Sorting & Pagination**: /api/submissions/page?page=0&size=5&sortBy=similarityScore&direction=desc
                                - **existsBy()**: Duplicate prevention for email and studentId
                                - **Province Queries**: /api/locations/province/code/KIG/users
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Academic Team").email("admin@university.ac.rw"))
                        .license(new License().name("MIT")));
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
