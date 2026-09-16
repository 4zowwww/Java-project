package com.unipi.studentapp.model;

import java.util.List;

// Οι βαθμοί ενός φοιτητή σε ένα εξάμηνο
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

    // Μέσος όρος, ECTS κλπ. μόνο για αυτό το εξάμηνο
    public GradeSummary getSummary()
    {
        return new GradeSummary(grades);
    }
}
