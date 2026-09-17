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

    // ---------- Φοιτητες ----------

    // Ολοι οι βαθμοι ενος φοιτητη
    public List<Grades> findStudentGrades(Long studentUserId)
    {
        return gradeRepository.findByStudent(studentUserId);
    }

    // Χωριζει τους βαθμους σε ομαδες, μια για καθε εξαμηνο που εχει βαθμους
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

    // Συνολικα στοιχεια (μεσος ορος, ECTS, επιτυχοντα μαθηματα)
    public GradeSummary summarize(List<Grades> grades)
    {
        return new GradeSummary(grades);
    }

    // ---------- Καθηγητες ----------

    // Οι βαθμοι ολων των φοιτητων σε ενα μαθημα
    public List<Grades> findCourseGrades(Long courseId)
    {
        return gradeRepository.findByCourse(courseId);
    }

    // Οι φοιτητες ενος μαθηματος που δεν εχουν βαθμο ακομα
    public List<Students> findStudentsToGrade(Long courseId)
    {
        return gradeRepository.findUngradedStudents(courseId);
    }

    // Καταχωρει τους βαθμους ενος μαθηματος και επιστρεφει ποσοι βαθμοι αποθηκευτηκαν.
    // Αν κατι δεν ειναι σωστο πεταει IllegalArgumentException με μηνυμα για τον χρηστη
    // και δεν αποθηκευεται κανενας βαθμος (@Transactional).
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

        // Επιτρεπεται βαθμος μονο για φοιτητες που ειναι εγγεγραμμενοι και δεν εχουν ακομα βαθμο
        List<Students> ungradedStudents = gradeRepository.findUngradedStudents(courseId);

        // 1ο βημα: ελεγχος ολων των βαθμων πριν αποθηκευτει οτιδηποτε
        List<Long> idsToSave = new ArrayList<>();
        List<Double> valuesToSave = new ArrayList<>();

        for (int i = 0; i < studentIds.length; i++)
        {
            String text = "";
            if (grades != null && i < grades.length && grades[i] != null)
            {
                text = grades[i].trim();
            }

            // Κενο πεδιο: ο φοιτητης μενει χωρις βαθμο προς το παρον
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

            // Γραμμενο ετσι ωστε να απορριπτονται και οι "ειδικες" τιμες οπως NaN
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

        // 2ο βημα: αποθηκευση
        for (int i = 0; i < idsToSave.size(); i++)
        {
            gradeRepository.insert(idsToSave.get(i), courseId, valuesToSave.get(i));
        }

        return idsToSave.size();
    }

    // Ελεγχει αν ο φοιτητης υπαρχει στη λιστα
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
