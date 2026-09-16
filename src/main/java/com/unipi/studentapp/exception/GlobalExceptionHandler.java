package com.unipi.studentapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler 
{

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoResourceFoundException exception, Model model) 
    {
        model.addAttribute("title", "Η σελίδα δεν βρέθηκε");
        model.addAttribute("message", "Η διεύθυνση που ζητήσατε δεν υπάρχει.");
        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleDatabaseError(DataAccessException exception, Model model) 
    {
        log.error("Σφάλμα πρόσβασης στη βάση δεδομένων", exception);
        model.addAttribute("title", "Πρόβλημα με τη βάση δεδομένων");
        model.addAttribute("message", "Δεν ήταν δυνατή η επικοινωνία με τον database server.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpectedError(Exception exception, Model model) 
    {
        log.error("Μη αναμενόμενο σφάλμα εφαρμογής", exception);
        model.addAttribute("title", "Σφάλμα εφαρμογής");
        model.addAttribute("message", "Παρουσιάστηκε μη αναμενόμενο σφάλμα.");
        return "error";
    }
}
