package com.unipi.studentapp.model;

import java.util.List;

// Οι βαθμοι ενος φοιτητη σε ενα εξαμηνο
public class SemesterGrades
{

    private final int semester;
    private final List<Grades> grades;


    public SemesterGrades(int semester, List<Grades> grades)
    {
        this.semester = semester;
        this.grades = grades;
    }

    public int getSemester()
    {
        return semester;
    }

    public List<Grades> getGrades()
    {
        return grades;
    }

    // Μεσος ορος, ECTS κλπ. μονο για αυτο το εξαμηνο
    public GradeSummary getSummary()
    {
        return new GradeSummary(grades);
    }
}
