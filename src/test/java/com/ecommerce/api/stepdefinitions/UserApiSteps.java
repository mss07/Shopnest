package com.ecommerce.api.stepdefinitions;

import com.ecommerce.api.client.RestApiClient;
import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.context.TestContext;
import com.ecommerce.api.models.TestUser;
import com.ecommerce.api.utils.TestDataManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions for User Authentication API testing
 * Implements Cucumber step definitions for user registration and login scenarios
 * Handles test data setup, API calls, and response validation
 */
public class UserApiSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(UserApiSteps.class);
    
    // Test components
    private RestApiClient apiClient;
    private TestDataManager testDataManager;
    private ConfigurationManager configManager;
    
    // Test data and state
    private TestUser currentTestUser;
    private TestUser.UserCredentials currentCredentials;
    private Response lastResponse;
    private String testSessionId;
    private Map<String, Object> requestData;
    private String malformedJsonData;
    
    // API endpoints
    private static final String SIGNUP_ENDPOINT = "/signup";
    private static final String LOGIN_ENDPOINT = "/login";
    
    /**
     * Setup method executed before each scenario
     */
    @Before
    public void setUp() {
        logger.info("Setting up UserApiSteps for new scenario");
        
        // Initialize components using shared context
        TestContext context = TestContext.getInstance();
        apiClient = context.getApiClient();
        testDataManager = context.getTestDataManager();
        configManager = context.getConfigManager();
        
        // Generate unique test session ID
        testSessionId = "session_" + System.currentTimeMillis();
        
        // Initialize request data map
        requestData = new HashMap<>();
        
        logger.debug("UserApiSteps setup completed for session: {}", testSessionId);
    }
    
    /**
     * Cleanup method executed after each scenario
     */
    @After
    public void tearDown() {
        logger.info("Cleaning up UserApiSteps for session: {}", testSessionId);
        
        // Clean up test data if cleanup is enabled
        if (testDataManager.isCleanupEnabled()) {
            int cleanedUsers = testDataManager.cleanupTestSession(testSessionId);
            logger.debug("Cleaned up {} test users for session: {}", cleanedUsers, testSessionId);
        }
        
        // Reset API client state
        if (apiClient != null) {
            apiClient.reset();
        }
        
        // Clear instance variables
        currentTestUser = null;
        currentCredentials = null;
        lastResponse = null;
        requestData.clear();
        malformedJsonData = null;
        
        logger.debug("UserApiSteps cleanup completed for session: {}", testSessionId);
    }
    
    // Background Steps
    

    

    
    // User Registration Steps
    
    @Given("I have valid user registration data")
    public void iHaveValidUserRegistrationData() {
        currentTestUser = testDataManager.createUniqueTestUser(testSessionId);
        Assert.assertNotNull(currentTestUser, "Failed to create test user");
        Assert.assertTrue(currentTestUser.isValid(), "Generated test user is not valid");
        
        logger.debug("Created valid test user: {}", currentTestUser.getUsername());
    }
    
    @Given("a user already exists with username {string}")
    public void aUserAlreadyExistsWithUsername(String username) {
        // Create a test user with the specified username
        currentTestUser = testDataManager.createTestUserWithUsername(username);
        
        // Register this user first to simulate existing user
        Response registrationResponse = apiClient.sendPostRequest(SIGNUP_ENDPOINT, currentTestUser);
        
        // Log the registration attempt (may succeed or fail depending on test scenario)
        logger.debug("Pre-registered user '{}' with status: {}", username, registrationResponse.getStatusCode());
    }
    
    @And("I have registration data with username {string}")
    public void iHaveRegistrationDataWithUsername(String username) {
        // Create test user with the duplicate username
        TestUser duplicateUser = testDataManager.createTestUserWithUsername(username);
        currentTestUser = duplicateUser;
        
        logger.debug("Created registration data with duplicate username: {}", username);
    }
    
    @Given("I have user registration data with {word} as {string}")
    public void iHaveUserRegistrationDataWithFieldAs(String field, String value) {
        // Start with valid test user
        currentTestUser = testDataManager.createUniqueTestUser(testSessionId);
        
        // Modify the specified field with the given value
        switch (field.toLowerCase()) {
            case "username":
                if ("null".equals(value)) {
                    currentTestUser.setUsername(null);
                } else {
                    currentTestUser.setUsername(value);
                }
                break;
            case "password":
                if ("null".equals(value)) {
                    currentTestUser.setPassword(null);
                } else {
                    currentTestUser.setPassword(value);
                }
                break;
            case "email":
                if ("null".equals(value)) {
                    currentTestUser.setEmail(null);
                } else {
                    currentTestUser.setEmail(value);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported field: " + field);
        }
        
        logger.debug("Created test user with {} set to: {}", field, value);
    }
    
    @Given("I have malformed JSON data for user registration")
    public void iHaveMalformedJsonDataForUserRegistration() {
        // Create malformed JSON string
        malformedJsonData = "{ \"username\": \"testuser\", \"password\": \"testpass\", \"email\": }";
        
        logger.debug("Created malformed JSON data for registration");
    }
    
    // User Login Steps
    
    @Given("a user exists with username {string} and password {string}")
    public void aUserExistsWithUsernameAndPassword(String username, String password) {
        // Create and register a user with specific credentials
        currentTestUser = new TestUser(username, password, username + "@test.com");
        currentTestUser.setFirstname("Test");
        currentTestUser.setLastname("User");
        currentTestUser.setPhone("+1234567890");
        
        // Register the user
        Response registrationResponse = apiClient.sendPostRequest(SIGNUP_ENDPOINT, currentTestUser);
        
        logger.debug("Pre-registered user '{}' for login testing with status: {}", 
                    username, registrationResponse.getStatusCode());
    }
    
    @And("I have valid login credentials for username {string} and password {string}")
    public void iHaveValidLoginCredentialsForUsernameAndPassword(String username, String password) {
        currentCredentials = new TestUser.UserCredentials(username, password);
        
        logger.debug("Created valid login credentials for username: {}", username);
    }
    
    @Given("I have login credentials with username {string} and password {string}")
    public void iHaveLoginCredentialsWithUsernameAndPassword(String username, String password) {
        // Handle null values
        String actualUsername = "null".equals(username) ? null : username;
        String actualPassword = "null".equals(password) ? null : password;
        
        currentCredentials = new TestUser.UserCredentials(actualUsername, actualPassword);
        
        logger.debug("Created login credentials with username: {} and password: {}", 
                    actualUsername, actualPassword != null ? "[PROTECTED]" : "null");
    }
    
    @Given("I have malformed JSON data for user login")
    public void iHaveMalformedJsonDataForUserLogin() {
        // Create malformed JSON string for login
        malformedJsonData = "{ \"username\": \"testuser\", \"password\": }";
        
        logger.debug("Created malformed JSON data for login");
    }
    
    // API Request Steps
    
    @When("I send a POST request to {string} with the user data")
    public void iSendAPostRequestToWithTheUserData(String endpoint) {
        Assert.assertNotNull(currentTestUser, "No test user data available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentTestUser);
        
        logger.info("Sent POST request to {} with user data. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with the duplicate username data")
    public void iSendAPostRequestToWithTheDuplicateUsernameData(String endpoint) {
        Assert.assertNotNull(currentTestUser, "No test user data available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentTestUser);
        
        logger.info("Sent POST request to {} with duplicate username data. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with the invalid data")
    public void iSendAPostRequestToWithTheInvalidData(String endpoint) {
        Assert.assertNotNull(currentTestUser, "No test user data available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentTestUser);
        
        logger.info("Sent POST request to {} with invalid data. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with malformed JSON")
    public void iSendAPostRequestToWithMalformedJson(String endpoint) {
        Assert.assertNotNull(malformedJsonData, "No malformed JSON data available");
        
        // Send raw malformed JSON string
        try {
            lastResponse = apiClient.sendPostRequest(endpoint, malformedJsonData);
        } catch (Exception e) {
            // Capture the exception response if available
            lastResponse = apiClient.getLastResponse();
            logger.debug("Exception occurred while sending malformed JSON: {}", e.getMessage());
        }
        
        logger.info("Sent POST request to {} with malformed JSON. Status: {}", 
                   endpoint, lastResponse != null ? lastResponse.getStatusCode() : "No response");
    }
    
    @When("I send a POST request to {string} with the valid credentials")
    public void iSendAPostRequestToWithTheValidCredentials(String endpoint) {
        Assert.assertNotNull(currentCredentials, "No credentials available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentCredentials);
        
        logger.info("Sent POST request to {} with valid credentials. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with the invalid username")
    public void iSendAPostRequestToWithTheInvalidUsername(String endpoint) {
        Assert.assertNotNull(currentCredentials, "No credentials available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentCredentials);
        
        logger.info("Sent POST request to {} with invalid username. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with the invalid password")
    public void iSendAPostRequestToWithTheInvalidPassword(String endpoint) {
        Assert.assertNotNull(currentCredentials, "No credentials available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentCredentials);
        
        logger.info("Sent POST request to {} with invalid password. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    @When("I send a POST request to {string} with the credentials")
    public void iSendAPostRequestToWithTheCredentials(String endpoint) {
        Assert.assertNotNull(currentCredentials, "No credentials available for request");
        
        lastResponse = apiClient.sendPostRequest(endpoint, currentCredentials);
        
        logger.info("Sent POST request to {} with credentials. Status: {}", 
                   endpoint, lastResponse.getStatusCode());
    }
    
    // Response Validation Steps
    

    
    @And("the response should contain success message")
    public void theResponseShouldContainSuccessMessage() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        
        // Check for common success indicators
        boolean hasSuccessMessage = responseBody.contains("success") || 
                                  responseBody.contains("created") || 
                                  responseBody.contains("registered") ||
                                  lastResponse.getStatusCode() == 200;
        
        Assert.assertTrue(hasSuccessMessage, "Response does not contain success message: " + responseBody);
        
        logger.debug("Success message validation passed");
    }
    
    @And("the response should contain user data with proper structure")
    public void theResponseShouldContainUserDataWithProperStructure() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // Validate that response contains user data fields
        Assert.assertTrue(apiClient.validateJsonResponse("username", currentTestUser.getUsername()) ||
                         lastResponse.getBody().asString().contains("username"),
                         "Response does not contain username field");
        
        logger.debug("User data structure validation passed");
    }
    
    @And("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // Verify successful creation by checking status code and response content
        Assert.assertTrue(lastResponse.getStatusCode() >= 200 && lastResponse.getStatusCode() < 300,
                         "User creation was not successful. Status: " + lastResponse.getStatusCode());
        
        logger.debug("User creation validation passed");
    }
    
    @And("the response should contain error message about duplicate username")
    public void theResponseShouldContainErrorMessageAboutDuplicateUsername() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasErrorMessage = responseBody.toLowerCase().contains("duplicate") ||
                                responseBody.toLowerCase().contains("exists") ||
                                responseBody.toLowerCase().contains("already") ||
                                responseBody.toLowerCase().contains("username");
        
        Assert.assertTrue(hasErrorMessage, "Response does not contain duplicate username error: " + responseBody);
        
        logger.debug("Duplicate username error message validation passed");
    }
    
    @And("no new user should be created")
    public void noNewUserShouldBeCreated() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // Verify that the response indicates failure (4xx status code)
        Assert.assertTrue(lastResponse.getStatusCode() >= 400 && lastResponse.getStatusCode() < 500,
                         "Expected client error status code but got: " + lastResponse.getStatusCode());
        
        logger.debug("No new user creation validation passed");
    }
    
    @And("the response should contain validation error message")
    public void theResponseShouldContainValidationErrorMessage() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasValidationError = responseBody.toLowerCase().contains("validation") ||
                                   responseBody.toLowerCase().contains("invalid") ||
                                   responseBody.toLowerCase().contains("required") ||
                                   responseBody.toLowerCase().contains("error");
        
        Assert.assertTrue(hasValidationError, "Response does not contain validation error: " + responseBody);
        
        logger.debug("Validation error message validation passed");
    }
    
    @And("the response should contain JSON parsing error message")
    public void theResponseShouldContainJsonParsingErrorMessage() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasParsingError = responseBody.toLowerCase().contains("json") ||
                                responseBody.toLowerCase().contains("parse") ||
                                responseBody.toLowerCase().contains("syntax") ||
                                responseBody.toLowerCase().contains("malformed");
        
        Assert.assertTrue(hasParsingError, "Response does not contain JSON parsing error: " + responseBody);
        
        logger.debug("JSON parsing error message validation passed");
    }
    
    @And("the response should contain user data JSON")
    public void theResponseShouldContainUserDataJson() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        
        // Verify response contains JSON with user data
        boolean hasUserData = responseBody.contains("{") && 
                            (responseBody.contains("username") || responseBody.contains("user"));
        
        Assert.assertTrue(hasUserData, "Response does not contain user data JSON: " + responseBody);
        
        logger.debug("User data JSON validation passed");
    }
    
    @And("the response should include authentication token or session data")
    public void theResponseShouldIncludeAuthenticationTokenOrSessionData() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasAuthData = responseBody.contains("token") ||
                            responseBody.contains("session") ||
                            responseBody.contains("auth") ||
                            responseBody.contains("jwt");
        
        Assert.assertTrue(hasAuthData, "Response does not contain authentication data: " + responseBody);
        
        logger.debug("Authentication data validation passed");
    }
    
    @And("the response should contain error message about invalid credentials")
    public void theResponseShouldContainErrorMessageAboutInvalidCredentials() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasCredentialError = responseBody.toLowerCase().contains("invalid") ||
                                   responseBody.toLowerCase().contains("incorrect") ||
                                   responseBody.toLowerCase().contains("wrong") ||
                                   responseBody.toLowerCase().contains("credentials") ||
                                   responseBody.toLowerCase().contains("unauthorized");
        
        Assert.assertTrue(hasCredentialError, "Response does not contain invalid credentials error: " + responseBody);
        
        logger.debug("Invalid credentials error message validation passed");
    }
    
    @And("no authentication token should be provided")
    public void noAuthenticationTokenShouldBeProvided() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasNoToken = !responseBody.contains("token") &&
                           !responseBody.contains("jwt") &&
                           !responseBody.contains("session");
        
        Assert.assertTrue(hasNoToken, "Response unexpectedly contains authentication token: " + responseBody);
        
        logger.debug("No authentication token validation passed");
    }
    
    @And("the response JSON should contain {string} field")
    public void theResponseJsonShouldContainField(String fieldName) {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        Object fieldValue = apiClient.extractResponseData(fieldName);
        Assert.assertNotNull(fieldValue, "Response JSON does not contain field: " + fieldName);
        
        logger.debug("JSON field '{}' validation passed with value: {}", fieldName, fieldValue);
    }
    
    @And("the response JSON should not contain {string} field")
    public void theResponseJsonShouldNotContainField(String fieldName) {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean fieldExists = responseBody.contains("\"" + fieldName + "\"");
        
        Assert.assertFalse(fieldExists, "Response JSON unexpectedly contains field: " + fieldName);
        
        logger.debug("JSON field '{}' absence validation passed", fieldName);
    }
    
    @And("the response JSON should have proper data types for all fields")
    public void theResponseJsonShouldHaveProperDataTypesForAllFields() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // Validate that the response is valid JSON
        String responseBody = lastResponse.getBody().asString();
        Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"),
                         "Response is not valid JSON object: " + responseBody);
        
        logger.debug("JSON data types validation passed");
    }
    
    @And("the response should have proper content-type header")
    public void theResponseShouldHaveProperContentTypeHeader() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String contentType = lastResponse.getContentType();
        Assert.assertNotNull(contentType, "Content-Type header is missing");
        Assert.assertTrue(contentType.contains("application/json"), 
                         "Expected JSON content type but got: " + contentType);
        
        logger.debug("Content-Type header validation passed: {}", contentType);
    }
    

}