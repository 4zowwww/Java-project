package com.unipi.studentapp.model;

import java.util.ArrayList;
import java.util.List;

public class Students extends Users 
{

    private int registrationNumber;

    private final List<Courses> courses = new ArrayList<>();
    private final List<Grades> grades = new ArrayList<>();

    public Students() 
    {
        setRole(ROLE_STUDENT);
    }

    public Students(String username, String name, String surname, String department, int registrationNumber) 
    {
        super(username, name, surname, department);
        setRole(ROLE_STUDENT);
        this.registrationNumber = registrationNumber;
    }

    public int getRegistrationNumber() 
    {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) 
    {
        this.registrationNumber = registrationNumber;
    }

    public void addCourse(Courses course) 
    {
        if (course != null && !courses.contains(course)) 
        {
            courses.add(course);
        }
    }

    public void removeCourse(Courses course) 
    {
        courses.remove(course);
    }

    public List<Courses> getCourses() 
    {
        return List.copyOf(courses);
    }

    public void addGrade(Grades grade) 
    {
        if (grade != null && !grades.contains(grade)) 
        {
            grades.add(grade);
        }
    }

    public void removeGrade(Grades grade) 
    {
        grades.remove(grade);
    }

    public List<Grades> getGrades() 
    {
        return List.copyOf(grades);
    }

    @Override
    public String toString() 
    {
        return "Φοιτητής: " + getFullName()
                + " | ΑΜ: " + registrationNumber
                + " | username: " + getUsername()
                + " | τμήμα: " + getDepartment();
    }
}
