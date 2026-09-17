package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.Professors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository
{

    // Κοινο SELECT για ολες τις αναζητησεις μαθηματων (μαζι με τον υπευθυνο καθηγητη, αν υπαρχει).
    // Καθε μεθοδος προσθετει στο τελος το δικο της WHERE / ORDER BY.
    private static final String SELECT_COURSES = """
            SELECT c.id, c.course_code, c.course_name, c.ects, c.semester,
                   cd.name      AS course_department,
                   u.id         AS professor_user_id,
                   u.username   AS professor_username,
                   u.name       AS professor_name,
                   u.surname    AS professor_surname,
                   pd.name      AS professor_department,
                   p.professor_id
            FROM courses c
            JOIN departments cd ON cd.id = c.department_id
            LEFT JOIN professors  p  ON p.user_id = c.professor_user_id
            LEFT JOIN users       u  ON u.id      = p.user_id
            LEFT JOIN departments pd ON pd.id     = u.department_id
            """;

    private final JdbcTemplate jdbcTemplate;


    public CourseRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Courses> findAll()
    {
        return jdbcTemplate.query(SELECT_COURSES + """
                ORDER BY c.semester, c.course_code
                """, courseRowMapper());
    }

    public Optional<Courses> findById(Long courseId)
    {
        return jdbcTemplate.query(SELECT_COURSES + """
                WHERE c.id = ?
                """, courseRowMapper(), courseId)
                .stream()
                .findFirst();
    }

    // Τα μαθηματα του καθηγητη που εχουν τουλαχιστον εναν βαθμο
    public List<Courses> findGradedByProfessor(Long professorUserId)
    {
        return jdbcTemplate.query(SELECT_COURSES + """
                WHERE c.professor_user_id = ?
                  AND EXISTS (SELECT 1 FROM grades g WHERE g.course_id = c.id)
                ORDER BY c.semester, c.course_code
                """, courseRowMapper(), professorUserId);
    }

    // Τα μαθηματα του καθηγητη που εχουν εγγεγραμμενους φοιτητες χωρις βαθμο
    public List<Courses> findToGradeByProfessor(Long professorUserId)
    {
        return jdbcTemplate.query(SELECT_COURSES + """
                WHERE c.professor_user_id = ?
                  AND EXISTS (SELECT 1 FROM enrollments e
                              WHERE e.course_id = c.id
                                AND NOT EXISTS (SELECT 1 FROM grades g
                                                WHERE g.course_id = e.course_id
                                                  AND g.student_user_id = e.student_user_id))
                ORDER BY c.semester, c.course_code
                """, courseRowMapper(), professorUserId);
    }

    public int assignProfessor(Long courseId, Long professorUserId)
    {
        return jdbcTemplate.update("""
                UPDATE courses
                SET professor_user_id = ?
                WHERE id = ?
                """, professorUserId, courseId);
    }

    // Υπαρχει ηδη μαθημα με αυτον τον κωδικο;
    public boolean existsByCode(String courseCode)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses WHERE course_code = ?", Integer.class, courseCode);
        return count > 0;
    }

    // Νεο μαθημα (ο καθηγητης μπορει να ειναι null)
    public void insert(String courseCode, String courseName, int ects, int semester, Long departmentId, Long professorUserId)
    {
        jdbcTemplate.update("""
                INSERT INTO courses (course_code, course_name, ects, semester, department_id, professor_user_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """, courseCode, courseName, ects, semester, departmentId, professorUserId);
    }

    // Ειναι ο φοιτητης ηδη εγγεγραμμενος στο μαθημα;
    public boolean isEnrolled(Long studentUserId, Long courseId)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollments WHERE student_user_id = ? AND course_id = ?", Integer.class, studentUserId, courseId);
        return count > 0;
    }

    // Εγγραφη φοιτητη σε μαθημα (δηλαδη στη λιστα φοιτητων προς βαθμολογηση)
    public void enroll(Long studentUserId, Long courseId)
    {
        jdbcTemplate.update("INSERT INTO enrollments (student_user_id, course_id) VALUES (?, ?)", studentUserId, courseId);
    }

    private RowMapper<Courses> courseRowMapper()
    {
        return (rs, rowNum) ->
        {
            Courses course = new Courses();
            course.setId(rs.getLong("id"));
            course.setCourseCode(rs.getString("course_code"));
            course.setCourseName(rs.getString("course_name"));
            course.setEcts(rs.getInt("ects"));
            course.setSemester(rs.getInt("semester"));
            course.setDepartment(rs.getString("course_department"));

            long professorUserId = rs.getLong("professor_user_id");
            if (!rs.wasNull())
            {
                Professors professor = new Professors();
                professor.setId(professorUserId);
                professor.setUsername(rs.getString("professor_username"));
                professor.setName(rs.getString("professor_name"));
                professor.setSurname(rs.getString("professor_surname"));
                professor.setDepartment(rs.getString("professor_department"));
                professor.setProfessorId(rs.getString("professor_id"));
                course.setProfessor(professor);
            }

            return course;
        };
    }
}
