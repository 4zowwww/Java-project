package com.unipi.studentapp.controller;

import com.unipi.studentapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Αρχικη σελιδα (index.html) για ολους τους χρηστες.
// Αν δεν εχει συνδεθει κανεις εμφανιζεται η φορμα συνδεσης,
// αλλιως το μενου λειτουργιων της κατηγοριας του χρηστη.
@Controller
public class HomeController
{

    private final AuthService authService;

    public HomeController(AuthService authService)
    {
        this.authService = authService;
    }

    // Η σελιδα ανοιγει και απο το "/" και απο το "/index.html"
    @GetMapping({"/", "/index.html"})
    public String index(HttpSession session, Model model)
    {
        model.addAttribute("user", authService.currentUser(session));
        return "index";
    }
}
