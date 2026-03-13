# 🎓 AIPS — Academic Integrity & Plagiarism System

> A web-based platform designed to uphold academic integrity by detecting plagiarism in student submissions, tracking misconduct cases, and maintaining institutional records.

---

## 📋 Project Overview

AIPS is a full-stack **Spring Boot** application that helps universities monitor academic honesty by analyzing student assignment submissions for similarity, tracking misconduct cases, and providing comprehensive reporting dashboards.

---

## 🛠️ Technologies Used

| Layer        | Technology                        |
|-------------|-----------------------------------|
| Backend     | Java 17, Spring Boot 3.2          |
| ORM         | Spring Data JPA / Hibernate       |
| Security    | Spring Security (BCrypt)          |
| Templates   | Thymeleaf                         |
| Database    | MySQL (Production), H2 (Dev/Test) |
| Build Tool  | Maven                             |
| Frontend    | HTML5, CSS3, Vanilla JS           |

---

## 🗄️ Database Design (ERD Tables)

| Table              | Purpose                                    |
|--------------------|--------------------------------------------|
| `provinces`        | Geographic provinces of Rwanda             |
| `roles`            | User roles: ADMIN, LECTURER, STUDENT       |
| `users`            | System users (students & staff)            |
| `courses`          | Academic courses/modules                   |
| `user_courses`     | Join table for many-to-many enrollment     |
| `submissions`      | Uploaded student assignments               |
| `plagiarism_reports` | Similarity analysis results             |
| `misconduct_cases` | Academic violation tracking                |
| `notifications`    | System alerts and messages                 |

---

## 🔗 Implemented Relationships

### 1️⃣ One-to-One: Submission ↔ PlagiarismReport
Each submission has **exactly one** plagiarism report.
```java
@OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
private PlagiarismReport plagiarismReport;
```
The `plagiarism_reports` table holds a `submission_id` FK with a `UNIQUE` constraint.

---

### 2️⃣ One-to-Many: Province → Users
One province can have **many users**. The `users` table has a `province_id` FK.
```java
// In Province:
@OneToMany(mappedBy = "province")
private List<User> users;

// In User:
@ManyToOne
@JoinColumn(name = "province_id")
private Province province;
```

---

### 3️⃣ Many-to-Many: Users ↔ Courses
Students can enroll in **many courses**; courses have **many students**.
```java
// In User:
@ManyToMany
@JoinTable(name = "user_courses",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id"))
private Set<Course> courses;
```
This creates a `user_courses` join table with `user_id` and `course_id`.

---

## 📄 Data Saving (Spring Data JPA)

Entities represent database tables. Spring Data JPA translates Java objects into SQL automatically:

```java
// Service layer calls repository to save data
userRepository.save(user);
// Generated SQL: INSERT INTO users (full_name, email, ...) VALUES (?, ?, ...)

// Spring Data JPA reads annotation @Entity and @Table to determine the table
// @Id @GeneratedValue handles auto-increment primary keys
```

---

## 🔄 Pagination & Sorting

```java
// Create Pageable object with page, size, and sort
Pageable pageable = PageRequest.of(
    0,                          // Page number (0-indexed)
    10,                         // Page size (records per page)
    Sort.by("fullName").asc()   // Sort field and direction
);

// Repository returns Page<T> — contains records + pagination metadata
Page<User> page = userRepository.findAll(pageable);

// Page object provides:
page.getContent();      // List of records for this page
page.getTotalPages();   // Total number of pages
page.getTotalElements(); // Total records in database
page.getNumber();       // Current page number
```

**Why Pagination?**
- Loads only 10 records instead of thousands
- Reduces memory usage and network transfer
- Faster response times for the user

---

## ✅ existsBy() Method

```java
// Checks if record exists WITHOUT loading the full entity
// Generated SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
boolean exists = userRepository.existsByEmail("alice@example.com");

// Used to prevent duplicate data:
if (userRepository.existsByEmail(user.getEmail())) {
    throw new RuntimeException("Email already exists!");
}
```

---

## 🗺️ Province Filtering (Custom JPQL Queries)

```java
// Filter by province CODE
@Query("SELECT u FROM User u WHERE u.province.provinceCode = :provinceCode")
List<User> findUsersByProvinceCode(@Param("provinceCode") String code);

// Filter by province NAME (case-insensitive)
@Query("SELECT u FROM User u WHERE LOWER(u.province.provinceName) LIKE LOWER(CONCAT('%', :name, '%'))")
List<User> findUsersByProvinceName(@Param("provinceName") String name);
```

These navigate the `User → Province` relationship using JPQL dot notation.

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+ (or use H2 for dev)

### Steps

```bash
# 1. Clone the project
git clone <repository-url>
cd aips

# 2. Configure database (for MySQL)
# Edit src/main/resources/application.properties
# Uncomment MySQL lines and set your credentials

# 3. Build and run
mvn spring-boot:run

# 4. Access the application
# Open: http://localhost:8080
```

### Default Credentials
| Role    | Email                                        | Password    |
|---------|----------------------------------------------|-------------|
| Admin   | admin@aips.ac.rw                             | admin123    |
| Student | alice.uwimana@student.aips.ac.rw             | student123  |

---

## ✨ Key Features

- 🔐 Role-based authentication (Admin / Lecturer / Student)
- 📊 Real-time dashboard with statistics
- 📤 File upload (PDF, DOCX) with plagiarism analysis
- 🔍 Similarity reports with visual progress bars
- ⚠️ Misconduct case tracking and management
- 🗺️ Province-based user filtering
- 📄 Pagination and multi-column sorting
- 🔎 Search functionality across all modules
- 📱 Responsive design for all screen sizes

---

## 👨‍💻 Project Structure

```
src/main/java/com/academic/integrity/
├── controller/        # HTTP request handlers
├── service/           # Business logic
├── repository/        # Spring Data JPA interfaces
├── entity/            # Database table mappings
├── dto/               # Data Transfer Objects
└── config/            # Security & app config

src/main/resources/
├── templates/         # Thymeleaf HTML templates
├── static/            # CSS, JS, images
└── application.properties
```

---

*AIPS © 2026 — University Academic Department. All Rights Reserved.*
