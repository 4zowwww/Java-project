package com.unipi.studentapp.controller;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.service.AuthService;
import com.unipi.studentapp.service.CourseService;
import com.unipi.studentapp.service.DepartmentService;
import com.unipi.studentapp.service.ProfessorService;
import com.unipi.studentapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Λειτουργιες Γραμματειας
@Controller
@RequestMapping("/secretary")
public class SecretaryController
{

    private final AuthService authService;
    private final CourseService courseService;
    private final ProfessorService professorService;
    private final UserService userService;
    private final DepartmentService departmentService;


    public SecretaryController(AuthService authService, CourseService courseService, ProfessorService professorService, UserService userService, DepartmentService departmentService)
    {
        this.authService = authService;
        this.courseService = courseService;
        this.professorService = professorService;
        this.userService = userService;
        this.departmentService = departmentService;
    }

    // ---------- Προβολη μαθηματων ----------

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

    // ---------- Αναθεση μαθηματος σε καθηγητη ----------

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

    // ---------- Δημιουργια φοιτητων και καθηγητων ----------

    @GetMapping("/create-user")
    public String createUserForm(HttpSession session, Model model)
    {
        fillCreateUserPage(session, model);
        return "secretary/create-user";
    }

    @PostMapping("/create-student")
    public String createStudent(@RequestParam(required = false) String username, @RequestParam(required = false) String password, @RequestParam(required = false) String name, @RequestParam(required = false) String surname, @RequestParam(required = false) Long departmentId, @RequestParam(required = false) String registrationNumber, HttpSession session, Model model)
    {
        try
        {
            model.addAttribute("success", userService.createStudent(username, password, name, surname, departmentId, registrationNumber));
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
        }

        fillCreateUserPage(session, model);
        return "secretary/create-user";
    }

    @PostMapping("/create-professor")
    public String createProfessor(@RequestParam(required = false) String username, @RequestParam(required = false) String password, @RequestParam(required = false) String name, @RequestParam(required = false) String surname, @RequestParam(required = false) Long departmentId, @RequestParam(required = false) String professorId, HttpSession session, Model model)
    {
        try
        {
            model.addAttribute("success", userService.createProfessor(username, password, name, surname, departmentId, professorId));
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
        }

        fillCreateUserPage(session, model);
        return "secretary/create-user";
    }

    // Τα δεδομενα της σελιδας δημιουργιας χρηστων (κοινο για GET και POST)
    private void fillCreateUserPage(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("departments", departmentService.findAll());
    }

    // ---------- Δημιουργια μαθηματων ----------

    @GetMapping("/create-course")
    public String createCourseForm(HttpSession session, Model model)
    {
        fillCreateCoursePage(session, model);
        return "secretary/create-course";
    }

    @PostMapping("/create-course")
    public String createCourse(@RequestParam(required = false) String courseCode, @RequestParam(required = false) String courseName, @RequestParam(required = false) String ects, @RequestParam(required = false) String semester, @RequestParam(required = false) Long departmentId, @RequestParam(required = false) Long professorUserId, HttpSession session, Model model)
    {
        try
        {
            model.addAttribute("success", courseService.createCourse(courseCode, courseName, ects, semester, departmentId, professorUserId));
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
        }

        fillCreateCoursePage(session, model);
        return "secretary/create-course";
    }

    // Τα δεδομενα της σελιδας δημιουργιας μαθηματος (κοινο για GET και POST)
    private void fillCreateCoursePage(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("professors", professorService.findAll());
    }

    // ---------- Λιστες φοιτητων προς βαθμολογηση (εγγραφες σε μαθηματα) ----------

    @GetMapping("/enrollments")
    public String enrollments(@RequestParam(required = false) Long courseId, HttpSession session, Model model)
    {
        fillEnrollmentsPage(courseId, session, model);
        return "secretary/enrollments";
    }

    @PostMapping("/enrollments")
    public String enroll(@RequestParam(required = false) Long courseId, @RequestParam(required = false) Long studentUserId, HttpSession session, Model model)
    {
        try
        {
            model.addAttribute("success", courseService.enrollStudent(courseId, studentUserId));
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
        }

        fillEnrollmentsPage(courseId, session, model);
        return "secretary/enrollments";
    }

    // Τα δεδομενα της σελιδας εγγραφων (κοινο για GET και POST)
    private void fillEnrollmentsPage(Long courseId, HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        model.addAttribute("allCourses", courseService.findAll());

        if (courseId == null)
        {
            return;
        }

        Courses course = courseService.findById(courseId);

        if (course == null)
        {
            model.addAttribute("error", "Το μάθημα δεν βρέθηκε.");
            return;
        }

        model.addAttribute("selectedCourse", course);
        model.addAttribute("enrolledStudents", courseService.findEnrolledStudents(courseId));
        model.addAttribute("otherStudents", courseService.findStudentsNotEnrolled(courseId));
    }
}
