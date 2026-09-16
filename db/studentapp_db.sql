-- =====================================================================
-- Βάση δεδομένων της εφαρμογής βαθμολογίου (PostgreSQL) - studentapp_db
-- Περιέχει: διαγραφή παλιών πινάκων, δημιουργία πινάκων, δοκιμαστικά δεδομένα.
--
-- Φόρτωση (ΠΡΟΣΟΧΗ: διαγράφει ό,τι υπάρχει ήδη στους πίνακες της βάσης):
--   psql -U <χρήστης> -d studentapp_db -f db/studentapp_db.sql
-- ή με Docker:
--   docker exec -i studentapp-db psql -U dani -d studentapp_db < db/studentapp_db.sql
-- =====================================================================

DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS secretaries CASCADE;
DROP TABLE IF EXISTS professors CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS departments CASCADE;

-- ---------------------------------------------------------------------
-- Πίνακες
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS departments (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    salt       VARCHAR(50)  NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    surname    VARCHAR(50)  NOT NULL,
    department_id BIGINT    NOT NULL REFERENCES departments (id),
    role       VARCHAR(20)  NOT NULL
               CHECK (role IN ('STUDENT', 'PROFESSOR', 'SECRETARY'))
);

CREATE TABLE IF NOT EXISTS students (
    user_id             BIGINT  PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    registration_number INTEGER NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS professors (
    user_id      BIGINT      PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    professor_id VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS secretaries (
    user_id         BIGINT      PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    employee_number VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS courses (
    id                BIGSERIAL    PRIMARY KEY,
    course_code       VARCHAR(20)  NOT NULL UNIQUE,
    course_name       VARCHAR(150) NOT NULL,
    ects              INTEGER      NOT NULL CHECK (ects > 0),
    semester          INTEGER      NOT NULL CHECK (semester BETWEEN 1 AND 8),
    department_id     BIGINT       NOT NULL REFERENCES departments (id),
    professor_user_id BIGINT       REFERENCES professors (user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS enrollments (
    student_user_id BIGINT NOT NULL REFERENCES students (user_id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES courses (id)       ON DELETE CASCADE,
    enrolled_on     DATE   NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (student_user_id, course_id)
);

CREATE TABLE IF NOT EXISTS grades (
    id              BIGSERIAL    PRIMARY KEY,
    student_user_id BIGINT       NOT NULL REFERENCES students (user_id) ON DELETE CASCADE,
    course_id       BIGINT       NOT NULL REFERENCES courses (id)       ON DELETE CASCADE,
    grade_value     NUMERIC(4,2) NOT NULL CHECK (grade_value >= 0 AND grade_value <= 10),
    graded_on       DATE         NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT uq_grade_per_student_course UNIQUE (student_user_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_users_department   ON users   (department_id);
CREATE INDEX IF NOT EXISTS idx_courses_department ON courses (department_id);
CREATE INDEX IF NOT EXISTS idx_courses_professor  ON courses (professor_user_id);
CREATE INDEX IF NOT EXISTS idx_grades_student     ON grades  (student_user_id);
CREATE INDEX IF NOT EXISTS idx_grades_course      ON grades  (course_id);

-- ---------------------------------------------------------------------
-- Δοκιμαστικά δεδομένα
-- ---------------------------------------------------------------------

INSERT INTO departments (id, code, name) VALUES
    (1, 'INF', 'Informatics'),
    (2, 'DS',  'Digital Systems'),
    (3, 'MATH','Mathematics');

-- Οι κωδικοί αποθηκεύονται κρυπτογραφημένοι (PBKDF2-HmacSHA256 hash + τυχαίο salt ανά χρήστη).
-- Δοκιμαστικοί κωδικοί: gramm1 / gramm123, gramm2 / gramm456, καθηγητές: prof123, φοιτητές: stud123
INSERT INTO users (id, username, password_hash, salt, name, surname, department_id, role) VALUES
    (1, 'gramm1',     'xZRtmqDrNQNtL3NzM/uywnHP1vz6qjhGPA3PKWTH8m8=', '8HAGFHJKxV5zNdU61kk1Pg==', 'Irini',    'Vlachou',    1, 'SECRETARY'),
    (2, 'gramm2',     '/RQeYyxzDUoZbFQVN9ENnHYugNh2L06kxiJlFatcYBE=', 'vLICfS0qr4B4JnVlAK57jA==', 'Petros',   'Antoniou',   2, 'SECRETARY');

INSERT INTO users (id, username, password_hash, salt, name, surname, department_id, role) VALUES
    (10, 'kkosta',     'nN9oyFK1kV5o8vupmkz0qr9PO44bGSFs/3YdeGmSPTo=', 'UU772aC7/vFj2LjdH3ApyQ==', 'Kostas',   'Kostakis',   1, 'PROFESSOR'),
    (11, 'mpapa',      'ds+L2WAfrGNhvy/P6UenzZYhwMOkOGvaaIYFWc7myQg=', 'cqhBQOrfaq2QTi5PlkJQyA==', 'Maria',    'Papadaki',   1, 'PROFESSOR'),
    (12, 'gdimou',     'RAz6QmJGBtIlLQjDASIQXYSmJZLEvACxwbY67BLgrb8=', '+ENhRwDb8AvX+iJ9LBDvVQ==', 'Georgios', 'Dimou',      2, 'PROFESSOR'),
    (13, 'avasil',     'hs406rs0cRWNqhw82PE45jwiO4OAaZpPq5eJJNNMBUw=', '+MYjsqkksPnijGDXZ5/zJg==', 'Anna',     'Vasileiou',  3, 'PROFESSOR');

INSERT INTO users (id, username, password_hash, salt, name, surname, department_id, role) VALUES
    (20, 'sofia.n',    'Nt07TtieSnkjZIt1hwvUnhGwYUmiawJL2FMlt/jhOrU=', 'P1h3tBko+O2Ke2W1PBMVwA==', 'Sofia',    'Nikolaou',   1, 'STUDENT'),
    (21, 'giorgos.p',  'ZeBbA4mIb6WdqASGajtx+oGh/X0O+Y5UZ56anm8O5U4=', '6RrtQwTTmk3CMFU3KFS0Ow==', 'Giorgos',  'Pappas',     1, 'STUDENT'),
    (22, 'eleni.k',    'cRmBmT3hcipo1HBXB928b5Ew0Zvvlq/oj0Qw9NEpmq0=', 'S1AFqzHTdPCMdIvdk/y2jw==', 'Eleni',    'Karagianni', 1, 'STUDENT'),
    (23, 'dimitris.a', 'nOzgjDMIk0mRf+JVYYMagzqFyO6ZzcASQPubEwbRBi4=', 'ALM4CUY9gEHR7KapLyKbJg==', 'Dimitris', 'Athanasiou', 2, 'STUDENT'),
    (24, 'nikos.x',    '9fw+x77nPxhklkvQsXpremoGw0V+IFqH2KNiBZMVDv4=', 'fs6nKls7tdnAtSpxSPeLgg==', 'Nikos',    'Xenakis',    2, 'STUDENT'),
    (25, 'maria.l',    'oxZ0eUyzz627GcLJXHmsUDwlwFzGkbzJfW/WJRDxVag=', 'OyEEBAibQAZ6Nx6j6eWaKw==', 'Maria',    'Lamprou',    3, 'STUDENT'),
    (26, 'kostas.m',   'XrPsBC73m9R/NmPBGix5DzoV32Do0d4CjPNyoL3FpZ8=', '89SOm8a0PpwV3EITLItkxw==', 'Konstantinos', 'Makris',  1, 'STUDENT'),
    (27, 'anna.p',     'Gt2t2yxgdwjhLlV6usLdp6rznLqjiup3+RUWElH+q5g=', 'VVpkVVlaEyQ9WXParzx7SQ==', 'Anna',     'Petrou',     1, 'STUDENT'),
    (28, 'vasilis.t',  'VSVpKzgUo5+V7mOUV5oq3FziqrJ3CE5v3eOH0+3ksp8=', '7uSGyW40/FNpV9IIUj2WZw==', 'Vasilis',  'Theodorou',  2, 'STUDENT'),
    (29, 'christina.g','pcXULFc4xXdaMspFuqK9ahyzpbGHLP1xsd069c6p26s=', 'KIvxvhCKNt/Q7W+CH98HWA==', 'Christina','Georgiou',   1, 'STUDENT');

INSERT INTO secretaries (user_id, employee_number) VALUES
    (1, 'EMP-01'),
    (2, 'EMP-02');

INSERT INTO professors (user_id, professor_id) VALUES
    (10, 'PROF-01'),
    (11, 'PROF-02'),
    (12, 'PROF-03'),
    (13, 'PROF-04');

INSERT INTO students (user_id, registration_number) VALUES
    (20, 21001),
    (21, 21002),
    (22, 21003),
    (23, 21004),
    (24, 21005),
    (25, 21006),
    (26, 21007),
    (27, 21008),
    (28, 21009),
    (29, 21010);

-- Τα DS202 και INF106 δεν έχουν καθηγητή, ώστε η γραμματεία να έχει κάτι να αναθέσει.
INSERT INTO courses (id, course_code, course_name, ects, semester, department_id, professor_user_id) VALUES
    (100, 'INF101', 'Programmatismos sto Diadiktyo',      6, 5, 1, 10),
    (101, 'INF102', 'Vaseis Dedomenon',                   6, 4, 1, 11),
    (102, 'INF103', 'Domes Dedomenon',                    5, 3, 1, 10),
    (103, 'DS201',  'Leitourgika Systimata',              6, 4, 2, 12),
    (104, 'DS202',  'Diktya Ypologiston',                 5, 5, 2, NULL),
    (105, 'INF106', 'Techniti Noimosyni',                 6, 6, 1, NULL),
    (106, 'MATH301','Grammiki Algevra',                   5, 1, 3, 13),
    (107, 'INF110', 'Eisagogi ston Programmatismo',       6, 1, 1, 11),
    (108, 'INF120', 'Antikeimenostrafis Programmatismos', 6, 2, 1, 10),
    (109, 'DS210',  'Asfaleia Systimaton',                5, 7, 2, 12),
    (110, 'MATH201','Pithanotites kai Statistiki',        5, 2, 3, 13);

-- Κάθε καθηγητής έχει τουλάχιστον ένα μάθημα με βαθμούς και ένα με φοιτητές που περιμένουν βαθμό.
-- Η christina.g (29) δεν έχει ακόμα κανέναν βαθμό.
INSERT INTO enrollments (student_user_id, course_id) VALUES
    (20, 100), (20, 101), (20, 102), (20, 106), (20, 107), (20, 108),
    (21, 100), (21, 101), (21, 107), (21, 108), (21, 110),
    (22, 100), (22, 103), (22, 107), (22, 108),
    (23, 101), (23, 102), (23, 103), (23, 109),
    (24, 100), (24, 106), (24, 109),
    (25, 102), (25, 106), (25, 110),
    (26, 107), (26, 108), (26, 110),
    (27, 102), (27, 107),
    (28, 103), (28, 109),
    (29, 110);

INSERT INTO grades (student_user_id, course_id, grade_value) VALUES
    (20, 100, 8.50), (20, 101, 7.00), (20, 106, 6.00), (20, 107, 9.00), (20, 108, 7.50),
    (21, 100, 4.50), (21, 107, 5.50), (21, 108, 3.00),
    (22, 100, 9.00), (22, 107, 10.00), (22, 108, 8.00),
    (23, 101, 6.50), (23, 102, 5.00), (23, 109, 7.00),
    (24, 106, 7.50), (24, 109, 6.00),
    (25, 106, 8.00),
    (26, 107, 7.00), (26, 108, 6.50),
    (27, 107, 4.00),
    (28, 109, 8.50);

-- Οι μετρητές των id συνεχίζουν μετά το μεγαλύτερο id που υπάρχει
SELECT setval('departments_id_seq', (SELECT MAX(id) FROM departments));
SELECT setval('users_id_seq',       (SELECT MAX(id) FROM users));
SELECT setval('courses_id_seq',     (SELECT MAX(id) FROM courses));
SELECT setval('grades_id_seq',      (SELECT MAX(id) FROM grades));
