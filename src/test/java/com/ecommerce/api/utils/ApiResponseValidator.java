package com.ecommerce.api.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for validating API responses
 * Provides comprehensive validation methods for HTTP responses, status codes, headers, and JSON content
 */
public class ApiResponseValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiResponseValidator.class);
    
    // Common HTTP status codes
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    
    // Common content types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    
    /**
     * Validate HTTP status code
     * @param response API response
     * @param expectedStatusCode Expected status code
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateStatusCode(Response response, int expectedStatusCode) {
        try {
            int actualStatusCode = response.getStatusCode();
            boolean isValid = actualStatusCode == expectedStatusCode;
            
            String message = isValid ? 
                String.format("Status code validation passed: %d", actualStatusCode) :
                String.format("Status code validation failed. Expected: %d, Actual: %d", 
                             expectedStatusCode, actualStatusCode);
            
            logger.info("Status code validation - Expected: {}, Actual: {}, Result: {}", 
                       expectedStatusCode, actualStatusCode, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "status_code");
        } catch (Exception e) {
            String errorMessage = "Failed to validate status code: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "status_code");
        }
    }
    
    /**
     * Validate that status code is in acceptable range
     * @param response API response
     * @param acceptableStatusCodes List of acceptable status codes
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateStatusCodeInRange(Response response, List<Integer> acceptableStatusCodes) {
        try {
            int actualStatusCode = response.getStatusCode();
            boolean isValid = acceptableStatusCodes.contains(actualStatusCode);
            
            String message = isValid ?
                String.format("Status code %d is in acceptable range: %s", actualStatusCode, acceptableStatusCodes) :
                String.format("Status code %d is not in acceptable range: %s", actualStatusCode, acceptableStatusCodes);
            
            logger.info("Status code range validation - Actual: {}, Acceptable: {}, Result: {}", 
                       actualStatusCode, acceptableStatusCodes, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "status_code_range");
        } catch (Exception e) {
            String errorMessage = "Failed to validate status code range: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "status_code_range");
        }
    }
    
    /**
     * Validate response content type
     * @param response API response
     * @param expectedContentType Expected content type
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateContentType(Response response, String expectedContentType) {
        try {
            String actualContentType = response.getContentType();
            boolean isValid = actualContentType != null && actualContentType.contains(expectedContentType);
            
            String message = isValid ?
                String.format("Content type validation passed: %s", actualContentType) :
                String.format("Content type validation failed. Expected: %s, Actual: %s", 
                             expectedContentType, actualContentType);
            
            logger.info("Content type validation - Expected: {}, Actual: {}, Result: {}", 
                       expectedContentType, actualContentType, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "content_type");
        } catch (Exception e) {
            String errorMessage = "Failed to validate content type: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "content_type");
        }
    }
    
    /**
     * Validate response header
     * @param response API response
     * @param headerName Header name to validate
     * @param expectedValue Expected header value
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateHeader(Response response, String headerName, String expectedValue) {
        try {
            String actualValue = response.getHeader(headerName);
            boolean isValid = expectedValue.equals(actualValue);
            
            String message = isValid ?
                String.format("Header validation passed - %s: %s", headerName, actualValue) :
                String.format("Header validation failed - %s. Expected: %s, Actual: %s", 
                             headerName, expectedValue, actualValue);
            
            logger.info("Header validation - {}: Expected: {}, Actual: {}, Result: {}", 
                       headerName, expectedValue, actualValue, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "header_" + headerName);
        } catch (Exception e) {
            String errorMessage = "Failed to validate header " + headerName + ": " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "header_" + headerName);
        }
    }
    
    /**
     * Validate that response header exists
     * @param response API response
     * @param headerName Header name to check
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateHeaderExists(Response response, String headerName) {
        try {
            String headerValue = response.getHeader(headerName);
            boolean isValid = headerValue != null && !headerValue.trim().isEmpty();
            
            String message = isValid ?
                String.format("Header exists validation passed - %s: %s", headerName, headerValue) :
                String.format("Header exists validation failed - %s header is missing or empty", headerName);
            
            logger.info("Header exists validation - {}: Present: {}, Result: {}", 
                       headerName, isValid, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "header_exists_" + headerName);
        } catch (Exception e) {
            String errorMessage = "Failed to validate header existence " + headerName + ": " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "header_exists_" + headerName);
        }
    }
    
    /**
     * Validate JSON response structure
     * @param response API response
     * @param requiredFields List of required JSON field paths
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateJsonStructure(Response response, List<String> requiredFields) {
        try {
            String responseBody = response.getBody().asString();
            
            if (!JsonUtils.isValidJson(responseBody)) {
                return new ValidationResult(false, "Response body is not valid JSON", "json_structure");
            }
            
            boolean isValid = JsonUtils.validateSchema(responseBody, requiredFields);
            
            String message = isValid ?
                "JSON structure validation passed - all required fields present" :
                "JSON structure validation failed - missing required fields";
            
            logger.info("JSON structure validation - Required fields: {}, Result: {}", 
                       requiredFields, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "json_structure");
        } catch (Exception e) {
            String errorMessage = "Failed to validate JSON structure: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "json_structure");
        }
    }
    
    /**
     * Validate JSON field value
     * @param response API response
     * @param fieldPath JSON field path
     * @param expectedValue Expected field value
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateJsonFieldValue(Response response, String fieldPath, Object expectedValue) {
        try {
            String responseBody = response.getBody().asString();
            String actualValue = JsonUtils.extractValue(responseBody, fieldPath);
            
            boolean isValid = (expectedValue == null && actualValue == null) ||
                             (expectedValue != null && expectedValue.toString().equals(actualValue));
            
            String message = isValid ?
                String.format("JSON field validation passed - %s: %s", fieldPath, actualValue) :
                String.format("JSON field validation failed - %s. Expected: %s, Actual: %s", 
                             fieldPath, expectedValue, actualValue);
            
            logger.info("JSON field validation - {}: Expected: {}, Actual: {}, Result: {}", 
                       fieldPath, expectedValue, actualValue, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "json_field_" + fieldPath);
        } catch (Exception e) {
            String errorMessage = "Failed to validate JSON field " + fieldPath + ": " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "json_field_" + fieldPath);
        }
    }
    
    /**
     * Validate that JSON field exists
     * @param response API response
     * @param fieldPath JSON field path
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateJsonFieldExists(Response response, String fieldPath) {
        try {
            String responseBody = response.getBody().asString();
            boolean isValid = JsonUtils.hasPath(responseBody, fieldPath);
            
            String message = isValid ?
                String.format("JSON field exists validation passed - %s", fieldPath) :
                String.format("JSON field exists validation failed - %s not found", fieldPath);
            
            logger.info("JSON field exists validation - {}: Present: {}, Result: {}", 
                       fieldPath, isValid, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "json_field_exists_" + fieldPath);
        } catch (Exception e) {
            String errorMessage = "Failed to validate JSON field existence " + fieldPath + ": " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "json_field_exists_" + fieldPath);
        }
    }
    
    /**
     * Validate response time
     * @param response API response
     * @param maxResponseTimeMs Maximum acceptable response time in milliseconds
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateResponseTime(Response response, long maxResponseTimeMs) {
        try {
            long actualResponseTime = response.getTime();
            boolean isValid = actualResponseTime <= maxResponseTimeMs;
            
            String message = isValid ?
                String.format("Response time validation passed: %d ms (max: %d ms)", 
                             actualResponseTime, maxResponseTimeMs) :
                String.format("Response time validation failed: %d ms exceeds maximum %d ms", 
                             actualResponseTime, maxResponseTimeMs);
            
            logger.info("Response time validation - Actual: {} ms, Max: {} ms, Result: {}", 
                       actualResponseTime, maxResponseTimeMs, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "response_time");
        } catch (Exception e) {
            String errorMessage = "Failed to validate response time: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "response_time");
        }
    }
    
    /**
     * Validate that response body is not empty
     * @param response API response
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateResponseBodyNotEmpty(Response response) {
        try {
            String responseBody = response.getBody().asString();
            boolean isValid = responseBody != null && !responseBody.trim().isEmpty();
            
            String message = isValid ?
                "Response body validation passed - body is not empty" :
                "Response body validation failed - body is empty or null";
            
            logger.info("Response body validation - Empty: {}, Result: {}", 
                       !isValid, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "response_body_not_empty");
        } catch (Exception e) {
            String errorMessage = "Failed to validate response body: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "response_body_not_empty");
        }
    }
    
    /**
     * Validate JSON array size
     * @param response API response
     * @param expectedSize Expected array size
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateJsonArraySize(Response response, int expectedSize) {
        try {
            String responseBody = response.getBody().asString();
            int actualSize = JsonUtils.getSize(responseBody);
            
            boolean isValid = actualSize == expectedSize;
            
            String message = isValid ?
                String.format("JSON array size validation passed: %d", actualSize) :
                String.format("JSON array size validation failed. Expected: %d, Actual: %d", 
                             expectedSize, actualSize);
            
            logger.info("JSON array size validation - Expected: {}, Actual: {}, Result: {}", 
                       expectedSize, actualSize, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "json_array_size");
        } catch (Exception e) {
            String errorMessage = "Failed to validate JSON array size: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "json_array_size");
        }
    }
    
    /**
     * Validate that response contains specific text
     * @param response API response
     * @param expectedText Text that should be present in response
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateResponseContainsText(Response response, String expectedText) {
        try {
            String responseBody = response.getBody().asString();
            boolean isValid = responseBody != null && responseBody.contains(expectedText);
            
            String message = isValid ?
                String.format("Response contains text validation passed: '%s'", expectedText) :
                String.format("Response contains text validation failed: '%s' not found", expectedText);
            
            logger.info("Response contains text validation - Text: '{}', Found: {}, Result: {}", 
                       expectedText, isValid, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "response_contains_text");
        } catch (Exception e) {
            String errorMessage = "Failed to validate response contains text: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "response_contains_text");
        }
    }
    
    /**
     * Validate response against regex pattern
     * @param response API response
     * @param pattern Regex pattern to match
     * @return ValidationResult with validation outcome
     */
    public static ValidationResult validateResponseMatchesPattern(Response response, String pattern) {
        try {
            String responseBody = response.getBody().asString();
            Pattern regexPattern = Pattern.compile(pattern);
            boolean isValid = regexPattern.matcher(responseBody).find();
            
            String message = isValid ?
                String.format("Response pattern validation passed: matches '%s'", pattern) :
                String.format("Response pattern validation failed: does not match '%s'", pattern);
            
            logger.info("Response pattern validation - Pattern: '{}', Matches: {}, Result: {}", 
                       pattern, isValid, isValid ? "PASS" : "FAIL");
            
            return new ValidationResult(isValid, message, "response_pattern");
        } catch (Exception e) {
            String errorMessage = "Failed to validate response pattern: " + e.getMessage();
            logger.error(errorMessage, e);
            return new ValidationResult(false, errorMessage, "response_pattern");
        }
    }
    
    /**
     * Perform comprehensive response validation
     * @param response API response
     * @param validationConfig Configuration for validation checks
     * @return CompositeValidationResult with all validation outcomes
     */
    public static CompositeValidationResult validateResponse(Response response, ValidationConfig validationConfig) {
        CompositeValidationResult compositeResult = new CompositeValidationResult();
        
        try {
            // Status code validation
            if (validationConfig.getExpectedStatusCode() != null) {
                ValidationResult statusResult = validateStatusCode(response, validationConfig.getExpectedStatusCode());
                compositeResult.addResult(statusResult);
            }
            
            // Content type validation
            if (validationConfig.getExpectedContentType() != null) {
                ValidationResult contentTypeResult = validateContentType(response, validationConfig.getExpectedContentType());
                compositeResult.addResult(contentTypeResult);
            }
            
            // Header validations
            if (validationConfig.getExpectedHeaders() != null) {
                for (Map.Entry<String, String> header : validationConfig.getExpectedHeaders().entrySet()) {
                    ValidationResult headerResult = validateHeader(response, header.getKey(), header.getValue());
                    compositeResult.addResult(headerResult);
                }
            }
            
            // JSON structure validation
            if (validationConfig.getRequiredJsonFields() != null) {
                ValidationResult jsonStructureResult = validateJsonStructure(response, validationConfig.getRequiredJsonFields());
                compositeResult.addResult(jsonStructureResult);
            }
            
            // JSON field value validations
            if (validationConfig.getExpectedJsonValues() != null) {
                for (Map.Entry<String, Object> fieldValue : validationConfig.getExpectedJsonValues().entrySet()) {
                    ValidationResult fieldResult = validateJsonFieldValue(response, fieldValue.getKey(), fieldValue.getValue());
                    compositeResult.addResult(fieldResult);
                }
            }
            
            // Response time validation
            if (validationConfig.getMaxResponseTimeMs() != null) {
                ValidationResult responseTimeResult = validateResponseTime(response, validationConfig.getMaxResponseTimeMs());
                compositeResult.addResult(responseTimeResult);
            }
            
            logger.info("Comprehensive validation completed - Total checks: {}, Passed: {}, Failed: {}", 
                       compositeResult.getTotalChecks(), compositeResult.getPassedChecks(), compositeResult.getFailedChecks());
            
        } catch (Exception e) {
            logger.error("Failed to perform comprehensive validation", e);
            compositeResult.addResult(new ValidationResult(false, "Comprehensive validation failed: " + e.getMessage(), "comprehensive"));
        }
        
        return compositeResult;
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        private final String validationType;
        
        public ValidationResult(boolean isValid, String message, String validationType) {
            this.isValid = isValid;
            this.message = message;
            this.validationType = validationType;
        }
        
        public boolean isValid() { return isValid; }
        public String getMessage() { return message; }
        public String getValidationType() { return validationType; }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{type='%s', valid=%s, message='%s'}", 
                                validationType, isValid, message);
        }
    }
    
    /**
     * Composite validation result class
     */
    public static class CompositeValidationResult {
        private final List<ValidationResult> results = new java.util.ArrayList<>();
        
        public void addResult(ValidationResult result) {
            results.add(result);
        }
        
        public List<ValidationResult> getResults() { return results; }
        
        public boolean isAllValid() {
            return results.stream().allMatch(ValidationResult::isValid);
        }
        
        public int getTotalChecks() { return results.size(); }
        
        public int getPassedChecks() {
            return (int) results.stream().filter(ValidationResult::isValid).count();
        }
        
        public int getFailedChecks() {
            return (int) results.stream().filter(r -> !r.isValid()).count();
        }
        
        public List<ValidationResult> getFailedResults() {
            return results.stream().filter(r -> !r.isValid()).collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public String toString() {
            return String.format("CompositeValidationResult{total=%d, passed=%d, failed=%d, allValid=%s}", 
                                getTotalChecks(), getPassedChecks(), getFailedChecks(), isAllValid());
        }
    }
    
    /**
     * Validation configuration class
     */
    public static class ValidationConfig {
        private Integer expectedStatusCode;
        private String expectedContentType;
        private Map<String, String> expectedHeaders;
        private List<String> requiredJsonFields;
        private Map<String, Object> expectedJsonValues;
        private Long maxResponseTimeMs;
        
        // Getters and setters
        public Integer getExpectedStatusCode() { return expectedStatusCode; }
        public ValidationConfig setExpectedStatusCode(Integer expectedStatusCode) { 
            this.expectedStatusCode = expectedStatusCode; return this; 
        }
        
        public String getExpectedContentType() { return expectedContentType; }
        public ValidationConfig setExpectedContentType(String expectedContentType) { 
            this.expectedContentType = expectedContentType; return this; 
        }
        
        public Map<String, String> getExpectedHeaders() { return expectedHeaders; }
        public ValidationConfig setExpectedHeaders(Map<String, String> expectedHeaders) { 
            this.expectedHeaders = expectedHeaders; return this; 
        }
        
        public List<String> getRequiredJsonFields() { return requiredJsonFields; }
        public ValidationConfig setRequiredJsonFields(List<String> requiredJsonFields) { 
            this.requiredJsonFields = requiredJsonFields; return this; 
        }
        
        public Map<String, Object> getExpectedJsonValues() { return expectedJsonValues; }
        public ValidationConfig setExpectedJsonValues(Map<String, Object> expectedJsonValues) { 
            this.expectedJsonValues = expectedJsonValues; return this; 
        }
        
        public Long getMaxResponseTimeMs() { return maxResponseTimeMs; }
        public ValidationConfig setMaxResponseTimeMs(Long maxResponseTimeMs) { 
            this.maxResponseTimeMs = maxResponseTimeMs; return this; 
        }
    }
}