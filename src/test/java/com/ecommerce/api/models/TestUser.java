package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Test User model class for API automation testing
 * Represents user data for testing user authentication and registration APIs
 * Includes validation methods for ensuring data integrity
 */
public class TestUser {
    
    private static final Logger logger = LoggerFactory.getLogger(TestUser.class);
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[1-9]\\d{1,14}$"
    );
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("firstname")
    private String firstname;
    
    @JsonProperty("lastname")
    private String lastname;
    
    // Metadata for test management
    private String testId;
    private boolean isCleanupRequired;
    private long createdTimestamp;
    
    /**
     * Default constructor
     */
    public TestUser() {
        this.createdTimestamp = System.currentTimeMillis();
        this.isCleanupRequired = true;
    }
    
    /**
     * Constructor with basic user data
     * @param username User's username
     * @param password User's password
     * @param email User's email
     */
    public TestUser(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    /**
     * Constructor with full user data
     * @param username User's username
     * @param password User's password
     * @param email User's email
     * @param phone User's phone number
     * @param firstname User's first name
     * @param lastname User's last name
     */
    public TestUser(String username, String password, String email, String phone, 
                   String firstname, String lastname) {
        this(username, password, email);
        this.phone = phone;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    
    // Getters and Setters
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getTestId() {
        return testId;
    }
    
    public void setTestId(String testId) {
        this.testId = testId;
    }
    
    public boolean isCleanupRequired() {
        return isCleanupRequired;
    }
    
    public void setCleanupRequired(boolean cleanupRequired) {
        isCleanupRequired = cleanupRequired;
    }
    
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    /**
     * Validate the test user data
     * @return ValidationResult containing validation status and messages
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        } else if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            result.addError(String.format("Username must be between %d and %d characters", 
                           MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH));
        } else if (!isValidUsername(username)) {
            result.addError("Username contains invalid characters");
        }
        
        // Validate password
        if (password == null || password.trim().isEmpty()) {
            result.addError("Password is required");
        } else if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            result.addError(String.format("Password must be between %d and %d characters", 
                           MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("Email format is invalid");
        }
        
        // Validate phone (optional field)
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            result.addError("Phone number format is invalid");
        }
        
        // Validate names (optional fields)
        if (firstname != null && (firstname.trim().isEmpty() || firstname.length() > 50)) {
            result.addError("First name must be between 1 and 50 characters if provided");
        }
        
        if (lastname != null && (lastname.trim().isEmpty() || lastname.length() > 50)) {
            result.addError("Last name must be between 1 and 50 characters if provided");
        }
        
        if (result.isValid()) {
            logger.debug("Test user validation passed for username: {}", username);
        } else {
            logger.warn("Test user validation failed for username: {}. Errors: {}", 
                       username, result.getErrors());
        }
        
        return result;
    }
    
    /**
     * Check if username contains only valid characters
     * @param username Username to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUsername(String username) {
        // Allow alphanumeric characters, underscores, and hyphens
        return username.matches("^[a-zA-Z0-9_-]+$");
    }
    
    /**
     * Check if this test user is valid for API testing
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return validate().isValid();
    }
    
    /**
     * Create a copy of this test user with a different username
     * Useful for creating variations for testing
     * @param newUsername New username for the copy
     * @return New TestUser instance with updated username
     */
    public TestUser withUsername(String newUsername) {
        TestUser copy = new TestUser(newUsername, this.password, this.email, 
                                   this.phone, this.firstname, this.lastname);
        copy.setTestId(this.testId);
        copy.setCleanupRequired(this.isCleanupRequired);
        return copy;
    }
    
    /**
     * Create a copy of this test user with a different email
     * @param newEmail New email for the copy
     * @return New TestUser instance with updated email
     */
    public TestUser withEmail(String newEmail) {
        TestUser copy = new TestUser(this.username, this.password, newEmail, 
                                   this.phone, this.firstname, this.lastname);
        copy.setTestId(this.testId);
        copy.setCleanupRequired(this.isCleanupRequired);
        return copy;
    }
    
    /**
     * Get user credentials for login testing
     * @return UserCredentials object
     */
    public UserCredentials getCredentials() {
        return new UserCredentials(username, password);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestUser testUser = (TestUser) o;
        return Objects.equals(username, testUser.username) &&
               Objects.equals(email, testUser.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
    
    @Override
    public String toString() {
        return "TestUser{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", firstname='" + firstname + '\'' +
               ", lastname='" + lastname + '\'' +
               ", testId='" + testId + '\'' +
               ", isCleanupRequired=" + isCleanupRequired +
               ", createdTimestamp=" + createdTimestamp +
               '}';
    }
    
    /**
     * Inner class for validation results
     */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrors() {
            return errors.toString();
        }
    }
    
    /**
     * Inner class for user credentials
     */
    public static class UserCredentials {
        @JsonProperty("username")
        private String username;
        
        @JsonProperty("password")
        private String password;
        
        public UserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        
        @Override
        public String toString() {
            return "UserCredentials{" +
                   "username='" + username + '\'' +
                   ", password='[PROTECTED]'" +
                   '}';
        }
    }
}