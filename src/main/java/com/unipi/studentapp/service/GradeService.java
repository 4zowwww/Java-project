package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Courses;
import com.unipi.studentapp.model.GradeSummary;
import com.unipi.studentapp.model.Grades;
import com.unipi.studentapp.model.SemesterGrades;
import com.unipi.studentapp.model.Students;
import com.unipi.studentapp.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GradeService
{

    private final GradeRepository gradeRepository;
    private final CourseService courseService;


    public GradeService(GradeRepository gradeRepository, CourseService courseService)
    {
        this.gradeRepository = gradeRepository;
        this.courseService = courseService;
    }

    // ---------- Φοιτητές ----------

    // Όλοι οι βαθμοί ενός φοιτητή
    public List<Grades> findStudentGrades(Long studentUserId)
    {
        return gradeRepository.findByStudent(studentUserId);
    }

    // Χωρίζει τους βαθμούς σε ομάδες, μία για κάθε εξάμηνο που έχει βαθμούς
    public List<SemesterGrades> groupBySemester(List<Grades> grades)
    {
        List<SemesterGrades> result = new ArrayList<>();

        for (int semester = 1; semester <= Courses.MAX_SEMESTER; semester++)
        {
            List<Grades> semesterGrades = new ArrayList<>();

            for (Grades grade : grades)
            {
                if (grade.getCourse().getSemester() == semester)
                {
                    semesterGrades.add(grade);
                }
            }

            if (!semesterGrades.isEmpty())
            {
                result.add(new SemesterGrades(semester, semesterGrades));
            }
        }
        return result;
    }

    // Συνολικά στοιχεία (μέσος όρος, ECTS, επιτυχόντα μαθήματα)
    public GradeSummary summarize(List<Grades> grades)
    {
        return new GradeSummary(grades);
    }

    // ---------- Καθηγητές ----------

    // Οι βαθμοί όλων των φοιτητών σε ένα μάθημα
    public List<Grades> findCourseGrades(Long courseId)
    {
        return gradeRepository.findByCourse(courseId);
    }

    // Οι φοιτητές ενός μαθήματος που δεν έχουν βαθμό ακόμα
    public List<Students> findStudentsToGrade(Long courseId)
    {
        return gradeRepository.findUngradedStudents(courseId);
    }

    // Καταχωρεί τους βαθμούς ενός μαθήματος και επιστρέφει πόσοι βαθμοί αποθηκεύτηκαν.
    // Αν κάτι δεν είναι σωστό πετάει IllegalArgumentException με μήνυμα για τον χρήστη
    // και δεν αποθηκεύεται κανένας βαθμός (@Transactional).
    @Transactional
    public int saveGrades(Long professorUserId, Long courseId, Long[] studentIds, String[] grades)
    {
        if (courseService.findProfessorCourse(professorUserId, courseId) == null)
        {
            throw new IllegalArgumentException("Το μάθημα δεν βρέθηκε ή δεν σας έχει ανατεθεί.");
        }

        if (studentIds == null || studentIds.length == 0)
        {
            throw new IllegalArgumentException("Δεν υπάρχουν φοιτητές προς βαθμολόγηση.");
        }

        // Επιτρέπεται βαθμός μόνο για φοιτητές που είναι εγγεγραμμένοι και δεν έχουν ακόμα βαθμό
        List<Students> ungradedStudents = gradeRepository.findUngradedStudents(courseId);

        // 1ο βήμα: έλεγχος όλων των βαθμών πριν αποθηκευτεί οτιδήποτε
        List<Long> idsToSave = new ArrayList<>();
        List<Double> valuesToSave = new ArrayList<>();

        for (int i = 0; i < studentIds.length; i++)
        {
            String text = "";
            if (grades != null && i < grades.length && grades[i] != null)
            {
                text = grades[i].trim();
            }

            // Κενό πεδίο: ο φοιτητής μένει χωρίς βαθμό προς το παρόν
            if (text.isEmpty())
            {
                continue;
            }

            if (!isInList(studentIds[i], ungradedStudents) || idsToSave.contains(studentIds[i]))
            {
                throw new IllegalArgumentException("Ένας φοιτητής δεν είναι εγγεγραμμένος στο μάθημα ή έχει ήδη βαθμό.");
            }

            double value;
            try
            {
                value = Double.parseDouble(text.replace(',', '.'));
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Μη έγκυρος βαθμός: " + text);
            }

            // Γραμμένο έτσι ώστε να απορρίπτονται και οι "ειδικές" τιμές όπως NaN
            if (!(value >= 0 && value <= 10))
            {
                throw new IllegalArgumentException("Ο βαθμός πρέπει να είναι από 0 έως 10.");
            }

            idsToSave.add(studentIds[i]);
            valuesToSave.add(value);
        }

        if (idsToSave.isEmpty())
        {
            throw new IllegalArgumentException("Δεν συμπληρώσατε κανέναν βαθμό.");
        }

        // 2ο βήμα: αποθήκευση
        for (int i = 0; i < idsToSave.size(); i++)
        {
            gradeRepository.insert(idsToSave.get(i), courseId, valuesToSave.get(i));
        }

        return idsToSave.size();
    }

    // Ελέγχει αν ο φοιτητής υπάρχει στη λίστα
    private boolean isInList(Long studentId, List<Students> students)
    {
        for (Students student : students)
        {
            if (student.getId().equals(studentId))
            {
                return true;
            }
        }
        return false;
    }
}
