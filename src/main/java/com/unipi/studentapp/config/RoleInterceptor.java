package com.unipi.studentapp.config;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// Τρέχει πριν από κάθε προστατευμένη σελίδα και ελέγχει ότι ο χρήστης
// έχει συνδεθεί και ανήκει στη σωστή κατηγορία (π.χ. ένας φοιτητής
// δεν μπορεί να ανοίξει σελίδες της γραμματείας).
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

        // Δεν έχει συνδεθεί: επιστροφή στη σελίδα σύνδεσης
        if (user == null)
        {
            response.sendRedirect(request.getContextPath() + "/index.html?denied=1");
            return false;
        }

        // Άλλη κατηγορία χρήστη: επιστροφή στην αρχική σελίδα με μήνυμα
        if (!user.getRole().equals(role))
        {
            response.sendRedirect(request.getContextPath() + "/index.html?forbidden=1");
            return false;
        }

        return true;
    }
}
