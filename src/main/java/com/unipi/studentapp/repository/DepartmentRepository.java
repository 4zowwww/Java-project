package com.unipi.studentapp.repository;

import com.unipi.studentapp.model.Departments;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRepository
{

    private final JdbcTemplate jdbcTemplate;


    public DepartmentRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Departments> findAll()
    {
        return jdbcTemplate.query("""
                SELECT id, code, name
                FROM departments
                ORDER BY name
                """, departmentRowMapper());
    }

    // Υπάρχει τμήμα με αυτό το id;
    public boolean existsById(Long departmentId)
    {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM departments WHERE id = ?", Integer.class, departmentId);
        return count > 0;
    }

    private RowMapper<Departments> departmentRowMapper()
    {
        return (rs, rowNum) ->
        {
            Departments department = new Departments();
            department.setId(rs.getLong("id"));
            department.setCode(rs.getString("code"));
            department.setName(rs.getString("name"));
            return department;
        };
    }
}
