package com.unipi.studentapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// Τρεχει πριν απο καθε σελιδα και λεει στον browser να ΜΗΝ κραταει αντιγραφο (cache).
// Ετσι μετα το logout, το κουμπι "Πισω" δεν μπορει να δειξει σελιδες του προηγουμενου χρηστη:
// ο browser ξαναζηταει τη σελιδα απο τον server, ο οποιος τον στελνει στη συνδεση.
public class NoCacheInterceptor implements HandlerInterceptor
{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache");                                   // HTTP 1.0
        response.setDateHeader("Expires", 0);                                       // παλιοι proxies
        return true;
    }
}
