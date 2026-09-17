package com.unipi.studentapp.model;

import java.util.List;

// Συγκεντρωτικα στοιχεια για μια λιστα βαθμων
// (π.χ. ολων των μαθηματων ενος φοιτητη η μονο ενος εξαμηνου)
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

    // Σε ποσα μαθηματα εξεταστηκε ο φοιτητης
    public int getExaminedCount()
    {
        return examinedCount;
    }

    // Ποσα μαθηματα περασε (βαθμος >= 5)
    public int getPassedCount()
    {
        return passedCount;
    }

    public int getFailedCount()
    {
        return examinedCount - passedCount;
    }

    // Οι μοναδες ECTS των μαθηματων που περασε
    public int getEctsEarned()
    {
        return ectsEarned;
    }

    // Μεσος ορος ολων των μαθηματων που εξεταστηκε
    public double getAverageAll()
    {
        return averageAll;
    }

    // Μεσος ορος μονο των μαθηματων που περασε
    public double getAveragePassed()
    {
        return averagePassed;
    }
}
