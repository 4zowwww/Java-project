package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.Grades;
import com.unipi.studentapp.model.Students;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GradeRepository
{

    // Κοινό SELECT για τους βαθμούς, μαζί με τα στοιχεία του μαθήματος και του φοιτητή
    private static final String SELECT_GRADES = """
            SELECT g.id, g.grade_value,
                   c.id AS course_id, c.course_code, c.course_name, c.ects, c.semester,
                   u.id AS student_user_id, u.name, u.surname, s.registration_number
            FROM grades g
            JOIN courses  c ON c.id      = g.course_id
            JOIN students s ON s.user_id = g.student_user_id
            JOIN users    u ON u.id      = s.user_id
            """;

    private final JdbcTemplate jdbcTemplate;


    public GradeRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Οι βαθμοί ενός φοιτητή, ταξινομημένοι ανά εξάμηνο και μάθημα
    public List<Grades> findByStudent(Long studentUserId)
    {
        return jdbcTemplate.query(SELECT_GRADES + """
                WHERE g.student_user_id = ?
                ORDER BY c.semester, c.course_code
                """, gradeRowMapper(), studentUserId);
    }

    // Οι βαθμοί όλων των φοιτητών σε ένα μάθημα
    public List<Grades> findByCourse(Long courseId)
    {
        return jdbcTemplate.query(SELECT_GRADES + """
                WHERE g.course_id = ?
                ORDER BY u.surname, u.name
                """, gradeRowMapper(), courseId);
    }

    // Οι φοιτητές που είναι εγγεγραμμένοι σε ένα μάθημα αλλά δεν έχουν βαθμό ακόμα
    public List<Students> findUngradedStudents(Long courseId)
    {
        return jdbcTemplate.query("""
                SELECT u.id, u.username, u.name, u.surname, s.registration_number
                FROM enrollments e
                JOIN students s ON s.user_id = e.student_user_id
                JOIN users    u ON u.id      = s.user_id
                WHERE e.course_id = ?
                  AND NOT EXISTS (SELECT 1 FROM grades g
                                  WHERE g.course_id = e.course_id
                                    AND g.student_user_id = e.student_user_id)
                ORDER BY u.surname, u.name
                """, studentRowMapper(), courseId);
    }

    public void insert(Long studentUserId, Long courseId, double value)
    {
        jdbcTemplate.update("""
                INSERT INTO grades (student_user_id, course_id, grade_value)
                VALUES (?, ?, ?)
                """, studentUserId, courseId, value);
    }

    private RowMapper<Grades> gradeRowMapper()
    {
        return (rs, rowNum) ->
        {
            Courses course = new Courses();
            course.setId(rs.getLong("course_id"));
            course.setCourseCode(rs.getString("course_code"));
            course.setCourseName(rs.getString("course_name"));
            course.setEcts(rs.getInt("ects"));
            course.setSemester(rs.getInt("semester"));

            Students student = new Students();
            student.setId(rs.getLong("student_user_id"));
            student.setName(rs.getString("name"));
            student.setSurname(rs.getString("surname"));
            student.setRegistrationNumber(rs.getInt("registration_number"));

            Grades grade = new Grades();
            grade.setId(rs.getLong("id"));
            grade.setCourse(course);
            grade.setStudent(student);
            grade.setValue(rs.getDouble("grade_value"));

            return grade;
        };
    }

    private RowMapper<Students> studentRowMapper()
    {
        return (rs, rowNum) ->
        {
            Students student = new Students();
            student.setId(rs.getLong("id"));
            student.setUsername(rs.getString("username"));
            student.setName(rs.getString("name"));
            student.setSurname(rs.getString("surname"));
            student.setRegistrationNumber(rs.getInt("registration_number"));
            return student;
        };
    }
}
