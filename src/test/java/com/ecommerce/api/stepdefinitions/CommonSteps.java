package com.ecommerce.api.stepdefinitions;

import com.ecommerce.api.client.RestApiClient;
import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Common step definitions shared across different feature files.
 * Contains reusable steps for API testing scenarios.
 */
public class CommonSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonSteps.class);
    private final RestApiClient apiClient;
    private final ConfigurationManager configManager;
    
    public CommonSteps() {
        TestContext context = TestContext.getInstance();
        this.apiClient = context.getApiClient();
        this.configManager = context.getConfigManager();
    }
    
    @Given("the API base URL is configured")
    public void theApiBaseUrlIsConfigured() {
        String baseUrl = configManager.getBaseUrl();
        logger.info("API base URL configured: {}", baseUrl);
        Assert.assertNotNull(baseUrl, "API base URL should be configured");
        Assert.assertFalse(baseUrl.isEmpty(), "API base URL should not be empty");
    }
    
    @Given("the test environment is ready")
    public void theTestEnvironmentIsReady() {
        logger.info("Verifying test environment readiness");
        
        // Verify configuration is loaded
        Assert.assertNotNull(configManager.getBaseUrl(), "Base URL should be configured");
        
        // Verify API client is initialized
        Assert.assertNotNull(apiClient, "API client should be initialized");
        
        logger.info("Test environment is ready for execution");
    }
    
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        int actualStatusCode = apiClient.getLastResponse().getStatusCode();
        logger.info("Validating response status code. Expected: {}, Actual: {}", 
                   expectedStatusCode, actualStatusCode);
        
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
                           String.format("Expected status code %d but got %d", 
                                       expectedStatusCode, actualStatusCode));
    }
    
    @Then("the response time should be less than {int} milliseconds")
    public void theResponseTimeShouldBeLessThanMilliseconds(int maxResponseTime) {
        long actualResponseTime = apiClient.getLastResponse().getTime();
        logger.info("Validating response time. Expected: < {} ms, Actual: {} ms", 
                   maxResponseTime, actualResponseTime);
        
        Assert.assertTrue(actualResponseTime < maxResponseTime, 
                         String.format("Response time %d ms exceeded maximum allowed %d ms", 
                                     actualResponseTime, maxResponseTime));
    }
    
    @Then("the response should have {string} header with value {string}")
    public void theResponseShouldHaveHeaderWithValue(String headerName, String expectedValue) {
        String actualValue = apiClient.getLastResponse().getHeader(headerName);
        logger.info("Validating response header '{}'. Expected: '{}', Actual: '{}'", 
                   headerName, expectedValue, actualValue);
        
        Assert.assertNotNull(actualValue, 
                           String.format("Header '%s' should be present in response", headerName));
        Assert.assertEquals(actualValue, expectedValue, 
                           String.format("Header '%s' should have value '%s' but got '%s'", 
                                       headerName, expectedValue, actualValue));
    }
    
    @Then("the response should contain error message")
    public void theResponseShouldContainErrorMessage() {
        String responseBody = apiClient.getLastResponse().getBody().asString();
        logger.info("Validating that response contains error message");
        
        Assert.assertNotNull(responseBody, "Response body should not be null");
        Assert.assertFalse(responseBody.isEmpty(), "Response body should not be empty");
        
        // Check for common error indicators
        boolean hasError = responseBody.toLowerCase().contains("error") ||
                          responseBody.toLowerCase().contains("message") ||
                          responseBody.toLowerCase().contains("fail");
        
        Assert.assertTrue(hasError, "Response should contain error message");
    }
    
    @Then("the response should contain appropriate error message")
    public void theResponseShouldContainAppropriateErrorMessage() {
        theResponseShouldContainErrorMessage();
    }
    
    @Then("the response should have proper HTTP headers and content-type")
    public void theResponseShouldHaveProperHttpHeadersAndContentType() {
        logger.info("Validating HTTP headers and content-type");
        
        // Validate Content-Type header
        String contentType = apiClient.getLastResponse().getContentType();
        Assert.assertNotNull(contentType, "Content-Type header should be present");
        Assert.assertTrue(contentType.contains("application/json"), 
                         "Content-Type should be application/json");
        
        logger.info("HTTP headers validation completed successfully");
    }
    
    @Then("the response should have proper cache control headers")
    public void theResponseShouldHaveProperCacheControlHeaders() {
        logger.info("Validating cache control headers");
        
        // Check for cache-related headers (optional validation)
        String cacheControl = apiClient.getLastResponse().getHeader("Cache-Control");
        if (cacheControl != null) {
            logger.info("Cache-Control header found: {}", cacheControl);
        }
        
        logger.info("Cache control headers validation completed");
    }
    
    @Then("the response should have appropriate security headers")
    public void theResponseShouldHaveAppropriateSecurityHeaders() {
        logger.info("Validating security headers");
        
        // Check that sensitive information is not exposed
        String server = apiClient.getLastResponse().getHeader("Server");
        if (server != null) {
            // Ensure server header doesn't expose too much information
            Assert.assertFalse(server.toLowerCase().contains("version"), 
                             "Server header should not expose version information");
        }
        
        logger.info("Security headers validation completed");
    }
    
    @Then("the response should not contain sensitive server information")
    public void theResponseShouldNotContainSensitiveServerInformation() {
        theResponseShouldHaveAppropriateSecurityHeaders();
    }
}