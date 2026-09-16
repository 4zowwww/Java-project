package com.unipi.studentapp.model;

import java.util.ArrayList;
import java.util.List;

public class Professors extends Users {

    private String professorId;

    private final List<Courses> courses = new ArrayList<>();

    public Professors() {
        setRole(ROLE_PROFESSOR);
    }

    public Professors(String username, String name, String surname, String department,
                      String professorId) {
        super(username, name, surname, department);
        setRole(ROLE_PROFESSOR);
        this.professorId = professorId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public void addCourse(Courses course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(Courses course) {
        courses.remove(course);
    }

    public List<Courses> getCourses() {
        return List.copyOf(courses);
    }

    @Override
    public String toString() {
        return "Καθηγητής: " + getFullName()
                + " | κωδικός: " + professorId
                + " | username: " + getUsername()
                + " | τμήμα: " + getDepartment();
    }
}
