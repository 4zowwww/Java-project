package com.unipi.studentapp.controller;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Σύνδεση και αποσύνδεση για όλες τις κατηγορίες χρηστών
@Controller
public class AuthController
{

    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, HttpSession session)
    {
        Users user = authService.login(session, username, password);

        if (user == null)
        {
            return "redirect:/index.html?error=1";
        }

        // Νέο session id μετά τη σύνδεση, ώστε ένα id που υπήρχε πριν το login να μην μπορεί
        // να χρησιμοποιηθεί από κάποιον άλλον (προστασία από "session fixation")
        request.changeSessionId();

        // Μετά τη σύνδεση η αρχική σελίδα δείχνει το μενού της κατηγορίας του χρήστη
        return "redirect:/index.html";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response)
    {
        // Ο browser διαγράφει ό,τι έχει αποθηκευμένο στην cache για την εφαρμογή
        response.setHeader("Clear-Site-Data", "\"cache\"");

        // Διαγραφή του session (invalidate)
        authService.logout(session);

        return "redirect:/index.html?logout=1";
    }
}
