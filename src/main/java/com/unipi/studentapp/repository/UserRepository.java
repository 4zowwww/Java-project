package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Professors;
import com.unipi.studentapp.model.Secretaries;
import com.unipi.studentapp.model.Students;
import com.unipi.studentapp.model.Users;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository
{

    private final JdbcTemplate jdbcTemplate;


    public UserRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Βρίσκει τον χρήστη μόνο με το username. Ο έλεγχος του κωδικού γίνεται στο AuthService.
    public Optional<Users> findByUsername(String username)
    {
        return jdbcTemplate.query("""
                SELECT u.id, u.username, u.password_hash, u.salt, u.name, u.surname, u.role,
                       d.name AS department,
                       s.registration_number,
                       p.professor_id,
                       g.employee_number
                FROM users u
                JOIN departments d ON d.id = u.department_id
                LEFT JOIN students    s ON s.user_id = u.id
                LEFT JOIN professors  p ON p.user_id = u.id
                LEFT JOIN secretaries g ON g.user_id = u.id
                WHERE u.username = ?
                """, userRowMapper(), username)
                .stream()
                .findFirst();
    }

    private RowMapper<Users> userRowMapper()
    {
        return (rs, rowNum) ->
        {
            String role = rs.getString("role");

            Users user = switch (role)
            {
                case Users.ROLE_STUDENT ->
                {
                    Students student = new Students();
                    student.setRegistrationNumber(rs.getInt("registration_number"));
                    yield student;
                }
                case Users.ROLE_PROFESSOR ->
                {
                    Professors professor = new Professors();
                    professor.setProfessorId(rs.getString("professor_id"));
                    yield professor;
                }
                case Users.ROLE_SECRETARY ->
                {
                    Secretaries secretary = new Secretaries();
                    secretary.setEmployeeNumber(rs.getString("employee_number"));
                    yield secretary;
                }
                default -> new Users();
            };

            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setSalt(rs.getString("salt"));
            user.setName(rs.getString("name"));
            user.setSurname(rs.getString("surname"));
            user.setDepartment(rs.getString("department"));
            user.setRole(role);

            return user;
        };
    }
}
