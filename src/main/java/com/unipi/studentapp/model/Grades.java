package com.unipi.studentapp.model;

public class Grades {

    private Long id;
    private Students student;
    private Courses course;
    private double value;

    
    public Grades() 
    {
    }

    public Grades(Students student, Courses course, double value) 
    {
        if (student == null || course == null) {
            throw new IllegalArgumentException("Ο φοιτητής και το μάθημα δεν μπορούν να είναι κενά.");
        }
        this.student = student;
        this.course = course;
        setValue(value);
    }

    public Long getId() 
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Students getStudent() 
    {
        return student;
    }

    public void setStudent(Students student) 
    {
        this.student = student;
    }

    public Courses getCourse() 
    {
        return course;
    }

    public void setCourse(Courses course) 
    {
        this.course = course;
    }

    public double getValue() 
    {
        return value;
    }

    public void setValue(double value) 
    {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("Ο βαθμός πρέπει να είναι από 0 έως 10.");
        }
        this.value = value;
    }

    public boolean isPassing() 
    {
        return value >= 5;
    }

    @Override
    public String toString() 
    {
        return (course == null ? "-" : course.getCourseName()) + ": " + value
                + (isPassing() ? " (επιτυχία)" : " (αποτυχία)");
    }
}
