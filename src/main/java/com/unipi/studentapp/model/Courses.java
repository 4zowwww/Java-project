package com.unipi.studentapp.model;

import java.util.ArrayList;
import java.util.List;

public class Courses
{

    // Το τελευταίο εξάμηνο σπουδών
    public static final int MAX_SEMESTER = 8;

    private Long id;
    private String courseCode;
    private String courseName;
    private int ects;
    private int semester;
    private String department;
    private Professors professor;

    private final List<Students> students = new ArrayList<>();
    private final List<Grades> grades = new ArrayList<>();


    public Courses()
    {
    }

    public Courses(String courseCode, String courseName, int ects)
    {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.ects = ects;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public int getEcts()
    {
        return ects;
    }

    public void setEcts(int ects)
    {
        this.ects = ects;
    }

    public int getSemester()
    {
        return semester;
    }

    public void setSemester(int semester)
    {
        this.semester = semester;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public Professors getProfessor()
    {
        return professor;
    }

    public void setProfessor(Professors professor)
    {
        this.professor = professor;
    }

    public boolean hasProfessor()
    {
        return professor != null;
    }

    public void addStudent(Students student)
    {
        if (student != null && !students.contains(student))
        {
            students.add(student);
        }
    }

    public void removeStudent(Students student)
    {
        students.remove(student);
    }

    public List<Students> getStudents()
    {
        return List.copyOf(students);
    }

    public void addGrade(Grades grade)
    {
        if (grade != null && !grades.contains(grade))
        {
            grades.add(grade);
        }
    }

    public List<Grades> getGrades()
    {
        return List.copyOf(grades);
    }

    @Override
    public String toString()
    {
        return "Μάθημα: " + courseName + " (" + courseCode + ")"
                + " | ECTS: " + ects
                + " | εξάμηνο: " + semester
                + " | διδάσκων: " + (professor == null ? "-" : professor.getFullName());
    }
}
