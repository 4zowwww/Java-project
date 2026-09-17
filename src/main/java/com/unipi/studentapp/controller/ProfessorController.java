package com.unipi.studentapp.controller;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import com.unipi.studentapp.service.CourseService;
import com.unipi.studentapp.service.GradeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Λειτουργιες Καθηγητων
@Controller
@RequestMapping("/professor")
public class ProfessorController
{

    private final AuthService authService;
    private final CourseService courseService;
    private final GradeService gradeService;


    public ProfessorController(AuthService authService, CourseService courseService, GradeService gradeService)
    {
        this.authService = authService;
        this.courseService = courseService;
        this.gradeService = gradeService;
    }

    // Λιστα βαθμολογιας ανα μαθημα (για μαθηματα που εχουν ηδη βαθμολογηθει)
    @GetMapping("/grades")
    public String grades(@RequestParam(required = false) Long courseId, HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findGradedCourses(user.getId()));

        // Αν εχει επιλεγει μαθημα, εμφανιζονται και οι βαθμοι του
        if (courseId != null)
        {
            Courses course = courseService.findProfessorCourse(user.getId(), courseId);

            if (course == null)
            {
                model.addAttribute("error", "Το μάθημα δεν βρέθηκε ή δεν σας έχει ανατεθεί.");
            }
            else
            {
                model.addAttribute("selectedCourse", course);
                model.addAttribute("grades", gradeService.findCourseGrades(courseId));
            }
        }
        return "professor/grades";
    }

    // Φορμα καταχωρησης βαθμολογιας (για μαθηματα με φοιτητες χωρις βαθμο)
    @GetMapping("/enter-grades")
    public String enterGradesForm(@RequestParam(required = false) Long courseId, HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);
        fillEnterGradesPage(user, courseId, model);
        return "professor/enter-grades";
    }

    // Αποθηκευση των βαθμων που συμπληρωθηκαν στη φορμα
    @PostMapping("/enter-grades")
    public String enterGrades(@RequestParam(required = false) Long courseId, @RequestParam(required = false) Long[] studentIds, @RequestParam(required = false) String[] grades, HttpSession session, Model model)
    {
        Users user = authService.currentUser(session);

        try
        {
            int saved = gradeService.saveGrades(user.getId(), courseId, studentIds, grades);
            model.addAttribute("success", "Καταχωρήθηκαν " + saved + " βαθμοί.");
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
        }

        fillEnterGradesPage(user, courseId, model);
        return "professor/enter-grades";
    }

    // Γεμιζει τα δεδομενα της σελιδας καταχωρησης (κοινο για GET και POST)
    private void fillEnterGradesPage(Users user, Long courseId, Model model)
    {
        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findCoursesToGrade(user.getId()));

        if (courseId == null)
        {
            return;
        }

        Courses course = courseService.findProfessorCourse(user.getId(), courseId);

        if (course == null)
        {
            model.addAttribute("error", "Το μάθημα δεν βρέθηκε ή δεν σας έχει ανατεθεί.");
            return;
        }

        model.addAttribute("selectedCourse", course);
        model.addAttribute("students", gradeService.findStudentsToGrade(courseId));
    }
}
