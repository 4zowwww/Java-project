package com.unipi.studentapp.controller;

import com.unipi.studentapp.model.Grades;
import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import com.unipi.studentapp.service.GradeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// Λειτουργιες Φοιτητων
@Controller
@RequestMapping("/student")
public class StudentController
{

    private final AuthService authService;
    private final GradeService gradeService;


    public StudentController(AuthService authService, GradeService gradeService)
    {
        this.authService = authService;
        this.gradeService = gradeService;
    }

    // Βαθμολογια ανα μαθημα
    @GetMapping("/grades")
    public String grades(HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);

        model.addAttribute("user", user);
        model.addAttribute("grades", gradeService.findStudentGrades(user.getId()));
        return "student/grades";
    }

    // Βαθμολογια ανα εξαμηνο
    @GetMapping("/grades-by-semester")
    public String gradesBySemester(HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);
        List<Grades> grades = gradeService.findStudentGrades(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("semesters", gradeService.groupBySemester(grades));
        return "student/grades-by-semester";
    }

    // Συνολικη βαθμολογια (ολα τα μαθηματα που εχει εξεταστει)
    @GetMapping("/summary")
    public String summary(HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);
        List<Grades> grades = gradeService.findStudentGrades(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("summary", gradeService.summarize(grades));
        return "student/summary";
    }
}
