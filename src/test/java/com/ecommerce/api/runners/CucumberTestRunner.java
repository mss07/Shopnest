package com.ecommerce.api.runners;

import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.utils.LoggingConfiguration;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

/**
 * Cucumber Test Runner class that integrates with TestNG for executing BDD scenarios.
 * Configures feature file discovery, reporting, and parallel execution settings.
 * 
 * Requirements: 1.3, 1.4, 5.1, 5.5
 */
@CucumberOptions(
    // Feature files location
    features = "src/test/resources/features",
    
    // Step definitions and hooks packages
    glue = {"com.ecommerce.api.stepdefinitions", "com.ecommerce.api.runners"},
    
    // Report generation plugins
    plugin = {
        "pretty",
        "html:target/cucumber-reports/html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    
    // Test execution settings
    monochrome = true,
    publish = false,
    
    // Tag-based execution (can be overridden via system properties)
    tags = "@smoke or @regression"
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Configure logging and environment settings before test suite execution
     */
    @BeforeSuite
    public void setupTestSuite() {
        // Get environment configuration
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        String environment = configManager.getCurrentEnvironment();
        
        // Configure logging based on environment
        LoggingConfiguration.configureForEnvironment(environment);
        
        // Log current configuration for debugging
        LoggingConfiguration.logCurrentConfiguration();
        
        System.out.println("=== API Test Suite Configuration ===");
        System.out.println("Environment: " + environment);
        System.out.println("Base URL: " + configManager.getBaseUrl());
        System.out.println("Logging configured for: " + environment);
        System.out.println("====================================");
    }
    
    /**
     * Enables parallel execution of scenarios.
     * Each scenario will run in a separate thread for faster execution.
     * 
     * @return DataProvider configuration for parallel execution
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}