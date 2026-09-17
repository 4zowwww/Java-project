-- Δημιουργια των πινακων της βασης.
-- Εκτελειται σε καθε εκκινηση της εφαρμογης, αλλα δημιουργει μονο οσους πινακες ΔΕΝ υπαρχουν,
-- οποτε τα δεδομενα που αποθηκευουν οι χρηστες (π.χ. βαθμοι, αναθεσεις) δεν χανονται.

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

-- [upgrade-start]
-- Αναβαθμιση βασης που ειχε δημιουργηθει με το σχημα της 2ης ασκησης
-- (κωδικος χωρις κρυπτογραφηση, μαθηματα χωρις εξαμηνο).
-- Σε νεα βαση οι εντολες αυτες δεν αλλαζουν τιποτα, γιατι οι στηλες υπαρχουν ηδη.
ALTER TABLE users   ADD COLUMN IF NOT EXISTS password_hash VARCHAR(100);
ALTER TABLE users   ADD COLUMN IF NOT EXISTS salt          VARCHAR(50);
ALTER TABLE users   DROP COLUMN IF EXISTS password;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS semester      INTEGER;
-- [upgrade-end]

CREATE INDEX IF NOT EXISTS idx_users_department   ON users   (department_id);
CREATE INDEX IF NOT EXISTS idx_courses_department ON courses (department_id);
CREATE INDEX IF NOT EXISTS idx_courses_professor  ON courses (professor_user_id);
CREATE INDEX IF NOT EXISTS idx_grades_student     ON grades  (student_user_id);
CREATE INDEX IF NOT EXISTS idx_grades_course      ON grades  (course_id);
