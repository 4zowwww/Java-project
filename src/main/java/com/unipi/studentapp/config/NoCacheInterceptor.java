package com.unipi.studentapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// Τρέχει πριν από κάθε σελίδα και λέει στον browser να ΜΗΝ κρατάει αντίγραφο (cache).
// Έτσι μετά το logout, το κουμπί "Πίσω" δεν μπορεί να δείξει σελίδες του προηγούμενου χρήστη:
// ο browser ξαναζητάει τη σελίδα από τον server, ο οποίος τον στέλνει στη σύνδεση.
public class NoCacheInterceptor implements HandlerInterceptor
{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache");                                   // HTTP 1.0
        response.setDateHeader("Expires", 0);                                       // παλιοί proxies
        return true;
    }
}
