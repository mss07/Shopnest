package com.ecommerce.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for generating realistic test data
 * Provides methods for creating various types of test data for API testing
 */
public class DataGenerationUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DataGenerationUtils.class);
    private static final SecureRandom random = new SecureRandom();
    
    // Common data sets for realistic generation
    private static final List<String> STREET_NAMES = Arrays.asList(
        "Main St", "Oak Ave", "Pine Rd", "Elm Dr", "Maple Ln", "Cedar Blvd", 
        "First St", "Second Ave", "Park Rd", "Hill Dr", "Valley Ln", "River St"
    );
    
    private static final List<String> CITIES = Arrays.asList(
        "Springfield", "Franklin", "Georgetown", "Madison", "Washington", "Arlington",
        "Centerville", "Fairview", "Greenville", "Mount Pleasant", "Riverside", "Oakland"
    );
    
    private static final List<String> STATES = Arrays.asList(
        "CA", "NY", "TX", "FL", "IL", "PA", "OH", "GA", "NC", "MI", "NJ", "VA"
    );
    
    private static final List<String> COUNTRIES = Arrays.asList(
        "United States", "Canada", "United Kingdom", "Australia", "Germany", "France"
    );
    
    /**
     * Generate a random string of specified length
     * @param length Length of the string to generate
     * @return Random string
     */
    public static String generateRandomString(int length) {
        return generateRandomString(length, false);
    }
    
    /**
     * Generate a random string of specified length
     * @param length Length of the string to generate
     * @param alphaNumericOnly If true, only alphanumeric characters are used
     * @return Random string
     */
    public static String generateRandomString(int length, boolean alphaNumericOnly) {
        String chars = alphaNumericOnly ? 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" :
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return result.toString();
    }
    
    /**
     * Generate a random number within a range
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random number
     */
    public static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min) + min;
    }
    
    /**
     * Generate a random double within a range
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random double
     */
    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    /**
     * Generate a random boolean value
     * @return Random boolean
     */
    public static boolean generateRandomBoolean() {
        return random.nextBoolean();
    }
    
    /**
     * Generate a unique identifier
     * @return Unique identifier string
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a short unique identifier (8 characters)
     * @return Short unique identifier
     */
    public static String generateShortUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Generate a timestamp-based unique identifier
     * @return Timestamp-based unique identifier
     */
    public static String generateTimestampId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + 
               "_" + generateRandomNumber(1000, 9999);
    }
    
    /**
     * Generate a realistic address
     * @return Generated address string
     */
    public static String generateAddress() {
        int streetNumber = generateRandomNumber(1, 9999);
        String streetName = getRandomElement(STREET_NAMES);
        return streetNumber + " " + streetName;
    }
    
    /**
     * Generate a realistic city name
     * @return Generated city name
     */
    public static String generateCity() {
        return getRandomElement(CITIES);
    }
    
    /**
     * Generate a realistic state code
     * @return Generated state code
     */
    public static String generateState() {
        return getRandomElement(STATES);
    }
    
    /**
     * Generate a realistic country name
     * @return Generated country name
     */
    public static String generateCountry() {
        return getRandomElement(COUNTRIES);
    }
    
    /**
     * Generate a realistic ZIP code
     * @return Generated ZIP code
     */
    public static String generateZipCode() {
        return String.format("%05d", generateRandomNumber(10000, 99999));
    }
    
    /**
     * Generate a realistic company name
     * @return Generated company name
     */
    public static String generateCompanyName() {
        String[] prefixes = {"Tech", "Global", "Digital", "Smart", "Advanced", "Modern"};
        String[] suffixes = {"Solutions", "Systems", "Corp", "Inc", "LLC", "Group"};
        
        return getRandomElement(Arrays.asList(prefixes)) + " " + 
               getRandomElement(Arrays.asList(suffixes));
    }
    
    /**
     * Generate a realistic job title
     * @return Generated job title
     */
    public static String generateJobTitle() {
        String[] levels = {"Junior", "Senior", "Lead", "Principal", ""};
        String[] roles = {"Developer", "Engineer", "Analyst", "Manager", "Specialist", "Consultant"};
        String[] departments = {"Software", "Quality", "Product", "Data", "Security", "DevOps"};
        
        String level = getRandomElement(Arrays.asList(levels));
        String department = getRandomElement(Arrays.asList(departments));
        String role = getRandomElement(Arrays.asList(roles));
        
        return (level.isEmpty() ? "" : level + " ") + department + " " + role;
    }
    
    /**
     * Generate a realistic price value
     * @param min Minimum price
     * @param max Maximum price
     * @return Generated price with 2 decimal places
     */
    public static double generatePrice(double min, double max) {
        double price = generateRandomDouble(min, max);
        return Math.round(price * 100.0) / 100.0;
    }
    
    /**
     * Generate a realistic product name
     * @return Generated product name
     */
    public static String generateProductName() {
        String[] adjectives = {"Premium", "Deluxe", "Professional", "Advanced", "Standard", "Basic"};
        String[] products = {"Widget", "Gadget", "Device", "Tool", "System", "Solution"};
        String[] versions = {"Pro", "Plus", "Lite", "Max", "Mini", "XL"};
        
        String adjective = getRandomElement(Arrays.asList(adjectives));
        String product = getRandomElement(Arrays.asList(products));
        String version = random.nextBoolean() ? " " + getRandomElement(Arrays.asList(versions)) : "";
        
        return adjective + " " + product + version;
    }
    
    /**
     * Generate a realistic description
     * @param minWords Minimum number of words
     * @param maxWords Maximum number of words
     * @return Generated description
     */
    public static String generateDescription(int minWords, int maxWords) {
        String[] words = {
            "innovative", "reliable", "efficient", "powerful", "versatile", "user-friendly",
            "advanced", "cutting-edge", "robust", "scalable", "secure", "flexible",
            "solution", "system", "platform", "framework", "application", "service",
            "designed", "built", "created", "developed", "engineered", "optimized",
            "performance", "quality", "excellence", "innovation", "technology", "features"
        };
        
        int wordCount = generateRandomNumber(minWords, maxWords + 1);
        StringBuilder description = new StringBuilder();
        
        for (int i = 0; i < wordCount; i++) {
            if (i > 0) {
                description.append(" ");
            }
            description.append(getRandomElement(Arrays.asList(words)));
        }
        
        // Capitalize first letter
        if (description.length() > 0) {
            description.setCharAt(0, Character.toUpperCase(description.charAt(0)));
        }
        
        return description.toString();
    }
    
    /**
     * Generate a random date string in ISO format
     * @param daysFromNow Number of days from current date (can be negative for past dates)
     * @return Generated date string
     */
    public static String generateDateString(int daysFromNow) {
        LocalDateTime date = LocalDateTime.now().plusDays(daysFromNow);
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Generate a random timestamp string
     * @return Generated timestamp string
     */
    public static String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * Get a random element from a list
     * @param list List to select from
     * @param <T> Type of list elements
     * @return Random element from the list
     */
    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    /**
     * Generate test data with specific patterns for edge case testing
     * @param pattern Pattern type (empty, null, long, special_chars, etc.)
     * @return Generated test data based on pattern
     */
    public static String generateEdgeCaseData(EdgeCasePattern pattern) {
        switch (pattern) {
            case EMPTY_STRING:
                return "";
            case WHITESPACE_ONLY:
                return "   ";
            case VERY_LONG_STRING:
                return generateRandomString(1000);
            case SPECIAL_CHARACTERS:
                return "!@#$%^&*()_+-=[]{}|;':\",./<>?";
            case UNICODE_CHARACTERS:
                return "测试数据 🚀 émojis ñoño";
            case SQL_INJECTION:
                return "'; DROP TABLE users; --";
            case XSS_ATTEMPT:
                return "<script>alert('xss')</script>";
            case NULL_STRING:
                return "null";
            case UNDEFINED_STRING:
                return "undefined";
            default:
                return generateRandomString(10);
        }
    }
    
    /**
     * Enum for edge case patterns
     */
    public enum EdgeCasePattern {
        EMPTY_STRING,
        WHITESPACE_ONLY,
        VERY_LONG_STRING,
        SPECIAL_CHARACTERS,
        UNICODE_CHARACTERS,
        SQL_INJECTION,
        XSS_ATTEMPT,
        NULL_STRING,
        UNDEFINED_STRING
    }
}