package com.ecommerce.api.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages test execution lifecycle including setup, cleanup, and reporting.
 * Handles report directory creation and post-execution cleanup.
 */
public class TestExecutionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionManager.class);
    private static final String REPORTS_DIR = "target/cucumber-reports";
    private static final String SCREENSHOTS_DIR = "target/cucumber-reports/screenshots";
    
    /**
     * Sets up test execution environment before test suite starts.
     * Creates necessary directories for reports and screenshots.
     */
    @BeforeSuite
    public void setupTestExecution() {
        logger.info("Setting up test execution environment...");
        
        try {
            // Clean up old reports
            ReportManager.cleanupOldReports();
            
            // Create reports directories
            createDirectoryIfNotExists(REPORTS_DIR);
            createDirectoryIfNotExists(SCREENSHOTS_DIR);
            createDirectoryIfNotExists(REPORTS_DIR + "/html");
            createDirectoryIfNotExists(REPORTS_DIR + "/extent");
            
            // Log execution start time
            String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.info("Test execution started at: {}", startTime);
            
            // Set system properties for reporting
            System.setProperty("cucumber.publish.quiet", "true");
            System.setProperty("cucumber.junit-platform.naming-strategy", "long");
            
        } catch (Exception e) {
            logger.error("Failed to setup test execution environment", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }
    
    /**
     * Cleans up test execution environment after test suite completes.
     * Generates summary reports and performs cleanup tasks.
     */
    @AfterSuite
    public void cleanupTestExecution() {
        logger.info("Cleaning up test execution environment...");
        
        try {
            // Log execution end time
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.info("Test execution completed at: {}", endTime);
            
            // Generate execution summary and CI/CD results
            ReportManager.generateExecutionSummary();
            ReportManager.generateCiCdResults();
            
            // Validate report generation
            boolean reportsValid = ReportManager.validateReportGeneration();
            if (!reportsValid) {
                logger.warn("Some reports may not have been generated correctly");
            }
            
            // Log report locations
            logReportLocations();
            
        } catch (Exception e) {
            logger.error("Failed to cleanup test execution environment", e);
        }
    }
    
    /**
     * Creates a directory if it doesn't exist.
     * 
     * @param directoryPath Path of the directory to create
     * @throws IOException If directory creation fails
     */
    private void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.debug("Created directory: {}", directoryPath);
        }
    }
    
    /**
     * Logs the locations of generated reports for easy access.
     */
    private void logReportLocations() {
        logger.info("=== Test Reports Generated ===");
        
        File htmlReport = new File(REPORTS_DIR + "/html/index.html");
        if (htmlReport.exists()) {
            logger.info("HTML Report: {}", htmlReport.getAbsolutePath());
        }
        
        File jsonReport = new File(REPORTS_DIR + "/cucumber.json");
        if (jsonReport.exists()) {
            logger.info("JSON Report: {}", jsonReport.getAbsolutePath());
        }
        
        File junitReport = new File(REPORTS_DIR + "/cucumber.xml");
        if (junitReport.exists()) {
            logger.info("JUnit XML Report: {}", junitReport.getAbsolutePath());
        }
        
        File extentReport = new File(REPORTS_DIR + "/extent/ExtentReport.html");
        if (extentReport.exists()) {
            logger.info("Extent Report: {}", extentReport.getAbsolutePath());
        }
        
        logger.info("==============================");
    }
}