package com.ecommerce.api.utils;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Manages correlation IDs for tracking requests across the testing framework
 * Provides thread-safe correlation ID generation and management using SLF4J MDC
 */
public class CorrelationIdManager {
    
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String TEST_SCENARIO_KEY = "testScenario";
    private static final String REQUEST_TYPE_KEY = "requestType";
    
    /**
     * Generate and set a new correlation ID for the current thread
     * @return Generated correlation ID
     */
    public static String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);
        return correlationId;
    }
    
    /**
     * Set a specific correlation ID for the current thread
     * @param correlationId Correlation ID to set
     */
    public static void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }
    
    /**
     * Get the current correlation ID for the thread
     * @return Current correlation ID or null if not set
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }
    
    /**
     * Set the test scenario name for logging context
     * @param scenarioName Name of the test scenario
     */
    public static void setTestScenario(String scenarioName) {
        MDC.put(TEST_SCENARIO_KEY, scenarioName);
    }
    
    /**
     * Get the current test scenario name
     * @return Current test scenario name or null if not set
     */
    public static String getTestScenario() {
        return MDC.get(TEST_SCENARIO_KEY);
    }
    
    /**
     * Set the request type for logging context
     * @param requestType Type of request (GET, POST, etc.)
     */
    public static void setRequestType(String requestType) {
        MDC.put(REQUEST_TYPE_KEY, requestType);
    }
    
    /**
     * Get the current request type
     * @return Current request type or null if not set
     */
    public static String getRequestType() {
        return MDC.get(REQUEST_TYPE_KEY);
    }
    
    /**
     * Clear all MDC context for the current thread
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    /**
     * Clear only the correlation ID from MDC
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }
    
    /**
     * Clear only the test scenario from MDC
     */
    public static void clearTestScenario() {
        MDC.remove(TEST_SCENARIO_KEY);
    }
    
    /**
     * Clear only the request type from MDC
     */
    public static void clearRequestType() {
        MDC.remove(REQUEST_TYPE_KEY);
    }
    
    /**
     * Set up complete logging context for a test scenario
     * @param scenarioName Name of the test scenario
     * @param requestType Type of request being made
     * @return Generated correlation ID
     */
    public static String setupTestContext(String scenarioName, String requestType) {
        String correlationId = generateCorrelationId();
        setTestScenario(scenarioName);
        setRequestType(requestType);
        return correlationId;
    }
}