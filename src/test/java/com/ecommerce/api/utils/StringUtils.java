package com.ecommerce.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for string operations, data generation, and validation
 * Provides methods for string manipulation, validation, and test data generation
 */
public class StringUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);
    private static final SecureRandom random = new SecureRandom();
    
    // Character sets for generation
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String DIGITS = "0123456789";
    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
    public static final String ALPHANUMERIC = LOWERCASE_LETTERS + UPPERCASE_LETTERS + DIGITS;
    public static final String ALL_CHARACTERS = ALPHANUMERIC + SPECIAL_CHARACTERS;
    
    // Common validation patterns
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    public static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[1-9]\\d{1,14}$"
    );
    public static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    public static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$"
    );
    
    // Common first and last names for realistic data generation
    private static final List<String> FIRST_NAMES = Arrays.asList(
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
        "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Nancy", "Daniel", "Lisa",
        "Matthew", "Betty", "Anthony", "Helen", "Mark", "Sandra", "Donald", "Donna"
    );
    
    private static final List<String> LAST_NAMES = Arrays.asList(
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
        "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
        "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young"
    );
    
    /**
     * Check if string is null or empty
     * @param str String to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * Check if string is null, empty, or contains only whitespace
     * @param str String to check
     * @return true if null, empty, or whitespace only, false otherwise
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Get string length, treating null as 0
     * @param str String to measure
     * @return Length of string, 0 if null
     */
    public static int safeLength(String str) {
        return str == null ? 0 : str.length();
    }
    
    /**
     * Safely trim a string, handling null values
     * @param str String to trim
     * @return Trimmed string, or null if input was null
     */
    public static String safeTrim(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * Safely convert string to lowercase, handling null values
     * @param str String to convert
     * @return Lowercase string, or null if input was null
     */
    public static String safeLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }
    
    /**
     * Safely convert string to uppercase, handling null values
     * @param str String to convert
     * @return Uppercase string, or null if input was null
     */
    public static String safeUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }
    
    /**
     * Generate random string of specified length using given character set
     * @param length Length of string to generate
     * @param characterSet Character set to use
     * @return Generated random string
     */
    public static String generateRandomString(int length, String characterSet) {
        if (length <= 0 || isNullOrEmpty(characterSet)) {
            return "";
        }
        
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }
        return result.toString();
    }
    
    /**
     * Generate random alphanumeric string
     * @param length Length of string to generate
     * @return Generated random alphanumeric string
     */
    public static String generateRandomAlphanumeric(int length) {
        return generateRandomString(length, ALPHANUMERIC);
    }
    
    /**
     * Generate random alphabetic string
     * @param length Length of string to generate
     * @return Generated random alphabetic string
     */
    public static String generateRandomAlphabetic(int length) {
        return generateRandomString(length, LOWERCASE_LETTERS + UPPERCASE_LETTERS);
    }
    
    /**
     * Generate random numeric string
     * @param length Length of string to generate
     * @return Generated random numeric string
     */
    public static String generateRandomNumeric(int length) {
        return generateRandomString(length, DIGITS);
    }
    
    /**
     * Generate random string with all character types
     * @param length Length of string to generate
     * @return Generated random string with mixed characters
     */
    public static String generateRandomMixed(int length) {
        return generateRandomString(length, ALL_CHARACTERS);
    }
    
    /**
     * Generate realistic email address
     * @return Generated email address
     */
    public static String generateEmail() {
        String username = generateRandomAlphanumeric(8).toLowerCase();
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "test.com"};
        String domain = domains[random.nextInt(domains.length)];
        return username + "@" + domain;
    }
    
    /**
     * Generate realistic email address with custom domain
     * @param domain Email domain
     * @return Generated email address
     */
    public static String generateEmail(String domain) {
        String username = generateRandomAlphanumeric(8).toLowerCase();
        return username + "@" + domain;
    }
    
    /**
     * Generate realistic username
     * @return Generated username
     */
    public static String generateUsername() {
        String prefix = generateRandomAlphabetic(4).toLowerCase();
        String suffix = generateRandomNumeric(3);
        return prefix + suffix;
    }
    
    /**
     * Generate realistic password meeting common requirements
     * @return Generated password
     */
    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one of each required character type
        password.append(generateRandomString(1, LOWERCASE_LETTERS));
        password.append(generateRandomString(1, UPPERCASE_LETTERS));
        password.append(generateRandomString(1, DIGITS));
        password.append(generateRandomString(1, "@$!%*?&"));
        
        // Fill remaining length with random characters
        int remainingLength = 8 + random.nextInt(5); // 8-12 characters total
        password.append(generateRandomString(remainingLength - 4, ALPHANUMERIC + "@$!%*?&"));
        
        // Shuffle the password characters
        return shuffleString(password.toString());
    }
    
    /**
     * Generate realistic phone number
     * @return Generated phone number
     */
    public static String generatePhoneNumber() {
        StringBuilder phone = new StringBuilder("+1");
        phone.append(generateRandomNumeric(10));
        return phone.toString();
    }
    
    /**
     * Generate realistic first name
     * @return Generated first name
     */
    public static String generateFirstName() {
        return FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
    }
    
    /**
     * Generate realistic last name
     * @return Generated last name
     */
    public static String generateLastName() {
        return LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
    }
    
    /**
     * Generate realistic full name
     * @return Generated full name
     */
    public static String generateFullName() {
        return generateFirstName() + " " + generateLastName();
    }
    
    /**
     * Generate URL with random path
     * @param baseUrl Base URL
     * @return Generated URL
     */
    public static String generateUrl(String baseUrl) {
        String path = generateRandomAlphanumeric(8).toLowerCase();
        return baseUrl + "/" + path;
    }
    
    /**
     * Shuffle characters in a string
     * @param str String to shuffle
     * @return Shuffled string
     */
    public static String shuffleString(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        
        List<Character> characters = str.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
        
        Collections.shuffle(characters, random);
        
        return characters.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !isNullOrBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid phone format, false otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        return !isNullOrBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate username format
     * @param username Username to validate
     * @return true if valid username format, false otherwise
     */
    public static boolean isValidUsername(String username) {
        return !isNullOrBlank(username) && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return !isNullOrBlank(password) && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate URL format
     * @param url URL to validate
     * @return true if valid URL format, false otherwise
     */
    public static boolean isValidUrl(String url) {
        return !isNullOrBlank(url) && URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Check if string contains only alphabetic characters
     * @param str String to check
     * @return true if only alphabetic, false otherwise
     */
    public static boolean isAlphabetic(String str) {
        return !isNullOrEmpty(str) && str.chars().allMatch(Character::isLetter);
    }
    
    /**
     * Check if string contains only numeric characters
     * @param str String to check
     * @return true if only numeric, false otherwise
     */
    public static boolean isNumeric(String str) {
        return !isNullOrEmpty(str) && str.chars().allMatch(Character::isDigit);
    }
    
    /**
     * Check if string contains only alphanumeric characters
     * @param str String to check
     * @return true if only alphanumeric, false otherwise
     */
    public static boolean isAlphanumeric(String str) {
        return !isNullOrEmpty(str) && str.chars().allMatch(Character::isLetterOrDigit);
    }
    
    /**
     * Capitalize first letter of string
     * @param str String to capitalize
     * @return Capitalized string
     */
    public static String capitalize(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Convert camelCase to snake_case
     * @param camelCase CamelCase string
     * @return snake_case string
     */
    public static String camelToSnakeCase(String camelCase) {
        if (isNullOrEmpty(camelCase)) {
            return camelCase;
        }
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * Convert snake_case to camelCase
     * @param snakeCase snake_case string
     * @return camelCase string
     */
    public static String snakeToCamelCase(String snakeCase) {
        if (isNullOrEmpty(snakeCase)) {
            return snakeCase;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            }
        }
        
        return result.toString();
    }
    
    /**
     * Repeat string n times
     * @param str String to repeat
     * @param times Number of times to repeat
     * @return Repeated string
     */
    public static String repeat(String str, int times) {
        if (isNullOrEmpty(str) || times <= 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            result.append(str);
        }
        return result.toString();
    }
    
    /**
     * Reverse a string
     * @param str String to reverse
     * @return Reversed string
     */
    public static String reverse(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }
    
    /**
     * Count occurrences of substring in string
     * @param str Main string
     * @param substring Substring to count
     * @return Number of occurrences
     */
    public static int countOccurrences(String str, String substring) {
        if (isNullOrEmpty(str) || isNullOrEmpty(substring)) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        
        return count;
    }
    
    /**
     * Truncate string to specified length with ellipsis
     * @param str String to truncate
     * @param maxLength Maximum length
     * @return Truncated string
     */
    public static String truncate(String str, int maxLength) {
        if (isNullOrEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        
        if (maxLength <= 3) {
            return str.substring(0, maxLength);
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Remove all whitespace from string
     * @param str String to process
     * @return String without whitespace
     */
    public static String removeWhitespace(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s+", "");
    }
    
    /**
     * Normalize whitespace (replace multiple spaces with single space)
     * @param str String to normalize
     * @return Normalized string
     */
    public static String normalizeWhitespace(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Generate test data based on edge case patterns
     * @param pattern Edge case pattern
     * @return Generated test data
     */
    public static String generateEdgeCaseString(EdgeCasePattern pattern) {
        switch (pattern) {
            case EMPTY:
                return "";
            case WHITESPACE_ONLY:
                return "   ";
            case SINGLE_CHARACTER:
                return "a";
            case VERY_LONG:
                return repeat("a", 1000);
            case SPECIAL_CHARACTERS:
                return "!@#$%^&*()";
            case UNICODE:
                return "测试数据🚀émojis";
            case SQL_INJECTION:
                return "'; DROP TABLE users; --";
            case XSS_ATTEMPT:
                return "<script>alert('xss')</script>";
            case NULL_STRING:
                return "null";
            case UNDEFINED_STRING:
                return "undefined";
            case MIXED_CASE:
                return "MiXeD cAsE StRiNg";
            case LEADING_TRAILING_SPACES:
                return "  spaced string  ";
            case NUMBERS_ONLY:
                return "1234567890";
            case BOOLEAN_STRING:
                return random.nextBoolean() ? "true" : "false";
            default:
                return generateRandomAlphanumeric(10);
        }
    }
    
    /**
     * Enum for edge case patterns
     */
    public enum EdgeCasePattern {
        EMPTY,
        WHITESPACE_ONLY,
        SINGLE_CHARACTER,
        VERY_LONG,
        SPECIAL_CHARACTERS,
        UNICODE,
        SQL_INJECTION,
        XSS_ATTEMPT,
        NULL_STRING,
        UNDEFINED_STRING,
        MIXED_CASE,
        LEADING_TRAILING_SPACES,
        NUMBERS_ONLY,
        BOOLEAN_STRING
    }
}