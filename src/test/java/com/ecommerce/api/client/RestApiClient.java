package com.ecommerce.api.client;

import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.utils.ApiLogger;
import com.ecommerce.api.utils.CorrelationIdManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * REST API Client using REST Assured
 * Provides methods for HTTP requests, response validation, and error handling
 * Supports GET and POST operations with JSON serialization/deserialization
 */
public class RestApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RestApiClient.class);
    private final ConfigurationManager configManager;
    private final ObjectMapper objectMapper;
    private Response lastResponse;
    
    /**
     * Constructor initializes REST Assured configuration
     */
    public RestApiClient() {
        this.configManager = ConfigurationManager.getInstance();
        this.objectMapper = new ObjectMapper();
        configureRestAssured();
    }
    
    /**
     * Configure REST Assured with default settings
     */
    private void configureRestAssured() {
        RestAssured.baseURI = configManager.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Set default timeout
        RestAssured.config = RestAssured.config()
            .httpClient(RestAssured.config().getHttpClientConfig()
                .setParam("http.connection.timeout", configManager.getApiTimeout())
                .setParam("http.socket.timeout", configManager.getApiTimeout()));
        
        logger.info("REST Assured configured with base URI: {}", RestAssured.baseURI);
    }
    
    /**
     * Create base request specification with common headers
     * @return RequestSpecification with default configuration
     */
    private RequestSpecification createBaseRequest() {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("User-Agent", "API-Automation-Testing/1.0")
            .relaxedHTTPSValidation()
            .when();
    }
    
    /**
     * Send GET request to specified endpoint
     * @param endpoint API endpoint path
     * @return Response object
     */
    public Response sendGetRequest(String endpoint) {
        // Set up correlation context
        CorrelationIdManager.setRequestType("GET");
        
        try {
            // Log request details
            ApiLogger.logRequest("GET", endpoint, null, null);
            
            RequestSpecification request = createBaseRequest();
            lastResponse = request.get(endpoint);
            
            // Log response details
            ApiLogger.logResponse(lastResponse, endpoint);
            
            logger.info("GET request completed. Status: {}, Response time: {}ms", 
                       lastResponse.getStatusCode(), lastResponse.getTime());
            
            return lastResponse;
            
        } catch (Exception e) {
            ApiLogger.logError(endpoint, e, "Failed during GET request execution");
            logger.error("Failed to send GET request to endpoint: {}", endpoint, e);
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        } finally {
            CorrelationIdManager.clearRequestType();
        }
    }
    
    /**
     * Send GET request with query parameters
     * @param endpoint API endpoint path
     * @param queryParams Map of query parameters
     * @return Response object
     */
    public Response sendGetRequest(String endpoint, Map<String, Object> queryParams) {
        // Set up correlation context
        CorrelationIdManager.setRequestType("GET");
        
        try {
            // Build full endpoint with query params for logging
            String fullEndpoint = endpoint;
            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder sb = new StringBuilder(endpoint).append("?");
                queryParams.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
                fullEndpoint = sb.substring(0, sb.length() - 1); // Remove trailing &
            }
            
            // Log request details
            ApiLogger.logRequest("GET", fullEndpoint, null, null);
            
            RequestSpecification request = createBaseRequest();
            
            if (queryParams != null && !queryParams.isEmpty()) {
                request.queryParams(queryParams);
            }
            
            lastResponse = request.get(endpoint);
            
            // Log response details
            ApiLogger.logResponse(lastResponse, fullEndpoint);
            
            logger.info("GET request completed. Status: {}, Response time: {}ms", 
                       lastResponse.getStatusCode(), lastResponse.getTime());
            
            return lastResponse;
            
        } catch (Exception e) {
            ApiLogger.logError(endpoint, e, "Failed during GET request with query parameters: " + queryParams);
            logger.error("Failed to send GET request to endpoint: {} with params: {}", endpoint, queryParams, e);
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        } finally {
            CorrelationIdManager.clearRequestType();
        }
    }
    
    /**
     * Send POST request with JSON body
     * @param endpoint API endpoint path
     * @param requestBody Object to be serialized as JSON
     * @return Response object
     */
    public Response sendPostRequest(String endpoint, Object requestBody) {
        // Set up correlation context
        CorrelationIdManager.setRequestType("POST");
        
        try {
            RequestSpecification request = createBaseRequest();
            String jsonBody = null;
            
            if (requestBody != null) {
                jsonBody = objectMapper.writeValueAsString(requestBody);
                request.body(jsonBody);
            }
            
            // Log request details
            ApiLogger.logRequest("POST", endpoint, null, jsonBody);
            
            lastResponse = request.post(endpoint);
            
            // Log response details
            ApiLogger.logResponse(lastResponse, endpoint);
            
            logger.info("POST request completed. Status: {}, Response time: {}ms", 
                       lastResponse.getStatusCode(), lastResponse.getTime());
            
            return lastResponse;
            
        } catch (Exception e) {
            ApiLogger.logError(endpoint, e, "Failed during POST request execution");
            logger.error("Failed to send POST request to endpoint: {}", endpoint, e);
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        } finally {
            CorrelationIdManager.clearRequestType();
        }
    }
    
    /**
     * Send POST request with custom headers
     * @param endpoint API endpoint path
     * @param requestBody Object to be serialized as JSON
     * @param headers Map of custom headers
     * @return Response object
     */
    public Response sendPostRequest(String endpoint, Object requestBody, Map<String, String> headers) {
        // Set up correlation context
        CorrelationIdManager.setRequestType("POST");
        
        try {
            RequestSpecification request = createBaseRequest();
            String jsonBody = null;
            
            if (headers != null && !headers.isEmpty()) {
                request.headers(headers);
            }
            
            if (requestBody != null) {
                jsonBody = objectMapper.writeValueAsString(requestBody);
                request.body(jsonBody);
            }
            
            // Log request details
            ApiLogger.logRequest("POST", endpoint, headers, jsonBody);
            
            lastResponse = request.post(endpoint);
            
            // Log response details
            ApiLogger.logResponse(lastResponse, endpoint);
            
            logger.info("POST request completed. Status: {}, Response time: {}ms", 
                       lastResponse.getStatusCode(), lastResponse.getTime());
            
            return lastResponse;
            
        } catch (Exception e) {
            ApiLogger.logError(endpoint, e, "Failed during POST request with custom headers: " + headers);
            logger.error("Failed to send POST request to endpoint: {} with headers: {}", endpoint, headers, e);
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        } finally {
            CorrelationIdManager.clearRequestType();
        }
    }
    
    /**
     * Validate HTTP status code
     * @param expectedStatusCode Expected HTTP status code
     * @return true if status code matches, false otherwise
     */
    public boolean validateStatusCode(int expectedStatusCode) {
        if (lastResponse == null) {
            logger.error("No response available for status code validation");
            return false;
        }
        
        int actualStatusCode = lastResponse.getStatusCode();
        boolean isValid = actualStatusCode == expectedStatusCode;
        
        // Log validation result
        ApiLogger.logValidation("Status Code", expectedStatusCode, actualStatusCode, isValid);
        
        if (isValid) {
            logger.info("Status code validation passed. Expected: {}, Actual: {}", 
                       expectedStatusCode, actualStatusCode);
        } else {
            logger.error("Status code validation failed. Expected: {}, Actual: {}", 
                        expectedStatusCode, actualStatusCode);
        }
        
        return isValid;
    }
    
    /**
     * Validate JSON response content using JSONPath
     * @param jsonPath JSONPath expression
     * @param expectedValue Expected value
     * @return true if validation passes, false otherwise
     */
    public boolean validateJsonResponse(String jsonPath, Object expectedValue) {
        if (lastResponse == null) {
            logger.error("No response available for JSON validation");
            return false;
        }
        
        try {
            Object actualValue = lastResponse.jsonPath().get(jsonPath);
            boolean isValid = (expectedValue == null && actualValue == null) || 
                             (expectedValue != null && expectedValue.equals(actualValue));
            
            // Log validation result
            ApiLogger.logValidation("JSON Path: " + jsonPath, expectedValue, actualValue, isValid);
            
            if (isValid) {
                logger.info("JSON validation passed for path '{}'. Expected: {}, Actual: {}", 
                           jsonPath, expectedValue, actualValue);
            } else {
                logger.error("JSON validation failed for path '{}'. Expected: {}, Actual: {}", 
                            jsonPath, expectedValue, actualValue);
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Failed to validate JSON path '{}': {}", jsonPath, e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract data from JSON response using JSONPath
     * @param jsonPath JSONPath expression
     * @return Extracted value or null if not found
     */
    public Object extractResponseData(String jsonPath) {
        if (lastResponse == null) {
            logger.error("No response available for data extraction");
            return null;
        }
        
        try {
            Object value = lastResponse.jsonPath().get(jsonPath);
            logger.debug("Extracted value from path '{}': {}", jsonPath, value);
            return value;
            
        } catch (Exception e) {
            logger.error("Failed to extract data from JSON path '{}': {}", jsonPath, e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract data from JSON response and convert to specified type
     * @param jsonPath JSONPath expression
     * @param targetType Target class type
     * @param <T> Type parameter
     * @return Extracted and converted value or null if conversion fails
     */
    public <T> T extractResponseData(String jsonPath, Class<T> targetType) {
        if (lastResponse == null) {
            logger.error("No response available for data extraction");
            return null;
        }
        
        try {
            T value = lastResponse.jsonPath().getObject(jsonPath, targetType);
            logger.debug("Extracted and converted value from path '{}' to type {}: {}", 
                        jsonPath, targetType.getSimpleName(), value);
            return value;
            
        } catch (Exception e) {
            logger.error("Failed to extract and convert data from JSON path '{}' to type {}: {}", 
                        jsonPath, targetType.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate response content type
     * @param expectedContentType Expected content type
     * @return true if content type matches, false otherwise
     */
    public boolean validateContentType(String expectedContentType) {
        if (lastResponse == null) {
            logger.error("No response available for content type validation");
            return false;
        }
        
        String actualContentType = lastResponse.getContentType();
        boolean isValid = actualContentType != null && actualContentType.contains(expectedContentType);
        
        if (isValid) {
            logger.info("Content type validation passed. Expected: {}, Actual: {}", 
                       expectedContentType, actualContentType);
        } else {
            logger.error("Content type validation failed. Expected: {}, Actual: {}", 
                        expectedContentType, actualContentType);
        }
        
        return isValid;
    }
    
    /**
     * Validate response time is within acceptable limit
     * @param maxResponseTimeMs Maximum acceptable response time in milliseconds
     * @return true if response time is acceptable, false otherwise
     */
    public boolean validateResponseTime(long maxResponseTimeMs) {
        if (lastResponse == null) {
            logger.error("No response available for response time validation");
            return false;
        }
        
        long actualResponseTime = lastResponse.getTime();
        boolean isValid = actualResponseTime <= maxResponseTimeMs;
        
        if (isValid) {
            logger.info("Response time validation passed. Expected: <={}ms, Actual: {}ms", 
                       maxResponseTimeMs, actualResponseTime);
        } else {
            logger.error("Response time validation failed. Expected: <={}ms, Actual: {}ms", 
                        maxResponseTimeMs, actualResponseTime);
        }
        
        return isValid;
    }
    
    /**
     * Get the last response object
     * @return Last response or null if no request was made
     */
    public Response getLastResponse() {
        return lastResponse;
    }
    
    /**
     * Get response body as string
     * @return Response body as string or null if no response
     */
    public String getResponseBody() {
        if (lastResponse == null) {
            logger.error("No response available to get body");
            return null;
        }
        
        return lastResponse.getBody().asString();
    }
    
    /**
     * Get response body as object of specified type
     * @param targetType Target class type
     * @param <T> Type parameter
     * @return Deserialized response body or null if conversion fails
     */
    public <T> T getResponseBodyAs(Class<T> targetType) {
        if (lastResponse == null) {
            logger.error("No response available to deserialize");
            return null;
        }
        
        try {
            String responseBody = lastResponse.getBody().asString();
            T result = objectMapper.readValue(responseBody, targetType);
            logger.debug("Successfully deserialized response to type: {}", targetType.getSimpleName());
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to deserialize response to type {}: {}", 
                        targetType.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if last response indicates success (2xx status codes)
     * @return true if response is successful, false otherwise
     */
    public boolean isResponseSuccessful() {
        if (lastResponse == null) {
            logger.error("No response available to check success status");
            return false;
        }
        
        int statusCode = lastResponse.getStatusCode();
        boolean isSuccessful = statusCode >= 200 && statusCode < 300;
        
        logger.debug("Response success check: Status code {}, Successful: {}", statusCode, isSuccessful);
        return isSuccessful;
    }
    
    /**
     * Get error details from response when request fails
     * @return Error details string or null if no error information available
     */
    public String getErrorDetails() {
        if (lastResponse == null) {
            return "No response available";
        }
        
        StringBuilder errorDetails = new StringBuilder();
        errorDetails.append("HTTP Status: ").append(lastResponse.getStatusCode());
        errorDetails.append(", Status Line: ").append(lastResponse.getStatusLine());
        
        try {
            String responseBody = lastResponse.getBody().asString();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                errorDetails.append(", Response Body: ").append(responseBody);
            }
        } catch (Exception e) {
            errorDetails.append(", Failed to read response body: ").append(e.getMessage());
        }
        
        return errorDetails.toString();
    }
    
    /**
     * Reset client state (clears last response)
     */
    public void reset() {
        lastResponse = null;
        logger.debug("REST API client state reset");
    }
}