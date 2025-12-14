package com.ecommerce.api.utils;

import com.ecommerce.api.models.TestUser;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Unit tests for TestDataManager
 * Validates test data creation, management, and cleanup functionality
 */
public class TestDataManagerTest {
    
    private TestDataManager testDataManager;
    private TestIsolationManager isolationManager;
    
    @BeforeMethod
    public void setUp() {
        testDataManager = TestDataManager.getInstance();
        isolationManager = TestIsolationManager.getInstance();
        
        // Clean up any existing test data
        testDataManager.cleanupAllTestData();
    }
    
    @AfterMethod
    public void tearDown() {
        // Clean up test data after each test
        testDataManager.cleanupAllTestData();
        isolationManager.forceCleanupAllSessions();
    }
    
    @Test
    public void testCreateUniqueTestUser() {
        // Test creating a unique test user
        TestUser user = testDataManager.createUniqueTestUser();
        
        Assert.assertNotNull(user, "Test user should not be null");
        Assert.assertNotNull(user.getUsername(), "Username should not be null");
        Assert.assertNotNull(user.getEmail(), "Email should not be null");
        Assert.assertNotNull(user.getPassword(), "Password should not be null");
        Assert.assertTrue(user.isValid(), "Test user should be valid");
        
        // Verify user is tracked
        TestUser retrievedUser = testDataManager.getTestUser(user.getUsername());
        Assert.assertEquals(retrievedUser, user, "Retrieved user should match created user");
    }
    
    @Test
    public void testCreateMultipleUniqueUsers() {
        // Test creating multiple unique users
        int userCount = 5;
        List<TestUser> users = testDataManager.createMultipleTestUsers(userCount);
        
        Assert.assertEquals(users.size(), userCount, "Should create correct number of users");
        
        // Verify all users are unique
        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                Assert.assertNotEquals(users.get(i).getUsername(), users.get(j).getUsername(),
                                     "All usernames should be unique");
                Assert.assertNotEquals(users.get(i).getEmail(), users.get(j).getEmail(),
                                     "All emails should be unique");
            }
        }
        
        // Verify all users are valid
        for (TestUser user : users) {
            Assert.assertTrue(user.isValid(), "All users should be valid");
        }
    }
    
    @Test
    public void testCreateTestUserWithSpecificUsername() {
        // Test creating user with specific username
        String customUsername = "custom_test_user";
        TestUser user = testDataManager.createTestUserWithUsername(customUsername);
        
        Assert.assertNotNull(user, "Test user should not be null");
        Assert.assertTrue(user.getUsername().contains(customUsername), 
                         "Username should contain the custom name");
        Assert.assertTrue(user.isValid(), "Test user should be valid");
    }
    
    @Test
    public void testGetValidAndInvalidCredentials() {
        // Test getting valid credentials
        TestUser.UserCredentials validCredentials = testDataManager.getValidUserCredentials();
        Assert.assertNotNull(validCredentials, "Valid credentials should not be null");
        Assert.assertNotNull(validCredentials.getUsername(), "Valid username should not be null");
        Assert.assertNotNull(validCredentials.getPassword(), "Valid password should not be null");
        
        // Test getting invalid credentials
        TestUser.UserCredentials invalidCredentials = testDataManager.getInvalidUserCredentials();
        Assert.assertNotNull(invalidCredentials, "Invalid credentials should not be null");
        Assert.assertNotNull(invalidCredentials.getUsername(), "Invalid username should not be null");
        Assert.assertNotNull(invalidCredentials.getPassword(), "Invalid password should not be null");
        
        // Verify they are different
        Assert.assertNotEquals(validCredentials.getUsername(), invalidCredentials.getUsername(),
                              "Valid and invalid credentials should be different");
    }
    
    @Test
    public void testTestSessionManagement() {
        // Test creating users for a specific session
        String sessionId = "test_session_1";
        int userCount = 3;
        
        List<TestUser> sessionUsers = testDataManager.createMultipleTestUsers(userCount, sessionId);
        Assert.assertEquals(sessionUsers.size(), userCount, "Should create correct number of users");
        
        // Verify users are associated with session
        List<TestUser> retrievedUsers = testDataManager.getTestUsersForSession(sessionId);
        Assert.assertEquals(retrievedUsers.size(), userCount, 
                           "Should retrieve correct number of users for session");
        
        // Test session cleanup
        int cleanedCount = testDataManager.cleanupTestSession(sessionId);
        Assert.assertEquals(cleanedCount, userCount, "Should clean up all session users");
        
        // Verify session is empty after cleanup
        List<TestUser> emptySession = testDataManager.getTestUsersForSession(sessionId);
        Assert.assertTrue(emptySession.isEmpty(), "Session should be empty after cleanup");
    }
    
    @Test
    public void testUserCleanup() {
        // Create a test user
        TestUser user = testDataManager.createUniqueTestUser();
        String username = user.getUsername();
        
        // Verify user exists
        Assert.assertNotNull(testDataManager.getTestUser(username), "User should exist");
        
        // Clean up user
        boolean cleaned = testDataManager.cleanupTestUser(username);
        Assert.assertTrue(cleaned, "User cleanup should be successful");
        
        // Verify user is removed
        Assert.assertNull(testDataManager.getTestUser(username), "User should be removed after cleanup");
    }
    
    @Test
    public void testTestDataStatistics() {
        // Create some test users
        testDataManager.createMultipleTestUsers(3);
        testDataManager.createMultipleTestUsers(2, "session1");
        
        // Get statistics
        TestDataManager.TestDataStatistics stats = testDataManager.getTestDataStatistics();
        
        Assert.assertEquals(stats.getTotalUsers(), 5, "Should have correct total user count");
        Assert.assertEquals(stats.getActiveSessions(), 1, "Should have correct session count");
        Assert.assertTrue(stats.getUserCounter() >= 5, "User counter should be at least 5");
    }
    
    @Test
    public void testTestUserValidation() {
        // Create a test user and verify validation
        TestUser user = testDataManager.createUniqueTestUser();
        
        TestUser.ValidationResult result = user.validate();
        Assert.assertTrue(result.isValid(), "Generated user should be valid");
        Assert.assertTrue(result.getErrors().isEmpty(), "Valid user should have no errors");
        
        // Test invalid user
        TestUser invalidUser = new TestUser("", "", "invalid-email");
        TestUser.ValidationResult invalidResult = invalidUser.validate();
        Assert.assertFalse(invalidResult.isValid(), "Invalid user should fail validation");
        Assert.assertFalse(invalidResult.getErrors().isEmpty(), "Invalid user should have errors");
    }
    
    @Test
    public void testUserCredentialsCreation() {
        // Test user credentials creation
        TestUser user = testDataManager.createUniqueTestUser();
        TestUser.UserCredentials credentials = user.getCredentials();
        
        Assert.assertNotNull(credentials, "Credentials should not be null");
        Assert.assertEquals(credentials.getUsername(), user.getUsername(), 
                           "Credentials username should match user username");
        Assert.assertEquals(credentials.getPassword(), user.getPassword(), 
                           "Credentials password should match user password");
    }
    
    @Test
    public void testUserCopyMethods() {
        // Test user copy methods
        TestUser originalUser = testDataManager.createUniqueTestUser();
        
        // Test withUsername
        String newUsername = "new_username";
        TestUser userWithNewUsername = originalUser.withUsername(newUsername);
        Assert.assertEquals(userWithNewUsername.getUsername(), newUsername, 
                           "New user should have updated username");
        Assert.assertEquals(userWithNewUsername.getEmail(), originalUser.getEmail(), 
                           "New user should have same email");
        
        // Test withEmail
        String newEmail = "newemail@test.com";
        TestUser userWithNewEmail = originalUser.withEmail(newEmail);
        Assert.assertEquals(userWithNewEmail.getEmail(), newEmail, 
                           "New user should have updated email");
        Assert.assertEquals(userWithNewEmail.getUsername(), originalUser.getUsername(), 
                           "New user should have same username");
    }
}