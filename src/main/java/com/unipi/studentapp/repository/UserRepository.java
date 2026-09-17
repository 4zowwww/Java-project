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

    // Βρισκει τον χρηστη μονο με το username. Ο ελεγχος του κωδικου γινεται στο AuthService.
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

    // Χρησιμοποιειται ηδη αυτο το username;
    public boolean existsByUsername(String username)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count > 0;
    }

    // Υπαρχει ηδη φοιτητης με αυτον τον αριθμο μητρωου;
    public boolean existsRegistrationNumber(int registrationNumber)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students WHERE registration_number = ?", Integer.class, registrationNumber);
        return count > 0;
    }

    // Υπαρχει ηδη καθηγητης με αυτον τον κωδικο;
    public boolean existsProfessorId(String professorId)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM professors WHERE professor_id = ?", Integer.class, professorId);
        return count > 0;
    }

    // Προσθετει νεο χρηστη και επιστρεφει το id που του εδωσε η βαση
    public Long insertUser(String username, String passwordHash, String salt, String name, String surname, Long departmentId, String role)
    {
        return jdbcTemplate.queryForObject("""
                INSERT INTO users (username, password_hash, salt, name, surname, department_id, role)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Long.class, username, passwordHash, salt, name, surname, departmentId, role);
    }

    public void insertStudent(Long userId, int registrationNumber)
    {
        jdbcTemplate.update("INSERT INTO students (user_id, registration_number) VALUES (?, ?)", userId, registrationNumber);
    }

    public void insertProfessor(Long userId, String professorId)
    {
        jdbcTemplate.update("INSERT INTO professors (user_id, professor_id) VALUES (?, ?)", userId, professorId);
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
