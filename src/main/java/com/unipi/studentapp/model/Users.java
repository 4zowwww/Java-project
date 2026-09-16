package com.unipi.studentapp.model;

public class Users
{

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_PROFESSOR = "PROFESSOR";
    public static final String ROLE_SECRETARY = "SECRETARY";

    private Long id;
    private String username;
    private String passwordHash;
    private String salt;
    private String name;
    private String surname;
    private String department;
    private String role;

    public Users()
    {
    }

    public Users(String username, String name, String surname, String department)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.department = department;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getFullName()
    {
        return name + " " + surname;
    }

    @Override
    public String toString()
    {
        return "Χρήστης: " + getFullName()
                + " | username: " + username
                + " | τμήμα: " + department;
    }
}
