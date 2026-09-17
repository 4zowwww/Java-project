package com.unipi.studentapp.config;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// Τρεχει πριν απο καθε προστατευμενη σελιδα και ελεγχει οτι ο χρηστης
// εχει συνδεθει και ανηκει στη σωστη κατηγορια (π.χ. ενας φοιτητης
// δεν μπορει να ανοιξει σελιδες της γραμματειας).
public class RoleInterceptor implements HandlerInterceptor
{

    private final AuthService authService;
    private final String role;

    public RoleInterceptor(AuthService authService, String role)
    {
        this.authService = authService;
        this.role = role;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        Users user = authService.currentUser(request.getSession(false));

        // Δεν εχει συνδεθει: επιστροφη στη σελιδα συνδεσης
        if (user == null)
        {
            response.sendRedirect(request.getContextPath() + "/index.html?denied=1");
            return false;
        }

        // Αλλη κατηγορια χρηστη: επιστροφη στην αρχικη σελιδα με μηνυμα
        if (!user.getRole().equals(role))
        {
            response.sendRedirect(request.getContextPath() + "/index.html?forbidden=1");
            return false;
        }

        return true;
    }
}
