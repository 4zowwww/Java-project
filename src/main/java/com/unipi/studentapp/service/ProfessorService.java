package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Professors;
import com.unipi.studentapp.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<Professors> findAll() {
        return professorRepository.findAll();
    }
}
