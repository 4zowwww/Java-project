package com.unipi.studentapp.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Κρυπτογραφηση κωδικων (hashed + salted) με τον αλγοριθμο PBKDF2 (HmacSHA256).
// Στη βαση δεν αποθηκευεται ποτε ο πραγματικος κωδικος, μονο το hash και το salt.
@Service
public class PasswordService
{

    // Ποσες φορες επαναλαμβανεται ο υπολογισμος (οσο περισσοτερες, τοσο πιο δυσκολο το "σπασιμο")
    private static final int ITERATIONS = 65536;

    // Μεγεθος του hash σε bits
    private static final int KEY_LENGTH = 256;

    // Δημιουργει ενα τυχαιο salt (16 bytes) για καθε χρηστη
    public String generateSalt()
    {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Υπολογιζει το hash του κωδικου μαζι με το salt του χρηστη
    public String hash(String password, String salt)
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (GeneralSecurityException e)
        {
            throw new IllegalStateException("Δεν ήταν δυνατός ο υπολογισμός του hash.", e);
        }
    }

    // Ελεγχει αν ο κωδικος που πληκτρολογησε ο χρηστης ταιριαζει με το αποθηκευμενο hash
    public boolean matches(String password, String salt, String storedHash)
    {
        String newHash = hash(password, salt);

        // Συγκριση σε σταθερο χρονο, ωστε να μην "προδιδεται" απο τον χρονο αποκρισης ποσο κοντα ηταν ο κωδικος
        return MessageDigest.isEqual(newHash.getBytes(), storedHash.getBytes());
    }
}
