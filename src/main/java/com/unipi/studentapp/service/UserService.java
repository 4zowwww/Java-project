package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.repository.DepartmentRepository;
import com.unipi.studentapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Δημιουργία νέων χρηστών από τη γραμματεία (1η άσκηση, βήμα 10.1.1).
// Αν κάτι δεν είναι σωστό, οι μέθοδοι πετάνε IllegalArgumentException με μήνυμα για τον χρήστη.
@Service
public class UserService
{

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordService passwordService;


    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository, PasswordService passwordService)
    {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordService = passwordService;
    }

    @Transactional
    public String createStudent(String username, String password, String name, String surname, Long departmentId, String registrationNumberText)
    {
        checkUserFields(username, password, name, surname, departmentId);

        if (isBlank(registrationNumberText))
        {
            throw new IllegalArgumentException("Συμπληρώστε τον αριθμό μητρώου.");
        }

        int registrationNumber;
        try
        {
            registrationNumber = Integer.parseInt(registrationNumberText.trim());
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Ο αριθμός μητρώου πρέπει να είναι ακέραιος αριθμός.");
        }

        if (registrationNumber <= 0)
        {
            throw new IllegalArgumentException("Ο αριθμός μητρώου πρέπει να είναι θετικός αριθμός.");
        }
        if (userRepository.existsRegistrationNumber(registrationNumber))
        {
            throw new IllegalArgumentException("Υπάρχει ήδη φοιτητής με αριθμό μητρώου " + registrationNumber + ".");
        }

        Long userId = insertUser(username, password, name, surname, departmentId, Users.ROLE_STUDENT);
        userRepository.insertStudent(userId, registrationNumber);

        return "Ο φοιτητής " + name.trim() + " " + surname.trim() + " (ΑΜ " + registrationNumber + ") δημιουργήθηκε.";
    }

    @Transactional
    public String createProfessor(String username, String password, String name, String surname, Long departmentId, String professorId)
    {
        checkUserFields(username, password, name, surname, departmentId);

        if (isBlank(professorId))
        {
            throw new IllegalArgumentException("Συμπληρώστε τον κωδικό καθηγητή.");
        }
        if (professorId.trim().length() > 20)
        {
            throw new IllegalArgumentException("Ο κωδικός καθηγητή μπορεί να έχει έως 20 χαρακτήρες.");
        }
        if (userRepository.existsProfessorId(professorId.trim()))
        {
            throw new IllegalArgumentException("Υπάρχει ήδη καθηγητής με κωδικό " + professorId.trim() + ".");
        }

        Long userId = insertUser(username, password, name, surname, departmentId, Users.ROLE_PROFESSOR);
        userRepository.insertProfessor(userId, professorId.trim());

        return "Ο καθηγητής " + name.trim() + " " + surname.trim() + " (" + professorId.trim() + ") δημιουργήθηκε.";
    }

    // Κοινοί έλεγχοι για κάθε νέο χρήστη
    private void checkUserFields(String username, String password, String name, String surname, Long departmentId)
    {
        if (isBlank(username) || isBlank(password) || isBlank(name) || isBlank(surname) || departmentId == null)
        {
            throw new IllegalArgumentException("Συμπληρώστε όλα τα πεδία.");
        }
        if (username.trim().contains(" "))
        {
            throw new IllegalArgumentException("Το όνομα χρήστη δεν μπορεί να περιέχει κενά.");
        }
        if (username.trim().length() > 50 || name.trim().length() > 50 || surname.trim().length() > 50)
        {
            throw new IllegalArgumentException("Το όνομα χρήστη, το όνομα και το επώνυμο μπορούν να έχουν έως 50 χαρακτήρες.");
        }
        if (password.length() < 6)
        {
            throw new IllegalArgumentException("Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.");
        }
        if (userRepository.existsByUsername(username.trim()))
        {
            throw new IllegalArgumentException("Το όνομα χρήστη " + username.trim() + " χρησιμοποιείται ήδη.");
        }
        if (!departmentRepository.existsById(departmentId))
        {
            throw new IllegalArgumentException("Το τμήμα δεν βρέθηκε.");
        }
    }

    // Αποθηκεύει τον χρήστη με κρυπτογραφημένο κωδικό (hash + salt) και επιστρέφει το id του
    private Long insertUser(String username, String password, String name, String surname, Long departmentId, String role)
    {
        String salt = passwordService.generateSalt();
        String hash = passwordService.hash(password, salt);

        return userRepository.insertUser(username.trim(), hash, salt, name.trim(), surname.trim(), departmentId, role);
    }

    private boolean isBlank(String text)
    {
        return text == null || text.trim().isEmpty();
    }
}
