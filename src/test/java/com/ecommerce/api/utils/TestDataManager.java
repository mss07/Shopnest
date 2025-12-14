package com.ecommerce.api.utils;

import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.models.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test Data Manager for API automation testing
 * Handles creation, management, and cleanup of test data
 * Provides thread-safe operations for parallel test execution
 */
public class TestDataManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataManager.class);
    private static TestDataManager instance;
    
    // Thread-safe collections for managing test data
    private final Map<String, TestUser> createdUsers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> testSessionUsers = new ConcurrentHashMap<>();
    private final AtomicLong userCounter = new AtomicLong(0);
    
    // Configuration
    private final ConfigurationManager configManager;
    private final SecureRandom random;
    
    // Test data generation constants
    private static final String[] FIRST_NAMES = {
        "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa",
        "James", "Maria", "William", "Jennifer", "Richard", "Linda", "Charles", "Patricia"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas"
    };
    
    private static final String[] EMAIL_DOMAINS = {
        "testmail.com", "example.org", "test-domain.net", "automation.test", "qa-testing.com"
    };
    
    /**
     * Private constructor for singleton pattern
     */
    private TestDataManager() {
        this.configManager = ConfigurationManager.getInstance();
        this.random = new SecureRandom();
        logger.info("TestDataManager initialized");
    }
    
    /**
     * Get singleton instance of TestDataManager
     * @return TestDataManager instance
     */
    public static synchronized TestDataManager getInstance() {
        if (instance == null) {
            instance = new TestDataManager();
        }
        return instance;
    }
    
    /**
     * Create a unique test user with generated data
     * @return TestUser with unique generated data
     */
    public TestUser createUniqueTestUser() {
        return createUniqueTestUser(null);
    }
    
    /**
     * Create a unique test user with generated data for a specific test session
     * @param testSessionId Test session identifier for grouping users
     * @return TestUser with unique generated data
     */
    public TestUser createUniqueTestUser(String testSessionId) {
        long userId = userCounter.incrementAndGet();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String prefix = configManager.getTestDataPrefix();
        
        // Generate unique username
        String username = String.format("%suser_%d_%s", prefix, userId, timestamp.substring(timestamp.length() - 6));
        
        // Generate unique email
        String emailDomain = EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        String email = String.format("%s@%s", username, emailDomain);
        
        // Generate password
        String password = generateSecurePassword();
        
        // Generate names
        String firstname = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastname = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        
        // Generate phone number
        String phone = generatePhoneNumber();
        
        TestUser testUser = new TestUser(username, password, email, phone, firstname, lastname);
        testUser.setTestId(generateTestId());
        testUser.setCleanupRequired(configManager.isTestDataCleanupEnabled());
        
        // Store user for cleanup tracking
        createdUsers.put(testUser.getUsername(), testUser);
        
        // Associate with test session if provided
        if (testSessionId != null) {
            testSessionUsers.computeIfAbsent(testSessionId, k -> ConcurrentHashMap.newKeySet())
                           .add(testUser.getUsername());
        }
        
        logger.debug("Created unique test user: {} for session: {}", username, testSessionId);
        return testUser;
    }
    
    /**
     * Create a test user with specific username
     * @param username Desired username
     * @return TestUser with specified username
     */
    public TestUser createTestUserWithUsername(String username) {
        if (createdUsers.containsKey(username)) {
            logger.warn("Test user with username '{}' already exists", username);
            return createdUsers.get(username);
        }
        
        String prefix = configManager.getTestDataPrefix();
        String fullUsername = username.startsWith(prefix) ? username : prefix + username;
        
        String emailDomain = EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        String email = String.format("%s@%s", fullUsername, emailDomain);
        String password = generateSecurePassword();
        String firstname = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastname = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String phone = generatePhoneNumber();
        
        TestUser testUser = new TestUser(fullUsername, password, email, phone, firstname, lastname);
        testUser.setTestId(generateTestId());
        testUser.setCleanupRequired(configManager.isTestDataCleanupEnabled());
        
        createdUsers.put(testUser.getUsername(), testUser);
        
        logger.debug("Created test user with specific username: {}", fullUsername);
        return testUser;
    }
    
    /**
     * Get valid user credentials for login testing
     * @return TestUser.UserCredentials with valid credentials
     */
    public TestUser.UserCredentials getValidUserCredentials() {
        TestUser testUser = createUniqueTestUser();
        return testUser.getCredentials();
    }
    
    /**
     * Get invalid user credentials for negative testing
     * @return TestUser.UserCredentials with invalid credentials
     */
    public TestUser.UserCredentials getInvalidUserCredentials() {
        String invalidUsername = configManager.getTestDataPrefix() + "invalid_user_" + System.currentTimeMillis();
        String invalidPassword = "wrongpassword123";
        return new TestUser.UserCredentials(invalidUsername, invalidPassword);
    }
    
    /**
     * Create multiple unique test users
     * @param count Number of users to create
     * @return List of TestUser objects
     */
    public List<TestUser> createMultipleTestUsers(int count) {
        return createMultipleTestUsers(count, null);
    }
    
    /**
     * Create multiple unique test users for a specific test session
     * @param count Number of users to create
     * @param testSessionId Test session identifier
     * @return List of TestUser objects
     */
    public List<TestUser> createMultipleTestUsers(int count, String testSessionId) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        
        List<TestUser> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createUniqueTestUser(testSessionId));
        }
        
        logger.info("Created {} test users for session: {}", count, testSessionId);
        return users;
    }
    
    /**
     * Get a test user by username
     * @param username Username to search for
     * @return TestUser if found, null otherwise
     */
    public TestUser getTestUser(String username) {
        return createdUsers.get(username);
    }
    
    /**
     * Get all test users created in a specific session
     * @param testSessionId Test session identifier
     * @return List of TestUser objects for the session
     */
    public List<TestUser> getTestUsersForSession(String testSessionId) {
        Set<String> usernames = testSessionUsers.get(testSessionId);
        if (usernames == null) {
            return new ArrayList<>();
        }
        
        return usernames.stream()
                       .map(createdUsers::get)
                       .filter(Objects::nonNull)
                       .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Clean up test data for a specific user
     * @param username Username of the user to clean up
     * @return true if cleanup was successful, false otherwise
     */
    public boolean cleanupTestUser(String username) {
        TestUser user = createdUsers.remove(username);
        if (user == null) {
            logger.warn("No test user found for cleanup: {}", username);
            return false;
        }
        
        // Remove from all test sessions
        testSessionUsers.values().forEach(userSet -> userSet.remove(username));
        
        logger.debug("Cleaned up test user: {}", username);
        return true;
    }
    
    /**
     * Clean up all test data for a specific test session
     * @param testSessionId Test session identifier
     * @return Number of users cleaned up
     */
    public int cleanupTestSession(String testSessionId) {
        Set<String> usernames = testSessionUsers.remove(testSessionId);
        if (usernames == null) {
            logger.debug("No test session found for cleanup: {}", testSessionId);
            return 0;
        }
        
        int cleanedCount = 0;
        for (String username : usernames) {
            if (createdUsers.remove(username) != null) {
                cleanedCount++;
            }
        }
        
        logger.info("Cleaned up {} test users for session: {}", cleanedCount, testSessionId);
        return cleanedCount;
    }
    
    /**
     * Clean up all test data
     * @return Number of users cleaned up
     */
    public int cleanupAllTestData() {
        int totalUsers = createdUsers.size();
        createdUsers.clear();
        testSessionUsers.clear();
        userCounter.set(0);
        
        logger.info("Cleaned up all test data. Total users removed: {}", totalUsers);
        return totalUsers;
    }
    
    /**
     * Get statistics about current test data
     * @return TestDataStatistics object
     */
    public TestDataStatistics getTestDataStatistics() {
        return new TestDataStatistics(
            createdUsers.size(),
            testSessionUsers.size(),
            userCounter.get(),
            configManager.isTestDataCleanupEnabled()
        );
    }
    
    /**
     * Generate a secure password for test users
     * @return Generated password
     */
    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        
        // Ensure password has at least one of each type
        password.append(chars.charAt(random.nextInt(26))); // Uppercase
        password.append(chars.charAt(26 + random.nextInt(26))); // Lowercase
        password.append(chars.charAt(52 + random.nextInt(10))); // Digit
        password.append(chars.charAt(62 + random.nextInt(5))); // Special char
        
        // Fill remaining length with random characters
        for (int i = 4; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, random);
        
        StringBuilder shuffledPassword = new StringBuilder();
        for (char c : passwordChars) {
            shuffledPassword.append(c);
        }
        
        return shuffledPassword.toString();
    }
    
    /**
     * Generate a realistic phone number
     * @return Generated phone number
     */
    private String generatePhoneNumber() {
        // Generate US format phone number: +1XXXXXXXXXX
        StringBuilder phone = new StringBuilder("+1");
        
        // Area code (first digit 2-9, second digit 0-9, third digit 0-9)
        phone.append(2 + random.nextInt(8));
        phone.append(random.nextInt(10));
        phone.append(random.nextInt(10));
        
        // Exchange code (first digit 2-9, second digit 0-9, third digit 0-9)
        phone.append(2 + random.nextInt(8));
        phone.append(random.nextInt(10));
        phone.append(random.nextInt(10));
        
        // Subscriber number (4 digits)
        for (int i = 0; i < 4; i++) {
            phone.append(random.nextInt(10));
        }
        
        return phone.toString();
    }
    
    /**
     * Generate a unique test ID
     * @return Generated test ID
     */
    private String generateTestId() {
        return "TEST_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
    
    /**
     * Check if cleanup is enabled
     * @return true if cleanup is enabled, false otherwise
     */
    public boolean isCleanupEnabled() {
        return configManager.isTestDataCleanupEnabled();
    }
    
    /**
     * Statistics class for test data information
     */
    public static class TestDataStatistics {
        private final int totalUsers;
        private final int activeSessions;
        private final long userCounter;
        private final boolean cleanupEnabled;
        
        public TestDataStatistics(int totalUsers, int activeSessions, long userCounter, boolean cleanupEnabled) {
            this.totalUsers = totalUsers;
            this.activeSessions = activeSessions;
            this.userCounter = userCounter;
            this.cleanupEnabled = cleanupEnabled;
        }
        
        public int getTotalUsers() {
            return totalUsers;
        }
        
        public int getActiveSessions() {
            return activeSessions;
        }
        
        public long getUserCounter() {
            return userCounter;
        }
        
        public boolean isCleanupEnabled() {
            return cleanupEnabled;
        }
        
        @Override
        public String toString() {
            return "TestDataStatistics{" +
                   "totalUsers=" + totalUsers +
                   ", activeSessions=" + activeSessions +
                   ", userCounter=" + userCounter +
                   ", cleanupEnabled=" + cleanupEnabled +
                   '}';
        }
    }
}