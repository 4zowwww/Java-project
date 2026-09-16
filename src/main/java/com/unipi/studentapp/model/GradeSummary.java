package com.unipi.studentapp.model;

import java.util.List;

// Συγκεντρωτικά στοιχεία για μια λίστα βαθμών
// (π.χ. όλων των μαθημάτων ενός φοιτητή ή μόνο ενός εξαμήνου)
public class GradeSummary
{

    private int examinedCount;
    private int passedCount;
    private int ectsEarned;
    private double averageAll;
    private double averagePassed;


    public GradeSummary(List<Grades> grades)
    {
        double sumAll = 0;
        double sumPassed = 0;

        for (Grades grade : grades)
        {
            examinedCount++;
            sumAll += grade.getValue();

            if (grade.isPassing())
            {
                passedCount++;
                sumPassed += grade.getValue();
                ectsEarned += grade.getCourse().getEcts();
            }
        }

        if (examinedCount > 0)
        {
            averageAll = sumAll / examinedCount;
        }
        if (passedCount > 0)
        {
            averagePassed = sumPassed / passedCount;
        }
    }

    // Σε πόσα μαθήματα εξετάστηκε ο φοιτητής
    public int getExaminedCount()
    {
        return examinedCount;
    }

    // Πόσα μαθήματα πέρασε (βαθμός >= 5)
    public int getPassedCount()
    {
        return passedCount;
    }

    public int getFailedCount()
    {
        return examinedCount - passedCount;
    }

    // Οι μονάδες ECTS των μαθημάτων που πέρασε
    public int getEctsEarned()
    {
        return ectsEarned;
    }

    // Μέσος όρος όλων των μαθημάτων που εξετάστηκε
    public double getAverageAll()
    {
        return averageAll;
    }

    // Μέσος όρος μόνο των μαθημάτων που πέρασε
    public double getAveragePassed()
    {
        return averagePassed;
    }
}
