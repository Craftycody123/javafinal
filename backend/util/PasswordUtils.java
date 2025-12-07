package backend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 * Uses SHA-256 with salt for secure password storage
 */
public class PasswordUtils {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Generate a random salt
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash a password with salt
     * @param password plain text password
     * @param salt Base64 encoded salt
     * @return hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            md.update(saltBytes);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Hash a password with a new salt
     * @param password plain text password
     * @return array containing [hashedPassword, salt]
     */
    public static String[] hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return new String[]{hashedPassword, salt};
    }
    
    /**
     * Verify a password against its hash
     * @param password plain text password
     * @param hashedPassword stored hashed password
     * @param salt stored salt
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(hashedPassword);
    }
    
    /**
     * Verify a password against a combined hash (hash:salt format)
     * @param password plain text password
     * @param combinedHash stored hash in format "hash:salt"
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String combinedHash) {
        if (combinedHash == null || !combinedHash.contains(":")) {
            return false;
        }
        
        String[] parts = combinedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        
        return verifyPassword(password, parts[0], parts[1]);
    }
    
    /**
     * Create a combined hash string (hash:salt format)
     * @param password plain text password
     * @return combined hash string
     */
    public static String createCombinedHash(String password) {
        String[] result = hashPassword(password);
        return result[0] + ":" + result[1];
    }
    
    /**
     * Validate password strength
     * @param password password to validate
     * @return validation result with error message
     */
    public static PasswordValidationResult validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return new PasswordValidationResult(false, "Password must be at least 8 characters long");
        }
        
        if (password.length() > 128) {
            return new PasswordValidationResult(false, "Password must be less than 128 characters");
        }
        
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        if (!hasUpperCase) {
            return new PasswordValidationResult(false, "Password must contain at least one uppercase letter");
        }
        
        if (!hasLowerCase) {
            return new PasswordValidationResult(false, "Password must contain at least one lowercase letter");
        }
        
        if (!hasDigit) {
            return new PasswordValidationResult(false, "Password must contain at least one digit");
        }
        
        if (!hasSpecialChar) {
            return new PasswordValidationResult(false, "Password must contain at least one special character");
        }
        
        return new PasswordValidationResult(true, "Password is valid");
    }
    
    /**
     * Inner class for password validation results
     */
    public static class PasswordValidationResult {
        private final boolean valid;
        private final String message;
        
        public PasswordValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
