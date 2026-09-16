package com.unipi.studentapp.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Κρυπτογράφηση κωδικών (hashed + salted) με τον αλγόριθμο PBKDF2 (HmacSHA256).
// Στη βάση δεν αποθηκεύεται ποτέ ο πραγματικός κωδικός, μόνο το hash και το salt.
@Service
public class PasswordService
{

    // Πόσες φορές επαναλαμβάνεται ο υπολογισμός (όσο περισσότερες, τόσο πιο δύσκολο το "σπάσιμο")
    private static final int ITERATIONS = 65536;

    // Μέγεθος του hash σε bits
    private static final int KEY_LENGTH = 256;

    // Δημιουργεί ένα τυχαίο salt (16 bytes) για κάθε χρήστη
    public String generateSalt()
    {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Υπολογίζει το hash του κωδικού μαζί με το salt του χρήστη
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

    // Ελέγχει αν ο κωδικός που πληκτρολόγησε ο χρήστης ταιριάζει με το αποθηκευμένο hash
    public boolean matches(String password, String salt, String storedHash)
    {
        String newHash = hash(password, salt);

        // Σύγκριση σε σταθερό χρόνο, ώστε να μην "προδίδεται" από τον χρόνο απόκρισης πόσο κοντά ήταν ο κωδικός
        return MessageDigest.isEqual(newHash.getBytes(), storedHash.getBytes());
    }
}
