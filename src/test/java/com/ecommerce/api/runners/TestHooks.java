package com.ecommerce.api.runners;

import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.context.TestContext;
import com.ecommerce.api.utils.ApiLogger;
import com.ecommerce.api.utils.CorrelationIdManager;
import com.ecommerce.api.utils.PerformanceMetrics;
import com.ecommerce.api.utils.TestIsolationManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

/**
 * Cucumber hooks for managing test lifecycle, setup, and cleanup.
 * Handles scenario-level setup, teardown, and reporting integration.
 * Integrates with logging and performance monitoring systems.
 */
public class TestHooks {
    
    private static final Logger logger = LoggerFactory.getLogger(TestHooks.class);
    private final ConfigurationManager configManager;
    private final TestIsolationManager isolationManager;
    private final PerformanceMetrics performanceMetrics;
    
    // Thread-local storage for scenario timing
    private static final ThreadLocal<Instant> scenarioStartTime = new ThreadLocal<>();
    
    public TestHooks() {
        this.configManager = ConfigurationManager.getInstance();
        this.isolationManager = TestIsolationManager.getInstance();
        this.performanceMetrics = PerformanceMetrics.getInstance();
    }
    
    /**
     * Executes before each scenario.
     * Sets up test environment and initializes test data.
     * 
     * @param scenario Current scenario being executed
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        // Record scenario start time
        scenarioStartTime.set(Instant.now());
        
        // Set up logging context with correlation ID
        String correlationId = CorrelationIdManager.generateCorrelationId();
        CorrelationIdManager.setTestScenario(scenario.getName());
        
        // Log scenario start with enhanced logging
        ApiLogger.logScenarioStart(scenario.getName(), "Scenario execution with tags: " + scenario.getSourceTagNames());
        
        logger.info("Starting scenario: {} with correlation ID: {}", scenario.getName(), correlationId);
        logger.debug("Scenario tags: {}", scenario.getSourceTagNames());
        
        try {
            // Start a new test session for isolation
            String sessionId = UUID.randomUUID().toString();
            isolationManager.startTestSession(sessionId, scenario.getName());
            
            // Log scenario start
            logger.info("Scenario '{}' setup completed with session: {}", scenario.getName(), sessionId);
            scenario.log("Correlation ID: " + correlationId);
            scenario.log("Session ID: " + sessionId);
            
        } catch (Exception e) {
            ApiLogger.logError("SCENARIO_SETUP", e, "Failed to setup scenario: " + scenario.getName());
            logger.error("Failed to setup scenario: {}", scenario.getName(), e);
            scenario.log("Setup failed: " + e.getMessage());
            throw new RuntimeException("Scenario setup failed", e);
        }
    }
    
    /**
     * Executes after each scenario.
     * Performs cleanup and captures test results for reporting.
     * 
     * @param scenario Current scenario that was executed
     */
    @After
    public void afterScenario(Scenario scenario) {
        // Calculate scenario execution time
        Instant startTime = scenarioStartTime.get();
        long duration = 0;
        if (startTime != null) {
            duration = java.time.Duration.between(startTime, Instant.now()).toMillis();
            scenarioStartTime.remove();
        }
        
        boolean passed = !scenario.isFailed();
        
        logger.info("Completing scenario: {} - Status: {} - Duration: {}ms", 
                   scenario.getName(), scenario.getStatus(), duration);
        
        try {
            // Log scenario end with enhanced logging
            ApiLogger.logScenarioEnd(scenario.getName(), passed, duration);
            
            // Capture scenario results
            if (scenario.isFailed()) {
                logger.error("Scenario '{}' failed after {}ms", scenario.getName(), duration);
                scenario.log("Scenario failed - Duration: " + duration + "ms - Check logs for details");
                
                // Log failure details if available
                if (scenario.getStatus() != null) {
                    scenario.log("Failure status: " + scenario.getStatus());
                }
            } else {
                logger.info("Scenario '{}' passed in {}ms", scenario.getName(), duration);
                scenario.log("Scenario passed - Duration: " + duration + "ms");
            }
            
            // End current test session and cleanup
            TestIsolationManager.CleanupResult result = isolationManager.endCurrentTestSession();
            logger.debug("Cleanup result: {}", result);
            
            logger.info("Scenario '{}' cleanup completed", scenario.getName());
            
        } catch (Exception e) {
            ApiLogger.logError("SCENARIO_CLEANUP", e, "Failed to cleanup scenario: " + scenario.getName());
            logger.error("Failed to cleanup scenario: {}", scenario.getName(), e);
            scenario.log("Cleanup failed: " + e.getMessage());
        } finally {
            // Clear logging context
            CorrelationIdManager.clearContext();
        }
    }
    
    /**
     * Hook for API-specific scenarios.
     * Provides additional setup for REST API testing scenarios.
     * 
     * @param scenario Current scenario being executed
     */
    @Before("@api")
    public void beforeApiScenario(Scenario scenario) {
        logger.debug("Setting up API-specific configuration for scenario: {}", scenario.getName());
        
        // Log API base URL for debugging
        String baseUrl = configManager.getBaseUrl();
        logger.info("API Base URL: {}", baseUrl);
        scenario.log("Testing against API: " + baseUrl);
    }
    
    /**
     * Hook for smoke test scenarios.
     * Provides additional logging for critical test scenarios.
     * 
     * @param scenario Current scenario being executed
     */
    @Before("@smoke")
    public void beforeSmokeTest(Scenario scenario) {
        logger.info("Executing SMOKE TEST: {}", scenario.getName());
        scenario.log("SMOKE TEST - Critical functionality validation");
    }
    
    /**
     * Hook for regression test scenarios.
     * Provides additional setup for comprehensive test scenarios.
     * 
     * @param scenario Current scenario being executed
     */
    @Before("@regression")
    public void beforeRegressionTest(Scenario scenario) {
        logger.info("Executing REGRESSION TEST: {}", scenario.getName());
        scenario.log("REGRESSION TEST - Comprehensive functionality validation");
    }
}