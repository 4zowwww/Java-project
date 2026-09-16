package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{

    // Το όνομα με το οποίο αποθηκεύεται ο συνδεδεμένος χρήστης στο session
    public static final String SESSION_USER = "user";
    private final UserRepository userRepository;
    private final PasswordService passwordService;


    public AuthService(UserRepository userRepository, PasswordService passwordService)
    {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    // Επιστρέφει τον χρήστη αν τα στοιχεία είναι σωστά, αλλιώς null
    public Users login(HttpSession session, String username, String password)
    {
        Users user = userRepository.findByUsername(username.trim()).orElse(null);

        // Δεν υπάρχει χρήστης με αυτό το username
        if (user == null)
        {
            return null;
        }

        // Λάθος κωδικός: το hash του κωδικού που δόθηκε δεν ταιριάζει με το αποθηκευμένο
        if (!passwordService.matches(password, user.getSalt(), user.getPasswordHash()))
        {
            return null;
        }

        // Το hash και το salt δεν χρειάζονται μετά τη σύνδεση, οπότε δεν τα κρατάμε στο session
        user.setPasswordHash(null);
        user.setSalt(null);

        session.setAttribute(SESSION_USER, user);
        return user;
    }

    public void logout(HttpSession session)
    {
        session.invalidate();
    }

    // Ο χρήστης που είναι συνδεδεμένος αυτή τη στιγμή (null αν δεν έχει συνδεθεί κανείς)
    public Users currentUser(HttpSession session)
    {
        if (session == null)
        {
            return null;
        }
        return (Users) session.getAttribute(SESSION_USER);
    }
}
