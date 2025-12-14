package com.ecommerce.api.client;

import com.ecommerce.api.config.ConfigurationManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for RestApiClient
 * Tests core functionality of REST API client operations
 */
public class RestApiClientTest {
    
    private RestApiClient restApiClient;
    private ConfigurationManager configManager;
    
    @BeforeClass
    public void setUp() {
        configManager = ConfigurationManager.getInstance();
        restApiClient = new RestApiClient();
    }
    
    @Test
    public void testClientInitialization() {
        Assert.assertNotNull(restApiClient, "RestApiClient should be initialized");
        Assert.assertNotNull(configManager.getBaseUrl(), "Base URL should be configured");
    }
    
    @Test
    public void testGetLastResponseInitiallyNull() {
        restApiClient.reset();
        Assert.assertNull(restApiClient.getLastResponse(), "Last response should be null initially");
    }
    
    @Test
    public void testValidateStatusCodeWithoutResponse() {
        restApiClient.reset();
        boolean result = restApiClient.validateStatusCode(200);
        Assert.assertFalse(result, "Status code validation should fail when no response is available");
    }
    
    @Test
    public void testValidateJsonResponseWithoutResponse() {
        restApiClient.reset();
        boolean result = restApiClient.validateJsonResponse("$.message", "success");
        Assert.assertFalse(result, "JSON validation should fail when no response is available");
    }
    
    @Test
    public void testExtractResponseDataWithoutResponse() {
        restApiClient.reset();
        Object result = restApiClient.extractResponseData("$.data");
        Assert.assertNull(result, "Data extraction should return null when no response is available");
    }
    
    @Test
    public void testGetResponseBodyWithoutResponse() {
        restApiClient.reset();
        String result = restApiClient.getResponseBody();
        Assert.assertNull(result, "Response body should be null when no response is available");
    }
    
    @Test
    public void testIsResponseSuccessfulWithoutResponse() {
        restApiClient.reset();
        boolean result = restApiClient.isResponseSuccessful();
        Assert.assertFalse(result, "Response success check should fail when no response is available");
    }
    
    @Test
    public void testGetErrorDetailsWithoutResponse() {
        restApiClient.reset();
        String result = restApiClient.getErrorDetails();
        Assert.assertEquals(result, "No response available", "Error details should indicate no response available");
    }
    
    @Test
    public void testValidateContentTypeWithoutResponse() {
        restApiClient.reset();
        boolean result = restApiClient.validateContentType("application/json");
        Assert.assertFalse(result, "Content type validation should fail when no response is available");
    }
    
    @Test
    public void testValidateResponseTimeWithoutResponse() {
        restApiClient.reset();
        boolean result = restApiClient.validateResponseTime(1000);
        Assert.assertFalse(result, "Response time validation should fail when no response is available");
    }
    
    @Test
    public void testGetResponseBodyAsWithoutResponse() {
        restApiClient.reset();
        Map result = restApiClient.getResponseBodyAs(Map.class);
        Assert.assertNull(result, "Response body deserialization should return null when no response is available");
    }
    
    @Test
    public void testExtractResponseDataWithTypeWithoutResponse() {
        restApiClient.reset();
        String result = restApiClient.extractResponseData("$.message", String.class);
        Assert.assertNull(result, "Typed data extraction should return null when no response is available");
    }
    
    @Test
    public void testResetFunctionality() {
        // This test verifies that reset clears the last response
        restApiClient.reset();
        Assert.assertNull(restApiClient.getLastResponse(), "Reset should clear last response");
    }
}