package com.ecommerce.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * Collects and manages performance metrics for API testing
 * Tracks response times, request counts, and error rates
 */
public class PerformanceMetrics {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMetrics.class);
    private static final PerformanceMetrics instance = new PerformanceMetrics();
    
    // Metrics storage
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> minResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> maxResponseTimes = new ConcurrentHashMap<>();
    
    // Test execution tracking
    private Instant testSuiteStartTime;
    private Instant testSuiteEndTime;
    private final AtomicLong totalTests = new AtomicLong(0);
    private final AtomicLong passedTests = new AtomicLong(0);
    private final AtomicLong failedTests = new AtomicLong(0);
    
    private PerformanceMetrics() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance
     * @return PerformanceMetrics instance
     */
    public static PerformanceMetrics getInstance() {
        return instance;
    }
    
    /**
     * Record the start of test suite execution
     */
    public void startTestSuite() {
        testSuiteStartTime = Instant.now();
        logger.info("Test suite execution started at: {}", testSuiteStartTime);
    }
    
    /**
     * Record the end of test suite execution
     */
    public void endTestSuite() {
        testSuiteEndTime = Instant.now();
        Duration totalDuration = Duration.between(testSuiteStartTime, testSuiteEndTime);
        logger.info("Test suite execution completed at: {}, Total duration: {}ms", 
                   testSuiteEndTime, totalDuration.toMillis());
        logTestSuiteSummary();
    }
    
    /**
     * Record API request metrics
     * @param endpoint API endpoint
     * @param responseTimeMs Response time in milliseconds
     * @param isSuccess Whether the request was successful
     */
    public void recordApiRequest(String endpoint, long responseTimeMs, boolean isSuccess) {
        String key = normalizeEndpoint(endpoint);
        
        // Update request count
        requestCounts.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        
        // Update error count if failed
        if (!isSuccess) {
            errorCounts.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        // Update response time metrics
        totalResponseTimes.computeIfAbsent(key, k -> new AtomicLong(0)).addAndGet(responseTimeMs);
        
        // Update min response time
        minResponseTimes.compute(key, (k, current) -> {
            if (current == null) {
                return new AtomicLong(responseTimeMs);
            } else {
                long currentMin = current.get();
                if (responseTimeMs < currentMin) {
                    current.set(responseTimeMs);
                }
                return current;
            }
        });
        
        // Update max response time
        maxResponseTimes.compute(key, (k, current) -> {
            if (current == null) {
                return new AtomicLong(responseTimeMs);
            } else {
                long currentMax = current.get();
                if (responseTimeMs > currentMax) {
                    current.set(responseTimeMs);
                }
                return current;
            }
        });
        
        logger.debug("Recorded API request metrics - Endpoint: {}, Response Time: {}ms, Success: {}", 
                    endpoint, responseTimeMs, isSuccess);
    }
    
    /**
     * Record test execution result
     * @param testName Name of the test
     * @param passed Whether the test passed
     */
    public void recordTestResult(String testName, boolean passed) {
        totalTests.incrementAndGet();
        if (passed) {
            passedTests.incrementAndGet();
            logger.debug("Test passed: {}", testName);
        } else {
            failedTests.incrementAndGet();
            logger.debug("Test failed: {}", testName);
        }
    }
    
    /**
     * Get average response time for an endpoint
     * @param endpoint API endpoint
     * @return Average response time in milliseconds, or 0 if no data
     */
    public double getAverageResponseTime(String endpoint) {
        String key = normalizeEndpoint(endpoint);
        AtomicLong total = totalResponseTimes.get(key);
        AtomicLong count = requestCounts.get(key);
        
        if (total == null || count == null || count.get() == 0) {
            return 0.0;
        }
        
        return (double) total.get() / count.get();
    }
    
    /**
     * Get error rate for an endpoint
     * @param endpoint API endpoint
     * @return Error rate as percentage (0.0 to 100.0)
     */
    public double getErrorRate(String endpoint) {
        String key = normalizeEndpoint(endpoint);
        AtomicLong errors = errorCounts.get(key);
        AtomicLong total = requestCounts.get(key);
        
        if (errors == null || total == null || total.get() == 0) {
            return 0.0;
        }
        
        return (double) errors.get() / total.get() * 100.0;
    }
    
    /**
     * Get request count for an endpoint
     * @param endpoint API endpoint
     * @return Total number of requests
     */
    public long getRequestCount(String endpoint) {
        String key = normalizeEndpoint(endpoint);
        AtomicLong count = requestCounts.get(key);
        return count != null ? count.get() : 0;
    }
    
    /**
     * Get minimum response time for an endpoint
     * @param endpoint API endpoint
     * @return Minimum response time in milliseconds
     */
    public long getMinResponseTime(String endpoint) {
        String key = normalizeEndpoint(endpoint);
        AtomicLong min = minResponseTimes.get(key);
        return min != null ? min.get() : 0;
    }
    
    /**
     * Get maximum response time for an endpoint
     * @param endpoint API endpoint
     * @return Maximum response time in milliseconds
     */
    public long getMaxResponseTime(String endpoint) {
        String key = normalizeEndpoint(endpoint);
        AtomicLong max = maxResponseTimes.get(key);
        return max != null ? max.get() : 0;
    }
    
    /**
     * Get test suite pass rate
     * @return Pass rate as percentage (0.0 to 100.0)
     */
    public double getTestPassRate() {
        long total = totalTests.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) passedTests.get() / total * 100.0;
    }
    
    /**
     * Get total test suite execution time
     * @return Duration in milliseconds, or 0 if not completed
     */
    public long getTotalExecutionTime() {
        if (testSuiteStartTime == null || testSuiteEndTime == null) {
            return 0;
        }
        return Duration.between(testSuiteStartTime, testSuiteEndTime).toMillis();
    }
    
    /**
     * Log comprehensive metrics summary
     */
    public void logMetricsSummary() {
        logger.info("=== API Performance Metrics Summary ===");
        
        for (String endpoint : requestCounts.keySet()) {
            long requests = getRequestCount(endpoint);
            double avgResponseTime = getAverageResponseTime(endpoint);
            double errorRate = getErrorRate(endpoint);
            long minTime = getMinResponseTime(endpoint);
            long maxTime = getMaxResponseTime(endpoint);
            
            logger.info("Endpoint: {} | Requests: {} | Avg Response: {:.2f}ms | Error Rate: {:.2f}% | Min: {}ms | Max: {}ms",
                       endpoint, requests, avgResponseTime, errorRate, minTime, maxTime);
        }
        
        logTestSuiteSummary();
    }
    
    /**
     * Log test suite execution summary
     */
    private void logTestSuiteSummary() {
        long total = totalTests.get();
        long passed = passedTests.get();
        long failed = failedTests.get();
        double passRate = getTestPassRate();
        long executionTime = getTotalExecutionTime();
        
        logger.info("=== Test Suite Execution Summary ===");
        logger.info("Total Tests: {} | Passed: {} | Failed: {} | Pass Rate: {}% | Execution Time: {}ms",
                   total, passed, failed, String.format("%.2f", passRate), executionTime);
    }
    
    /**
     * Reset all metrics (useful for test isolation)
     */
    public void reset() {
        requestCounts.clear();
        errorCounts.clear();
        totalResponseTimes.clear();
        minResponseTimes.clear();
        maxResponseTimes.clear();
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        testSuiteStartTime = null;
        testSuiteEndTime = null;
        logger.debug("Performance metrics reset");
    }
    
    /**
     * Normalize endpoint for consistent metrics tracking
     * @param endpoint Raw endpoint path
     * @return Normalized endpoint key
     */
    private String normalizeEndpoint(String endpoint) {
        if (endpoint == null) {
            return "unknown";
        }
        
        // Remove query parameters and normalize path
        String normalized = endpoint.split("\\?")[0];
        
        // Replace dynamic path parameters with placeholders
        normalized = normalized.replaceAll("/\\d+", "/{id}");
        normalized = normalized.replaceAll("/[a-f0-9-]{36}", "/{uuid}");
        
        return normalized;
    }
}