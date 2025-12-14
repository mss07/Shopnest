package com.ecommerce.api.runners;

import com.ecommerce.api.utils.PerformanceMetrics;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test suite level hooks for managing overall test execution lifecycle
 * Handles test suite setup, teardown, and performance metrics collection
 */
public class TestSuiteHooks {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSuiteHooks.class);
    
    /**
     * Executes once before all scenarios in the test suite
     * Sets up test suite level configuration and metrics collection
     */
    @BeforeAll
    public static void beforeTestSuite() {
        logger.info("=== API Test Suite Starting ===");
        
        try {
            // Initialize performance metrics collection
            PerformanceMetrics performanceMetrics = PerformanceMetrics.getInstance();
            performanceMetrics.startTestSuite();
            
            // Log test suite configuration
            logger.info("Performance metrics collection initialized");
            logger.info("Test suite setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to setup test suite", e);
            throw new RuntimeException("Test suite setup failed", e);
        }
    }
    
    /**
     * Executes once after all scenarios in the test suite
     * Performs cleanup and generates final performance reports
     */
    @AfterAll
    public static void afterTestSuite() {
        logger.info("=== API Test Suite Completing ===");
        
        try {
            // Finalize performance metrics collection
            PerformanceMetrics performanceMetrics = PerformanceMetrics.getInstance();
            performanceMetrics.endTestSuite();
            
            // Generate comprehensive metrics summary
            performanceMetrics.logMetricsSummary();
            
            logger.info("Test suite completed successfully");
            logger.info("Performance metrics and reports generated");
            
        } catch (Exception e) {
            logger.error("Failed to complete test suite cleanup", e);
        }
        
        logger.info("=== API Test Suite Finished ===");
    }
}