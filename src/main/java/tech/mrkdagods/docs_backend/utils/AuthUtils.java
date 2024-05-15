package tech.mrkdagods.docs_backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AuthUtils {
    private final static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    // min 3, max 16
    public static boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9_-]{3,16}$");
    }

    // min 3, max 32
    public static boolean validatePassword(String password) {
        return password.matches("^[A-Za-z0-9\\s$&+,:;=?@#|'<>.^*()%!-]{3,32}$");
    }

    public static boolean validateEmail(String email) {
        return email.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    }

    public static String calculateHash(String password) {
        try {
            var bytes = password.getBytes(StandardCharsets.US_ASCII);
            var md = MessageDigest.getInstance("MD5");
            var output = md.digest(bytes);

            var buf = new StringBuffer();
            for (byte b : output) {
                buf.append(String.format("%02x", b));
            }

            return buf.toString().toUpperCase();
        }
        catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String generateSessionString() {
        StringBuilder id = new StringBuilder();

        Random rnd = new Random();
        while (id.length() < 64) {
            int index = (int) (rnd.nextFloat() * chars.length());
            id.append(chars.charAt(index));
        }

        return id.toString();
    }
}
