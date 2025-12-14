package com.ecommerce.api.runners;

import org.testng.annotations.Test;

/**
 * Main test suite class that extends TestExecutionManager for proper lifecycle management.
 * Provides setup and cleanup functionality for the entire test execution.
 */
public class ApiTestSuite extends TestExecutionManager {
    
    /**
     * Executes the Cucumber test runner within the managed test lifecycle.
     * This ensures proper setup and cleanup around test execution.
     */
    @Test
    public void runCucumberTests() {
        // The actual test execution is handled by CucumberTestRunner
        // This method exists to trigger the TestExecutionManager lifecycle
        CucumberTestRunner runner = new CucumberTestRunner();
        Object[][] scenarios = runner.scenarios();
        
        // Log the number of scenarios to be executed
        if (scenarios != null) {
            System.out.println("Total scenarios to execute: " + scenarios.length);
        }
    }
}