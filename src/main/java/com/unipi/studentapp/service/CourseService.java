package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.Professors;
import com.unipi.studentapp.repository.CourseRepository;
import com.unipi.studentapp.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService 
{

    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;

    
    public CourseService(CourseRepository courseRepository, ProfessorRepository professorRepository) 
    {
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
    }

    public List<Courses> findAll()
    {
        return courseRepository.findAll();
    }

    // Τα μαθήματα του καθηγητή που έχουν ήδη βαθμολογίες
    public List<Courses> findGradedCourses(Long professorUserId)
    {
        return courseRepository.findGradedByProfessor(professorUserId);
    }

    // Τα μαθήματα του καθηγητή με φοιτητές που περιμένουν βαθμό
    public List<Courses> findCoursesToGrade(Long professorUserId)
    {
        return courseRepository.findToGradeByProfessor(professorUserId);
    }

    // Επιστρέφει το μάθημα μόνο αν έχει ανατεθεί στον συγκεκριμένο καθηγητή, αλλιώς null.
    // Έτσι ένας καθηγητής δεν μπορεί να δει ή να βαθμολογήσει μαθήματα άλλου καθηγητή.
    public Courses findProfessorCourse(Long professorUserId, Long courseId)
    {
        if (courseId == null)
        {
            return null;
        }

        Courses course = courseRepository.findById(courseId).orElse(null);

        if (course == null || !course.hasProfessor())
        {
            return null;
        }
        if (!course.getProfessor().getId().equals(professorUserId))
        {
            return null;
        }
        return course;
    }

    @Transactional
    public AssignmentResult assignProfessorToCourse(Long courseId, Long professorUserId) 
    {
        if (courseId == null || professorUserId == null) 
        {
            return AssignmentResult.failed("Επιλέξτε μάθημα και καθηγητή.");
        }

        Optional<Courses> foundCourse = courseRepository.findById(courseId);

        if (foundCourse.isEmpty()) 
        {
            return AssignmentResult.failed("Δεν βρέθηκε μάθημα με κωδικό " + courseId + ".");
        }

        Optional<Professors> foundProfessor = professorRepository.findById(professorUserId);

        if (foundProfessor.isEmpty()) 
        {
            return AssignmentResult.failed("Δεν βρέθηκε καθηγητής με κωδικό " + professorUserId + ".");
        }

        Courses course = foundCourse.get();
        Professors professor = foundProfessor.get();

        if (course.hasProfessor() && course.getProfessor().getId().equals(professorUserId)) 
        {
            return AssignmentResult.failed("Το μάθημα " + course.getCourseCode()
                    + " είναι ήδη ανατεθειμένο στον/στην " + professor.getFullName() + ".");
        }

        courseRepository.assignProfessor(courseId, professorUserId);

        course.setProfessor(professor);
        professor.addCourse(course);

        return AssignmentResult.ok("Το μάθημα " + course.getCourseName()
                + " (" + course.getCourseCode() + ") ανατέθηκε στον/στην "
                + professor.getFullName() + ".");
    }

    public record AssignmentResult(boolean success, String message) 
    {

        static AssignmentResult ok(String message) 
        {
            return new AssignmentResult(true, message);
        }

        static AssignmentResult failed(String message) 
        {
            return new AssignmentResult(false, message);
        }
    }
}
