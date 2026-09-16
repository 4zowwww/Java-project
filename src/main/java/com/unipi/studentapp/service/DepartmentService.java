package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Departments;
import com.unipi.studentapp.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService
{

    private final DepartmentRepository departmentRepository;


    public DepartmentService(DepartmentRepository departmentRepository)
    {
        this.departmentRepository = departmentRepository;
    }

    public List<Departments> findAll()
    {
        return departmentRepository.findAll();
    }
}
