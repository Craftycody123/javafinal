package backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * Provides methods to validate various types of user input
 */
public class ValidationUtils {
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
        "^[A-Z0-9]{2,10}$"
    );
    
    private static final Pattern LICENSE_NUMBER_PATTERN = Pattern.compile(
        "^[A-Z0-9]{8,20}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Validate email address
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number
     * @param phone phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove spaces, dashes, and parentheses
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate license plate
     * @param licensePlate license plate to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        return LICENSE_PLATE_PATTERN.matcher(licensePlate.trim().toUpperCase()).matches();
    }
    
    /**
     * Validate driver's license number
     * @param licenseNumber license number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return false;
        }
        return LICENSE_NUMBER_PATTERN.matcher(licenseNumber.trim().toUpperCase()).matches();
    }
    
    /**
     * Validate username
     * @param username username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validate date string
     * @param dateString date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateString.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validate date range
     * @param startDate start date
     * @param endDate end date
     * @return true if valid range, false otherwise
     */
    public static boolean isValidDateRange(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            return false;
        }
        
        try {
            LocalDate start = LocalDate.parse(startDate.trim(), DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate.trim(), DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            
            // Start date should not be in the past
            if (start.isBefore(today)) {
                return false;
            }
            
            // End date should be after start date
            return end.isAfter(start);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validate name (first name, last name)
     * @param name name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && 
               trimmedName.length() <= 50 && 
               trimmedName.matches("^[a-zA-Z\\s\\-']+$");
    }
    
    /**
     * Validate age based on date of birth
     * @param dateOfBirth date of birth string
     * @param minAge minimum age required
     * @return true if age is valid, false otherwise
     */
    public static boolean isValidAge(String dateOfBirth, int minAge) {
        if (!isValidDate(dateOfBirth)) {
            return false;
        }
        
        try {
            LocalDate birthDate = LocalDate.parse(dateOfBirth.trim(), DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            int age = today.getYear() - birthDate.getYear();
            
            // Adjust if birthday hasn't occurred this year
            if (today.getDayOfYear() < birthDate.getDayOfYear()) {
                age--;
            }
            
            return age >= minAge;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validate monetary amount
     * @param amount amount string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        
        try {
            double value = Double.parseDouble(amount.trim());
            return value >= 0 && value <= 999999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate year
     * @param year year to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidYear(String year) {
        if (year == null || year.trim().isEmpty()) {
            return false;
        }
        
        try {
            int yearValue = Integer.parseInt(year.trim());
            int currentYear = LocalDate.now().getYear();
            return yearValue >= 1900 && yearValue <= currentYear + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate positive integer
     * @param value value to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPositiveInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            int intValue = Integer.parseInt(value.trim());
            return intValue > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Sanitize string input
     * @param input input string
     * @return sanitized string
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potentially dangerous characters
                   .replaceAll("\\s+", " ");   // Normalize whitespace
    }
    
    /**
     * Validate text length
     * @param text text to validate
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if valid, false otherwise
     */
    public static boolean isValidTextLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return minLength == 0;
        }
        
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }
}
