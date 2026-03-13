# midterm_26227_B
A Spring Boot web application for tracking academic integrity and detecting plagiarism in student submissions. Features One-to-One, One-to-Many, and Many-to-Many JPA relationships, pagination, sorting, existsBy() validation, and province-based user filtering. Built with Spring Data JPA, Spring Security, Thymeleaf, and MySQL.
# 🎓 AIPS — Academic Integrity & Plagiarism System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-BCrypt-brightgreen?style=for-the-badge&logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-green?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)

**A full-stack Spring Boot web application for tracking academic integrity and detecting plagiarism in student submissions.**

</div>

---

## 📖 About The Project

AIPS (Academic Integrity & Plagiarism System) is a web-based platform designed to uphold academic integrity by detecting plagiarism in student submissions, tracking misconduct cases, and maintaining institutional records. It ensures fairness, transparency, and accountability in academic work.

The system allows:
- **Admins** to manage users, courses, and view all reports
- **Lecturers** to monitor student submissions and plagiarism results
- **Students** to upload assignments and view their reports

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 Role-Based Auth | Secure login for Admin, Lecturer, and Student roles |
| 📊 Dashboard | Real-time statistics and overview cards |
| 📤 File Upload | Students upload PDF or DOCX assignments |
| 🔍 Plagiarism Detection | Automatic similarity scoring with visual progress bars |
| ⚠️ Misconduct Tracking | Case management with status updates (Pending → Resolved) |
| 🗺️ Province Filter | Filter users by Rwanda province code or name |
| 📄 Pagination & Sorting | Efficient data loading with multi-column sorting |
| 🔎 Search | Search users, courses, and submissions |
| 📱 Responsive UI | Works on desktop, tablet, and mobile |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + BCrypt |
| Templates | Thymeleaf |
| Database | MySQL 8 (Production) / H2 (Development) |
| Build Tool | Apache Maven |
| Frontend | HTML5, CSS3, Vanilla JavaScript |

---

## 🗄️ Database Design — ERD

The system uses **9 tables** with clearly defined relationships:

```
provinces ──────────────────────────────────────────────────┐
    │ (One-to-Many)                                          │
    ▼                                                        │
  users ◄──────── roles                                      │
    │  └──────────────────────────────────────────────────── ┘
    │ (Many-to-Many)
    ▼
user_courses ◄──── courses
                      │ (One-to-Many)
                      ▼
                 submissions ──(One-to-One)──► plagiarism_reports
                                                      │
                                              (One-to-One)
                                                      ▼
                                             misconduct_cases

users ──(One-to-Many)──► notifications
```

| Table | Purpose |
|---|---|
| `provinces` | Rwanda geographic provinces |
| `roles` | User roles: ADMIN, LECTURER, STUDENT |
| `users` | All system users |
| `courses` | Academic courses/modules |
| `user_courses` | Join table — Many-to-Many enrollment |
| `submissions` | Uploaded student assignments |
| `plagiarism_reports` | Similarity analysis results |
| `misconduct_cases` | Academic violation tracking |
| `notifications` | System alerts and messages |

---

## 🔗 JPA Relationships Implemented

### 1️⃣ One-to-One — `Submission` ↔ `PlagiarismReport`
Each submission has **exactly one** plagiarism report. The FK lives in `plagiarism_reports` with a `UNIQUE` constraint.

```java
// In Submission.java
@OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
private PlagiarismReport plagiarismReport;

// In PlagiarismReport.java
@OneToOne
@JoinColumn(name = "submission_id", unique = true)
private Submission submission;
```

---

### 2️⃣ One-to-Many — `Province` → `Users`
One province can have **many users**. The `users` table stores the `province_id` FK.

```java
// In Province.java
@OneToMany(mappedBy = "province", cascade = CascadeType.ALL)
private List<User> users;

// In User.java
@ManyToOne
@JoinColumn(name = "province_id")
private Province province;
```

---

### 3️⃣ Many-to-Many — `Users` ↔ `Courses`
Students enroll in many courses; courses have many students. JPA creates the `user_courses` join table automatically.

```java
// In User.java
@ManyToMany
@JoinTable(
    name = "user_courses",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id")
)
private Set<Course> courses;

// In Course.java
@ManyToMany(mappedBy = "courses")
private Set<User> enrolledStudents;
```

---

## 📄 Data Saving — Spring Data JPA

Entities map directly to database tables. Spring Data JPA generates SQL automatically from annotations:

```java
// @Entity tells JPA this class is a database table
// @Id + @GeneratedValue handles auto-increment primary keys
// Calling .save() generates an INSERT or UPDATE statement

userRepository.save(user);
// Generated SQL: INSERT INTO users (full_name, email, province_id, ...) VALUES (?, ?, ?, ...)
```

---

## 🔄 Pagination & Sorting

```java
// Build a Pageable — combines page number, page size, and sort direction
Pageable pageable = PageRequest.of(
    0,                            // Page number (0 = first page)
    10,                           // Records per page
    Sort.by("fullName").ascending() // Sort field and direction
);

// Returns Page<T> with records + metadata
Page<User> page = userRepository.findAll(pageable);

page.getContent();       // The 10 records for this page
page.getTotalPages();    // Total number of pages
page.getTotalElements(); // Total records in the database
page.getNumber();        // Current page number
```

> **Why Pagination?** Loading 10 records instead of thousands reduces memory usage, speeds up response time, and improves overall system performance.

---

## ✅ existsBy() — Duplicate Prevention

```java
// Spring Data JPA generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
// Returns true/false WITHOUT loading the full entity — very efficient

boolean exists = userRepository.existsByEmail(email);

if (userRepository.existsByEmail(user.getEmail())) {
    throw new RuntimeException("Email already exists!");
}
```

Used across the system to prevent:
- Duplicate user emails
- Duplicate student IDs
- Duplicate course codes
- Duplicate province codes

---

## 🗺️ Province Filtering — Custom JPQL Queries

```java
// Filter by province CODE — e.g., "KGL" for Kigali
@Query("SELECT u FROM User u WHERE u.province.provinceCode = :provinceCode")
List<User> findUsersByProvinceCode(@Param("provinceCode") String provinceCode);

// Filter by province NAME — case-insensitive partial match
@Query("SELECT u FROM User u WHERE LOWER(u.province.provinceName) " +
       "LIKE LOWER(CONCAT('%', :provinceName, '%'))")
List<User> findUsersByProvinceName(@Param("provinceName") String provinceName);
```

> The dot notation `u.province.provinceCode` navigates the `User → Province` Many-to-One relationship directly in JPQL without writing a manual SQL JOIN.

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+ *(or use built-in H2 for quick dev testing)*

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/THIERRO1/midterm_26227_B.git
cd midterm_26227_B

# 2. (Optional) Switch to MySQL
#    Open: src/main/resources/application.properties
#    Uncomment the MySQL lines and fill in your credentials
#    Comment out the H2 lines

# 3. Build the project
mvn clean install

# 4. Run the application
mvn spring-boot:run

# 5. Open in browser
# http://localhost:8080
```

### Default Login Credentials

| Role | Email | Password |
|---|---|---|
| 🔴 Admin | `admin@aips.ac.rw` | `admin123` |
| 🟡 Lecturer | `jb.nkusi@aips.ac.rw` | `lecturer123` |
| 🟢 Student | `alice.uwimana@student.aips.ac.rw` | `student123` |

---

## 📁 Project Structure

```
midterm_26227_B/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/academic/integrity/
        │   ├── AipsApplication.java          # Entry point + DB seeder
        │   ├── config/
        │   │   └── SecurityConfig.java       # Spring Security setup
        │   ├── controller/
        │   │   ├── DashboardController.java
        │   │   ├── UserController.java
        │   │   ├── CourseController.java
        │   │   ├── SubmissionController.java
        │   │   ├── ReportController.java
        │   │   └── MisconductCaseController.java
        │   ├── service/
        │   │   ├── UserService.java          # existsBy, pagination, province filter
        │   │   └── SubmissionService.java    # File upload + plagiarism generation
        │   ├── repository/
        │   │   ├── UserRepository.java       # Custom @Query province methods
        │   │   ├── SubmissionRepository.java
        │   │   ├── PlagiarismReportRepository.java
        │   │   ├── MisconductCaseRepository.java
        │   │   ├── CourseRepository.java
        │   │   ├── ProvinceRepository.java
        │   │   └── RoleRepository.java
        │   └── entity/
        │       ├── Province.java
        │       ├── Role.java
        │       ├── User.java                 # Many-to-Many with Course
        │       ├── Course.java
        │       ├── Submission.java           # One-to-One with PlagiarismReport
        │       ├── PlagiarismReport.java
        │       ├── MisconductCase.java
        │       └── Notification.java
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/style.css             # Professional dark-navy theme
            │   └── js/app.js
            └── templates/
                ├── login.html
                ├── dashboard.html
                ├── layout.html               # Shared sidebar + topbar
                ├── users/                    # list, form, detail, by-province
                ├── courses/                  # list, form, detail
                ├── submissions/              # list, form, detail
                ├── reports/                  # list
                └── cases/                   # list, detail
```

---

## 📸 Pages Overview

| Page | URL | Description |
|---|---|---|
| Login | `/login` | Secure login with role detection |
| Dashboard | `/dashboard` | Statistics cards + recent activity |
| Users | `/users` | Paginated, sortable user table |
| Province Filter | `/users/by-province` | Filter users by province code or name |
| Courses | `/courses` | Course management with enrollment count |
| Submissions | `/submissions` | Assignment uploads with similarity scores |
| Plagiarism Reports | `/reports` | Sorted by similarity score |
| Misconduct Cases | `/cases` | Case tracking with status management |

---

## 🎯 Assessment Coverage

This project covers all **9 practical exam requirements**:

- ✅ ERD with 8+ tables and explained relationships
- ✅ Data saving via Spring Data JPA with explanation
- ✅ Sorting and Pagination using `PageRequest` + `Sort`
- ✅ Many-to-Many (`Users ↔ Courses` via `user_courses`)
- ✅ One-to-Many (`Province → Users`)
- ✅ One-to-One (`Submission ↔ PlagiarismReport`)
- ✅ `existsBy()` for duplicate checking across all entities
- ✅ Province filtering by code AND name using custom JPQL
- ✅ Clean code with comments ready for Viva-Voce explanation

---

## 👨‍💻 Author

**THIERRO1**
- GitHub: [@THIERRO1](https://github.com/THIERRO1)

---

## 📝 License

This project was developed as part of a **Database & Spring Boot Application Development** practical examination.

© 2026 University Academic Department. All Rights Reserved.
