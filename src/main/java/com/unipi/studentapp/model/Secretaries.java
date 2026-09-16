package com.unipi.studentapp.model;

public class Secretaries extends Users {

    private String employeeNumber;

    public Secretaries() {
        setRole(ROLE_SECRETARY);
    }

    public Secretaries(String username, String name, String surname, String department,
                       String employeeNumber) {
        super(username, name, surname, department);
        setRole(ROLE_SECRETARY);
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @Override
    public String toString() {
        return "Γραμματεία: " + getFullName()
                + " | αρ. υπαλλήλου: " + employeeNumber
                + " | username: " + getUsername()
                + " | τμήμα: " + getDepartment();
    }
}
