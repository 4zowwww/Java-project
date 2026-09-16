package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Professors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfessorRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfessorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Professors> findAll() {
        return jdbcTemplate.query("""
                SELECT u.id, u.username, u.name, u.surname, d.name AS department, p.professor_id
                FROM professors p
                JOIN users u ON u.id = p.user_id
                JOIN departments d ON d.id = u.department_id
                ORDER BY u.surname, u.name
                """, professorRowMapper());
    }

    public Optional<Professors> findById(Long userId) {
        return jdbcTemplate.query("""
                SELECT u.id, u.username, u.name, u.surname, d.name AS department, p.professor_id
                FROM professors p
                JOIN users u ON u.id = p.user_id
                JOIN departments d ON d.id = u.department_id
                WHERE p.user_id = ?
                """, professorRowMapper(), userId)
                .stream()
                .findFirst();
    }

    private RowMapper<Professors> professorRowMapper() {
        return (rs, rowNum) -> {
            Professors professor = new Professors();
            professor.setId(rs.getLong("id"));
            professor.setUsername(rs.getString("username"));
            professor.setName(rs.getString("name"));
            professor.setSurname(rs.getString("surname"));
            professor.setDepartment(rs.getString("department"));
            professor.setProfessorId(rs.getString("professor_id"));
            return professor;
        };
    }
}
