package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.Professors;
import com.unipi.studentapp.model.Students;
import com.unipi.studentapp.repository.CourseRepository;
import com.unipi.studentapp.repository.DepartmentRepository;
import com.unipi.studentapp.repository.ProfessorRepository;
import com.unipi.studentapp.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService
{

    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;


    public CourseService(CourseRepository courseRepository, ProfessorRepository professorRepository, DepartmentRepository departmentRepository, StudentRepository studentRepository)
    {
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
    }

    public List<Courses> findAll()
    {
        return courseRepository.findAll();
    }

    // Το μάθημα με αυτό το id, ή null αν δεν υπάρχει
    public Courses findById(Long courseId)
    {
        if (courseId == null)
        {
            return null;
        }
        return courseRepository.findById(courseId).orElse(null);
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

    // ---------- Δημιουργία μαθημάτων (1η άσκηση, βήμα 10.1.1) ----------

    // Δημιουργεί νέο μάθημα. Αν κάτι δεν είναι σωστό πετάει IllegalArgumentException με μήνυμα για τον χρήστη.
    @Transactional
    public String createCourse(String courseCode, String courseName, String ectsText, String semesterText, Long departmentId, Long professorUserId)
    {
        if (isBlank(courseCode) || isBlank(courseName) || isBlank(ectsText) || isBlank(semesterText) || departmentId == null)
        {
            throw new IllegalArgumentException("Συμπληρώστε όλα τα υποχρεωτικά πεδία.");
        }

        String code = courseCode.trim().toUpperCase();
        String title = courseName.trim();

        if (code.contains(" "))
        {
            throw new IllegalArgumentException("Ο κωδικός μαθήματος δεν μπορεί να περιέχει κενά.");
        }
        if (code.length() > 20 || title.length() > 150)
        {
            throw new IllegalArgumentException("Ο κωδικός μπορεί να έχει έως 20 χαρακτήρες και ο τίτλος έως 150.");
        }

        int ects = parseWholeNumber(ectsText, "Οι μονάδες ECTS πρέπει να είναι ακέραιος αριθμός.");
        if (ects < 1 || ects > 30)
        {
            throw new IllegalArgumentException("Οι μονάδες ECTS πρέπει να είναι από 1 έως 30.");
        }

        int semester = parseWholeNumber(semesterText, "Το εξάμηνο πρέπει να είναι ακέραιος αριθμός.");
        if (semester < 1 || semester > Courses.MAX_SEMESTER)
        {
            throw new IllegalArgumentException("Το εξάμηνο πρέπει να είναι από 1 έως " + Courses.MAX_SEMESTER + ".");
        }

        if (courseRepository.existsByCode(code))
        {
            throw new IllegalArgumentException("Υπάρχει ήδη μάθημα με κωδικό " + code + ".");
        }
        if (!departmentRepository.existsById(departmentId))
        {
            throw new IllegalArgumentException("Το τμήμα δεν βρέθηκε.");
        }

        // Ο υπεύθυνος καθηγητής είναι προαιρετικός
        if (professorUserId != null && professorRepository.findById(professorUserId).isEmpty())
        {
            throw new IllegalArgumentException("Ο καθηγητής δεν βρέθηκε.");
        }

        courseRepository.insert(code, title, ects, semester, departmentId, professorUserId);

        return "Το μάθημα " + title + " (" + code + ") δημιουργήθηκε.";
    }

    // ---------- Λίστες φοιτητών προς βαθμολόγηση (1η άσκηση, βήμα 10.1.3) ----------

    // Οι φοιτητές που είναι εγγεγραμμένοι στο μάθημα
    public List<Students> findEnrolledStudents(Long courseId)
    {
        return studentRepository.findEnrolled(courseId);
    }

    // Οι φοιτητές που μπορούν ακόμα να εγγραφούν στο μάθημα
    public List<Students> findStudentsNotEnrolled(Long courseId)
    {
        return studentRepository.findNotEnrolled(courseId);
    }

    // Εγγράφει έναν φοιτητή σε μάθημα, δηλαδή τον προσθέτει στη λίστα προς βαθμολόγηση.
    // Αν κάτι δεν είναι σωστό πετάει IllegalArgumentException με μήνυμα για τον χρήστη.
    @Transactional
    public String enrollStudent(Long courseId, Long studentUserId)
    {
        if (courseId == null || studentUserId == null)
        {
            throw new IllegalArgumentException("Επιλέξτε μάθημα και φοιτητή.");
        }

        Courses course = courseRepository.findById(courseId).orElse(null);

        if (course == null)
        {
            throw new IllegalArgumentException("Το μάθημα δεν βρέθηκε.");
        }
        if (!studentRepository.existsById(studentUserId))
        {
            throw new IllegalArgumentException("Ο φοιτητής δεν βρέθηκε.");
        }
        if (courseRepository.isEnrolled(studentUserId, courseId))
        {
            throw new IllegalArgumentException("Ο φοιτητής είναι ήδη εγγεγραμμένος στο μάθημα.");
        }

        courseRepository.enroll(studentUserId, courseId);

        return "Ο φοιτητής προστέθηκε στη λίστα του μαθήματος " + course.getCourseCode() + ".";
    }

    // Μετατρέπει κείμενο σε ακέραιο αριθμό, αλλιώς πετάει το μήνυμα που δόθηκε
    private int parseWholeNumber(String text, String errorMessage)
    {
        try
        {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private boolean isBlank(String text)
    {
        return text == null || text.trim().isEmpty();
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
