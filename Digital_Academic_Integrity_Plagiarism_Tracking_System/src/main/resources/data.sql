-- ===========================
-- SAMPLE DATA FOR DEMO
-- (Run after tables are created by Hibernate)
-- ===========================

-- Departments (3)
INSERT IGNORE INTO department (id, name, code, description) VALUES
(1, 'Computer Science', 'CS', 'Department of Computer Science and Software Engineering'),
(2, 'Business Administration', 'BA', 'Department of Business and Management Studies'),
(3, 'Engineering', 'ENG', 'Department of Electrical and Mechanical Engineering');

-- Courses (6)
INSERT IGNORE INTO course (id, title, course_code, credits, department_id) VALUES
(1, 'Data Structures and Algorithms', 'CS301', 4, 1),
(2, 'Database Management Systems', 'CS401', 3, 1),
(3, 'Software Engineering', 'CS450', 3, 1),
(4, 'Principles of Management', 'BA201', 3, 2),
(5, 'Financial Accounting', 'BA310', 3, 2),
(6, 'Circuit Analysis', 'ENG201', 4, 3);

-- Users (12)
INSERT IGNORE INTO users (id, first_name, last_name, email, student_id, role, created_at) VALUES
(1,  'Alice',   'Nkurunziza', 'alice.n@university.ac.rw',   'STU001', 'STUDENT',   NOW()),
(2,  'Bob',     'Habimana',   'bob.h@university.ac.rw',     'STU002', 'STUDENT',   NOW()),
(3,  'Claire',  'Uwimana',    'claire.u@university.ac.rw',  'STU003', 'STUDENT',   NOW()),
(4,  'David',   'Mutabazi',   'david.m@university.ac.rw',   'STU004', 'STUDENT',   NOW()),
(5,  'Eve',     'Ingabire',   'eve.i@university.ac.rw',     'STU005', 'STUDENT',   NOW()),
(6,  'Frank',   'Bizimana',   'frank.b@university.ac.rw',   'STU006', 'STUDENT',   NOW()),
(7,  'Grace',   'Mukamana',   'grace.m@university.ac.rw',   'STU007', 'STUDENT',   NOW()),
(8,  'Henry',   'Nsanzimana', 'henry.ns@university.ac.rw',  'STU008', 'STUDENT',   NOW()),
(9,  'Iris',    'Kayitesi',   'iris.k@university.ac.rw',    'STU009', 'STUDENT',   NOW()),
(10, 'James',   'Niyonzima',  'james.n@university.ac.rw',   'STU010', 'STUDENT',   NOW()),
(11, 'Dr. Keza','Uwera',      'keza.uwera@university.ac.rw','FAC001', 'INSTRUCTOR', NOW()),
(12, 'Admin',   'System',     'admin@university.ac.rw',     'ADM001', 'ADMIN',     NOW());

-- Locations (12) – different Rwandan provinces
INSERT IGNORE INTO location (id, address, city, province_name, province_code, country, user_id) VALUES
(1,  'KG 123 St', 'Kigali',    'Kigali City',   'KIG', 'Rwanda', 1),
(2,  'KN 45 Ave', 'Kigali',    'Kigali City',   'KIG', 'Rwanda', 2),
(3,  'NB 78 Rd',  'Huye',      'Southern Province', 'SP', 'Rwanda', 3),
(4,  'EP 12 Blvd','Rwamagana', 'Eastern Province',  'EP', 'Rwanda', 4),
(5,  'WP 56 Ln',  'Rubavu',    'Western Province',  'WP', 'Rwanda', 5),
(6,  'NP 90 Way', 'Musanze',   'Northern Province', 'NP', 'Rwanda', 6),
(7,  'KG 234 St', 'Kigali',    'Kigali City',   'KIG', 'Rwanda', 7),
(8,  'SP 11 Ave', 'Butare',    'Southern Province', 'SP', 'Rwanda', 8),
(9,  'EP 67 Rd',  'Kayonza',   'Eastern Province',  'EP', 'Rwanda', 9),
(10, 'WP 23 Blvd','Kibuye',    'Western Province',  'WP', 'Rwanda', 10),
(11, 'KG 567 St', 'Kigali',    'Kigali City',   'KIG', 'Rwanda', 11),
(12, 'KG 789 Ave','Kigali',    'Kigali City',   'KIG', 'Rwanda', 12);

-- Submissions (20)
INSERT IGNORE INTO submission (id, title, content, similarity_score, status, submitted_at, user_id, course_id) VALUES
(1,  'DSA Assignment 1',       'Content about sorting algorithms...',  0.12, 'CLEAN',       NOW(), 1, 1),
(2,  'DSA Assignment 2',       'Content about graph traversal...',     0.78, 'FLAGGED',     NOW(), 2, 1),
(3,  'DB Design Report',       'Content about normalization...',       0.91, 'PLAGIARIZED', NOW(), 3, 2),
(4,  'SE Project Proposal',    'Content about agile methodology...',   0.05, 'CLEAN',       NOW(), 4, 3),
(5,  'Management Essay',       'Content about leadership styles...',   0.65, 'FLAGGED',     NOW(), 5, 4),
(6,  'Financial Analysis',     'Content about balance sheets...',      0.88, 'PLAGIARIZED', NOW(), 6, 5),
(7,  'Circuit Lab Report',     'Content about Ohm law...',             0.15, 'CLEAN',       NOW(), 7, 6),
(8,  'DSA Final Project',      'Content about dynamic programming...', 0.45, 'PENDING',     NOW(), 8, 1),
(9,  'DB Implementation',      'Content about SQL queries...',         0.72, 'FLAGGED',     NOW(), 9, 2),
(10, 'SE Requirements Doc',    'Content about use cases...',           0.08, 'CLEAN',       NOW(), 10,3),
(11, 'Algorithms Research',    'Content about complexity...',          0.55, 'PENDING',     NOW(), 1, 1),
(12, 'Database Security',      'Content about encryption...',          0.82, 'PLAGIARIZED', NOW(), 2, 2),
(13, 'Software Testing',       'Content about unit tests...',          0.18, 'CLEAN',       NOW(), 3, 3),
(14, 'Business Strategy',      'Content about market analysis...',     0.69, 'FLAGGED',     NOW(), 4, 4),
(15, 'Accounting Report',      'Content about income statements....',  0.33, 'CLEAN',       NOW(), 5, 5),
(16, 'Digital Circuits',       'Content about logic gates...',         0.77, 'FLAGGED',     NOW(), 6, 6),
(17, 'OOP Assignment',         'Content about inheritance...',         0.94, 'PLAGIARIZED', NOW(), 7, 1),
(18, 'Network Design',         'Content about topology...',            0.41, 'PENDING',     NOW(), 8, 3),
(19, 'Cost Accounting',        'Content about variance analysis...',   0.62, 'FLAGGED',     NOW(), 9, 5),
(20, 'Machine Learning Intro', 'Content about neural networks...',     0.21, 'CLEAN',       NOW(), 10,1);

-- Plagiarism Cases (5)
INSERT IGNORE INTO plagiarism_case (id, case_number, description, severity, status, created_at, investigator_id) VALUES
(1, 'CASE-2024-001', 'High similarity detected between DB submissions', 'HIGH',   'OPEN',       NOW(), 11),
(2, 'CASE-2024-002', 'Management essays share identical paragraphs',    'MEDIUM', 'UNDER_REVIEW',NOW(), 11),
(3, 'CASE-2024-003', 'Financial analysis copied from online source',    'HIGH',   'RESOLVED',   NOW(), 11),
(4, 'CASE-2024-004', 'OOP assignment matches previous year submission', 'CRITICAL','OPEN',       NOW(), 11),
(5, 'CASE-2024-005', 'Multiple DSA submissions share code blocks',      'LOW',    'DISMISSED',  NOW(), 12);

-- Many-to-Many: case_submissions join table
INSERT IGNORE INTO case_submissions (plagiarism_case_id, submission_id) VALUES
(1, 3), (1, 9), (1, 12),
(2, 5), (2, 14),
(3, 6),
(4, 17), (4, 2),
(5, 8), (5, 11), (5, 20);
