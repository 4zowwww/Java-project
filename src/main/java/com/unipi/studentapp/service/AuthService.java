package com.unipi.studentapp.service;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{

    // Το ονομα με το οποιο αποθηκευεται ο συνδεδεμενος χρηστης στο session
    public static final String SESSION_USER = "user";
    private final UserRepository userRepository;
    private final PasswordService passwordService;


    public AuthService(UserRepository userRepository, PasswordService passwordService)
    {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    // Επιστρεφει τον χρηστη αν τα στοιχεια ειναι σωστα, αλλιως null
    public Users login(HttpSession session, String username, String password)
    {
        Users user = userRepository.findByUsername(username.trim()).orElse(null);

        // Δεν υπαρχει χρηστης με αυτο το username
        if (user == null)
        {
            return null;
        }

        // Λαθος κωδικος: το hash του κωδικου που δοθηκε δεν ταιριαζει με το αποθηκευμενο
        if (!passwordService.matches(password, user.getSalt(), user.getPasswordHash()))
        {
            return null;
        }

        // Το hash και το salt δεν χρειαζονται μετα τη συνδεση, οποτε δεν τα κραταμε στο session
        user.setPasswordHash(null);
        user.setSalt(null);

        session.setAttribute(SESSION_USER, user);
        return user;
    }

    public void logout(HttpSession session)
    {
        session.invalidate();
    }

    // Ο χρηστης που ειναι συνδεδεμενος αυτη τη στιγμη (null αν δεν εχει συνδεθει κανεις)
    public Users currentUser(HttpSession session)
    {
        if (session == null)
        {
            return null;
        }
        return (Users) session.getAttribute(SESSION_USER);
    }
}
