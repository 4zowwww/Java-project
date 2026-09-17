package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Students;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepository
{

    // Κοινο SELECT για τους φοιτητες
    private static final String SELECT_STUDENTS = """
            SELECT u.id, u.username, u.name, u.surname, d.name AS department, s.registration_number
            FROM students s
            JOIN users u       ON u.id = s.user_id
            JOIN departments d ON d.id = u.department_id
            """;

    private final JdbcTemplate jdbcTemplate;


    public StudentRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Οι φοιτητες που ειναι εγγεγραμμενοι σε ενα μαθημα
    public List<Students> findEnrolled(Long courseId)
    {
        return jdbcTemplate.query(SELECT_STUDENTS + """
                WHERE s.user_id IN (SELECT e.student_user_id FROM enrollments e WHERE e.course_id = ?)
                ORDER BY u.surname, u.name
                """, studentRowMapper(), courseId);
    }

    // Οι φοιτητες που ΔΕΝ ειναι εγγεγραμμενοι σε ενα μαθημα (για να επιλεγουν στην εγγραφη)
    public List<Students> findNotEnrolled(Long courseId)
    {
        return jdbcTemplate.query(SELECT_STUDENTS + """
                WHERE s.user_id NOT IN (SELECT e.student_user_id FROM enrollments e WHERE e.course_id = ?)
                ORDER BY u.surname, u.name
                """, studentRowMapper(), courseId);
    }

    // Υπαρχει φοιτητης με αυτο το id;
    public boolean existsById(Long studentUserId)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students WHERE user_id = ?", Integer.class, studentUserId);
        return count > 0;
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
            student.setDepartment(rs.getString("department"));
            student.setRegistrationNumber(rs.getInt("registration_number"));
            return student;
        };
    }
}
