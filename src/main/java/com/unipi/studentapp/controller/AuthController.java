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

// Συνδεση και αποσυνδεση για ολες τις κατηγοριες χρηστων
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

        // Νεο session id μετα τη συνδεση, ωστε ενα id που υπηρχε πριν το login να μην μπορει
        // να χρησιμοποιηθει απο καποιον αλλον (προστασια απο "session fixation")
        request.changeSessionId();

        // Μετα τη συνδεση η αρχικη σελιδα δειχνει το μενου της κατηγοριας του χρηστη
        return "redirect:/index.html";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response)
    {
        // Ο browser διαγραφει ο,τι εχει αποθηκευμενο στην cache για την εφαρμογη
        response.setHeader("Clear-Site-Data", "\"cache\"");

        // Διαγραφη του session (invalidate)
        authService.logout(session);

        return "redirect:/index.html?logout=1";
    }
}
