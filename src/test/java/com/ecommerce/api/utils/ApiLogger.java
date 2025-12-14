package com.ecommerce.api.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Specialized logger for API interactions with detailed request/response logging
 * Provides structured logging for HTTP requests, responses, and error conditions
 */
public class ApiLogger {
    
    private static final Logger httpLogger = LoggerFactory.getLogger("HTTP_INTERACTIONS");
    private static final Logger errorLogger = LoggerFactory.getLogger("API_ERRORS");
    private static final DateTimeFormatter timestampFormatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    
    /**
     * Log HTTP request details
     * @param method HTTP method (GET, POST, etc.)
     * @param endpoint API endpoint
     * @param headers Request headers
     * @param requestBody Request body (can be null)
     */
    public static void logRequest(String method, String endpoint, Map<String, String> headers, String requestBody) {
        String correlationId = CorrelationIdManager.getCorrelationId();
        String timestamp = timestampFormatter.format(Instant.now());
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== HTTP REQUEST ===");
        logMessage.append("\nTimestamp: ").append(timestamp);
        logMessage.append("\nCorrelation ID: ").append(correlationId != null ? correlationId : "N/A");
        logMessage.append("\nMethod: ").append(method);
        logMessage.append("\nEndpoint: ").append(endpoint);
        
        if (headers != null && !headers.isEmpty()) {
            logMessage.append("\nHeaders:");
            headers.forEach((key, value) -> {
                // Mask sensitive headers
                String maskedValue = maskSensitiveData(key, value);
                logMessage.append("\n  ").append(key).append(": ").append(maskedValue);
            });
        }
        
        if (requestBody != null && !requestBody.trim().isEmpty()) {
            logMessage.append("\nRequest Body: ").append(maskSensitiveJsonData(requestBody));
        }
        
        logMessage.append("\n==================");
        
        httpLogger.info(logMessage.toString());
    }
    
    /**
     * Log HTTP response details
     * @param response REST Assured response object
     * @param endpoint API endpoint
     */
    public static void logResponse(Response response, String endpoint) {
        if (response == null) {
            errorLogger.error("Cannot log response - response object is null for endpoint: {}", endpoint);
            return;
        }
        
        String correlationId = CorrelationIdManager.getCorrelationId();
        String timestamp = timestampFormatter.format(Instant.now());
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== HTTP RESPONSE ===");
        logMessage.append("\nTimestamp: ").append(timestamp);
        logMessage.append("\nCorrelation ID: ").append(correlationId != null ? correlationId : "N/A");
        logMessage.append("\nEndpoint: ").append(endpoint);
        logMessage.append("\nStatus Code: ").append(response.getStatusCode());
        logMessage.append("\nStatus Line: ").append(response.getStatusLine());
        logMessage.append("\nResponse Time: ").append(response.getTime()).append("ms");
        
        // Log response headers
        if (response.getHeaders() != null) {
            logMessage.append("\nResponse Headers:");
            response.getHeaders().forEach(header -> 
                logMessage.append("\n  ").append(header.getName()).append(": ").append(header.getValue())
            );
        }
        
        // Log response body
        try {
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                logMessage.append("\nResponse Body: ").append(maskSensitiveJsonData(responseBody));
            } else {
                logMessage.append("\nResponse Body: [Empty]");
            }
        } catch (Exception e) {
            logMessage.append("\nResponse Body: [Failed to read - ").append(e.getMessage()).append("]");
        }
        
        logMessage.append("\n====================");
        
        httpLogger.info(logMessage.toString());
        
        // Record performance metrics
        PerformanceMetrics.getInstance().recordApiRequest(
            endpoint, 
            response.getTime(), 
            response.getStatusCode() >= 200 && response.getStatusCode() < 300
        );
    }
    
    /**
     * Log API error with detailed context
     * @param endpoint API endpoint where error occurred
     * @param error Exception or error object
     * @param additionalContext Additional context information
     */
    public static void logError(String endpoint, Throwable error, String additionalContext) {
        String correlationId = CorrelationIdManager.getCorrelationId();
        String testScenario = CorrelationIdManager.getTestScenario();
        String timestamp = timestampFormatter.format(Instant.now());
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== API ERROR ===");
        logMessage.append("\nTimestamp: ").append(timestamp);
        logMessage.append("\nCorrelation ID: ").append(correlationId != null ? correlationId : "N/A");
        logMessage.append("\nTest Scenario: ").append(testScenario != null ? testScenario : "N/A");
        logMessage.append("\nEndpoint: ").append(endpoint);
        logMessage.append("\nError Type: ").append(error.getClass().getSimpleName());
        logMessage.append("\nError Message: ").append(error.getMessage());
        
        if (additionalContext != null && !additionalContext.trim().isEmpty()) {
            logMessage.append("\nAdditional Context: ").append(additionalContext);
        }
        
        logMessage.append("\nStack Trace:");
        for (StackTraceElement element : error.getStackTrace()) {
            logMessage.append("\n  at ").append(element.toString());
        }
        
        logMessage.append("\n================");
        
        errorLogger.error(logMessage.toString());
    }
    
    /**
     * Log API error with response details
     * @param endpoint API endpoint where error occurred
     * @param response Response object (may contain error details)
     * @param error Exception that occurred
     */
    public static void logErrorWithResponse(String endpoint, Response response, Throwable error) {
        String correlationId = CorrelationIdManager.getCorrelationId();
        String testScenario = CorrelationIdManager.getTestScenario();
        String timestamp = timestampFormatter.format(Instant.now());
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== API ERROR WITH RESPONSE ===");
        logMessage.append("\nTimestamp: ").append(timestamp);
        logMessage.append("\nCorrelation ID: ").append(correlationId != null ? correlationId : "N/A");
        logMessage.append("\nTest Scenario: ").append(testScenario != null ? testScenario : "N/A");
        logMessage.append("\nEndpoint: ").append(endpoint);
        logMessage.append("\nError Type: ").append(error.getClass().getSimpleName());
        logMessage.append("\nError Message: ").append(error.getMessage());
        
        if (response != null) {
            logMessage.append("\nResponse Status: ").append(response.getStatusCode());
            logMessage.append("\nResponse Time: ").append(response.getTime()).append("ms");
            
            try {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    logMessage.append("\nError Response Body: ").append(maskSensitiveJsonData(responseBody));
                }
            } catch (Exception e) {
                logMessage.append("\nError Response Body: [Failed to read - ").append(e.getMessage()).append("]");
            }
        }
        
        logMessage.append("\nStack Trace:");
        for (StackTraceElement element : error.getStackTrace()) {
            logMessage.append("\n  at ").append(element.toString());
        }
        
        logMessage.append("\n==============================");
        
        errorLogger.error(logMessage.toString());
    }
    
    /**
     * Log test scenario start
     * @param scenarioName Name of the test scenario
     * @param description Description of what the scenario tests
     */
    public static void logScenarioStart(String scenarioName, String description) {
        String correlationId = CorrelationIdManager.setupTestContext(scenarioName, "SCENARIO");
        
        httpLogger.info("\n=== TEST SCENARIO START ===\nScenario: {}\nDescription: {}\nCorrelation ID: {}\n==========================", 
                       scenarioName, description, correlationId);
    }
    
    /**
     * Log test scenario end
     * @param scenarioName Name of the test scenario
     * @param passed Whether the scenario passed
     * @param duration Duration in milliseconds
     */
    public static void logScenarioEnd(String scenarioName, boolean passed, long duration) {
        String correlationId = CorrelationIdManager.getCorrelationId();
        String status = passed ? "PASSED" : "FAILED";
        
        httpLogger.info("\n=== TEST SCENARIO END ===\nScenario: {}\nStatus: {}\nDuration: {}ms\nCorrelation ID: {}\n========================", 
                       scenarioName, status, duration, correlationId);
        
        // Record test result in metrics
        PerformanceMetrics.getInstance().recordTestResult(scenarioName, passed);
        
        // Clear scenario context
        CorrelationIdManager.clearTestScenario();
    }
    
    /**
     * Log validation result
     * @param validationType Type of validation (status code, JSON, etc.)
     * @param expected Expected value
     * @param actual Actual value
     * @param passed Whether validation passed
     */
    public static void logValidation(String validationType, Object expected, Object actual, boolean passed) {
        String correlationId = CorrelationIdManager.getCorrelationId();
        String status = passed ? "PASSED" : "FAILED";
        
        httpLogger.info("Validation {} - Type: {}, Expected: {}, Actual: {}, Correlation ID: {}", 
                       status, validationType, expected, actual, correlationId);
    }
    
    /**
     * Mask sensitive data in headers
     * @param headerName Header name
     * @param headerValue Header value
     * @return Masked value if sensitive, original value otherwise
     */
    private static String maskSensitiveData(String headerName, String headerValue) {
        if (headerName == null || headerValue == null) {
            return headerValue;
        }
        
        String lowerHeaderName = headerName.toLowerCase();
        if (lowerHeaderName.contains("authorization") || 
            lowerHeaderName.contains("token") || 
            lowerHeaderName.contains("password") ||
            lowerHeaderName.contains("secret")) {
            return "[MASKED]";
        }
        
        return headerValue;
    }
    
    /**
     * Mask sensitive data in JSON strings
     * @param jsonData JSON string
     * @return JSON string with sensitive fields masked
     */
    private static String maskSensitiveJsonData(String jsonData) {
        if (jsonData == null) {
            return null;
        }
        
        // Simple regex-based masking for common sensitive fields
        String masked = jsonData;
        masked = masked.replaceAll("(\"password\"\\s*:\\s*\")([^\"]*)(\")", "$1[MASKED]$3");
        masked = masked.replaceAll("(\"token\"\\s*:\\s*\")([^\"]*)(\")", "$1[MASKED]$3");
        masked = masked.replaceAll("(\"secret\"\\s*:\\s*\")([^\"]*)(\")", "$1[MASKED]$3");
        masked = masked.replaceAll("(\"authorization\"\\s*:\\s*\")([^\"]*)(\")", "$1[MASKED]$3");
        
        return masked;
    }
}