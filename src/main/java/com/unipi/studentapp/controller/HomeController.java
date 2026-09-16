package com.unipi.studentapp.controller;

import com.unipi.studentapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Αρχική σελίδα (index.html) για όλους τους χρήστες.
// Αν δεν έχει συνδεθεί κανείς εμφανίζεται η φόρμα σύνδεσης,
// αλλιώς το μενού λειτουργιών της κατηγορίας του χρήστη.
@Controller
public class HomeController
{

    private final AuthService authService;

    public HomeController(AuthService authService)
    {
        this.authService = authService;
    }

    // Η σελίδα ανοίγει και από το "/" και από το "/index.html"
    @GetMapping({"/", "/index.html"})
    public String index(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        return "index";
    }
}
