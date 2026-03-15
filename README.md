# midterm_26227_B
A Spring Boot web application for tracking academic integrity and detecting plagiarism in student submissions. Features One-to-One, One-to-Many, and Many-to-Many JPA relationships, pagination, sorting, existsBy() validation, and province-based user filtering.

---

## 🛡️ AcademicGuard — Digital Academic Integrity & Plagiarism Tracking System

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![Maven](https://img.shields.io/badge/Maven-Build-red)

A full-stack Spring Boot REST API demonstrating all JPA relationships, pagination, sorting, existsBy() validation, and province-based user filtering.

---

## 🚀 Quick Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 1. Database Setup
```sql
mysql -u root -p
CREATE DATABASE plagiarism_tracker_db;
EXIT;
```

### 2. Configure Password
Open `src/main/resources/application.properties` and set:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 3. Run
```bash
mvn clean spring-boot:run
```

### 4. Test
- Swagger UI: http://localhost:8080/swagger-ui.html
- Stats: http://localhost:8080/api/stats
- Open `frontend/index.html` in browser for dashboard

---

## 🗄️ ERD — Database Tables

| Table | Relationship |
|-------|-------------|
| `department` | @OneToMany → courses |
| `course` | @ManyToOne → department |
| `users` | @OneToOne(mappedBy) → location |
| `location` | @OneToOne (OWNER, FK: user_id UNIQUE) |
| `submission` | @ManyToOne → user, course |
| `plagiarism_case` | @ManyToMany (OWNER) → submissions |
| `case_submissions` | Join table (Many-to-Many) |

---

## ✅ Assessment Requirements Coverage

| # | Requirement | Implementation |
|---|-------------|---------------|
| 1 | ERD with 5+ tables | 6 entity tables + 1 join table |
| 2 | Save Location | `POST /api/locations` — @Transactional + bidirectional sync |
| 3 | Sorting & Pagination | `GET /api/submissions/sorted`, `/paginated`, `/page` |
| 4 | Many-to-Many | PlagiarismCase ↔ Submission via `case_submissions` |
| 5 | One-to-Many | Department → Courses with cascade + orphanRemoval |
| 6 | One-to-One | User ↔ Location with unique FK |
| 7 | existsBy() | `existsByEmail()`, `existsByStudentId()`, `existsByEmailAndIdNot()` |
| 8 | Province Query | `GET /api/locations/province/code/{code}/users` + `/name/{name}/users` |
| 9 | Viva-Voce | Open `frontend/index.html` → Viva-Voce tab |

---

## 🧪 Postman Quick Test Guide

```bash
# Stats
GET http://localhost:8080/api/stats

# Users
GET  http://localhost:8080/api/users
POST http://localhost:8080/api/users
Body: {"firstName":"Alice","lastName":"Test","email":"alice@test.com","studentId":"STU099","role":"STUDENT"}

# Save Location (Req #2)
POST http://localhost:8080/api/locations
Body: {"address":"KG 123 St","city":"Kigali","provinceName":"Kigali City","provinceCode":"KIG","country":"Rwanda","userId":1}

# Sorting (Req #3)
GET http://localhost:8080/api/submissions/sorted?sortBy=similarityScore&direction=desc

# Pagination (Req #3)
GET http://localhost:8080/api/submissions/paginated?page=0&size=5

# Pagination + Sorting (Req #3)
GET http://localhost:8080/api/submissions/page?page=0&size=5&sortBy=similarityScore&direction=desc

# Many-to-Many: Create case with submissions (Req #4)
POST http://localhost:8080/api/cases
Body: {"caseNumber":"CASE-TEST-001","description":"Test case","severity":"HIGH","status":"OPEN","investigatorId":11,"submissionIds":[1,2,3]}

# Add/Remove submission from case (Req #4)
POST   http://localhost:8080/api/cases/1/submissions/5
DELETE http://localhost:8080/api/cases/1/submissions/5

# One-to-Many: courses in department (Req #5)
GET http://localhost:8080/api/courses/department/1

# Province by code (Req #8)
GET http://localhost:8080/api/locations/province/code/KIG/users
GET http://localhost:8080/api/locations/province/code/SP/users

# Province by name (Req #8)
GET http://localhost:8080/api/locations/province/name/Kigali/users
GET http://localhost:8080/api/locations/province/name/southern/users

# existsBy demo — try duplicate email (Req #7)
POST http://localhost:8080/api/users
Body: {"firstName":"Bob","lastName":"Test","email":"alice.n@university.ac.rw","studentId":"STU999"}
# Returns 409 Conflict — email already exists
```

---

## 📁 Project Structure

```
AcademicGuard/
├── pom.xml
├── frontend/
│   └── index.html                  ← Dashboard + Viva-Voce answers
└── src/main/
    ├── resources/
    │   ├── application.properties  ← Set DB password here
    │   └── data.sql                ← 12 users, 20 submissions, 5 cases
    └── java/com/academic/plagiarism/
        ├── model/         Department, Course, User, Location, Submission, PlagiarismCase
        ├── repository/    UserRepository (existsBy), LocationRepository (@Query JOIN FETCH)
        ├── service/       Business logic, @Transactional
        ├── controller/    REST endpoints
        ├── dto/           Request/Response wrappers
        ├── exception/     GlobalExceptionHandler
        └── config/        Swagger + CORS
```
