package com.unipi.studentapp.controller;

import com.unipi.studentapp.service.AuthService;
import com.unipi.studentapp.service.CourseService;
import com.unipi.studentapp.service.ProfessorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Λειτουργίες Γραμματείας
@Controller
@RequestMapping("/secretary")
public class SecretaryController
{

    private final AuthService authService;
    private final CourseService courseService;
    private final ProfessorService professorService;


    public SecretaryController(AuthService authService, CourseService courseService, ProfessorService professorService)
    {
        this.authService = authService;
        this.courseService = courseService;
        this.professorService = professorService;
    }

    @GetMapping("/courses")
    public String courses(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("courses", courseService.findAll());
        return "secretary/courses";
    }

    @GetMapping("/courses-professors")
    public String coursesWithProfessors(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("courses", courseService.findAll());
        return "secretary/courses-professors";
    }

    @GetMapping("/assign")
    public String assignForm(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("allCourses", courseService.findAll());
        model.addAttribute("professors", professorService.findAll());
        return "secretary/assign";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam(required = false) Long courseId, @RequestParam(required = false) Long professorUserId, HttpSession session, Model model)
    {

        model.addAttribute("result", courseService.assignProfessorToCourse(courseId, professorUserId));
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("allCourses", courseService.findAll());
        model.addAttribute("professors", professorService.findAll());
        return "secretary/assign";
    }
}
