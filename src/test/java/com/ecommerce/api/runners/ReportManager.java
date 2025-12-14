package com.ecommerce.api.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Manages test report generation and CI/CD integration.
 * Handles multiple report formats and provides utilities for report processing.
 */
public class ReportManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);
    private static final String REPORTS_BASE_DIR = "target/cucumber-reports";
    
    /**
     * Generates a summary report with test execution metadata.
     * Creates a properties file with execution details for CI/CD consumption.
     */
    public static void generateExecutionSummary() {
        logger.info("Generating test execution summary...");
        
        try {
            Properties summary = new Properties();
            
            // Add execution metadata
            summary.setProperty("execution.timestamp", 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            summary.setProperty("execution.environment", 
                System.getProperty("environment", "dev"));
            summary.setProperty("execution.baseUrl", 
                System.getProperty("base.url", "http://localhost:8000"));
            
            // Add report file locations
            summary.setProperty("reports.html", REPORTS_BASE_DIR + "/html/index.html");
            summary.setProperty("reports.json", REPORTS_BASE_DIR + "/cucumber.json");
            summary.setProperty("reports.junit", REPORTS_BASE_DIR + "/cucumber.xml");
            summary.setProperty("reports.extent", REPORTS_BASE_DIR + "/extent/ExtentReport.html");
            
            // Check report file existence
            summary.setProperty("reports.html.exists", 
                String.valueOf(Files.exists(Paths.get(REPORTS_BASE_DIR + "/html/index.html"))));
            summary.setProperty("reports.json.exists", 
                String.valueOf(Files.exists(Paths.get(REPORTS_BASE_DIR + "/cucumber.json"))));
            summary.setProperty("reports.junit.exists", 
                String.valueOf(Files.exists(Paths.get(REPORTS_BASE_DIR + "/cucumber.xml"))));
            
            // Write summary file
            File summaryFile = new File(REPORTS_BASE_DIR + "/execution-summary.properties");
            try (FileWriter writer = new FileWriter(summaryFile)) {
                summary.store(writer, "Test Execution Summary");
            }
            
            logger.info("Execution summary generated: {}", summaryFile.getAbsolutePath());
            
        } catch (IOException e) {
            logger.error("Failed to generate execution summary", e);
        }
    }
    
    /**
     * Creates a CI/CD friendly test results file.
     * Generates a simple text file with pass/fail status for pipeline consumption.
     */
    public static void generateCiCdResults() {
        logger.info("Generating CI/CD results file...");
        
        try {
            File junitFile = new File(REPORTS_BASE_DIR + "/cucumber.xml");
            boolean testsExist = junitFile.exists();
            
            File cicdFile = new File(REPORTS_BASE_DIR + "/cicd-results.txt");
            try (FileWriter writer = new FileWriter(cicdFile)) {
                writer.write("TEST_EXECUTION_COMPLETED=true\n");
                writer.write("JUNIT_REPORT_EXISTS=" + testsExist + "\n");
                writer.write("EXECUTION_TIMESTAMP=" + 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
                
                if (testsExist) {
                    writer.write("JUNIT_REPORT_PATH=" + junitFile.getAbsolutePath() + "\n");
                }
            }
            
            logger.info("CI/CD results file generated: {}", cicdFile.getAbsolutePath());
            
        } catch (IOException e) {
            logger.error("Failed to generate CI/CD results file", e);
        }
    }
    
    /**
     * Validates that all expected report files were generated.
     * 
     * @return true if all expected reports exist, false otherwise
     */
    public static boolean validateReportGeneration() {
        logger.info("Validating report generation...");
        
        String[] expectedReports = {
            REPORTS_BASE_DIR + "/cucumber.json",
            REPORTS_BASE_DIR + "/cucumber.xml"
        };
        
        boolean allReportsExist = true;
        for (String reportPath : expectedReports) {
            Path path = Paths.get(reportPath);
            if (!Files.exists(path)) {
                logger.warn("Expected report not found: {}", reportPath);
                allReportsExist = false;
            } else {
                logger.debug("Report found: {}", reportPath);
            }
        }
        
        if (allReportsExist) {
            logger.info("All expected reports generated successfully");
        } else {
            logger.warn("Some expected reports are missing");
        }
        
        return allReportsExist;
    }
    
    /**
     * Cleans up old report files before new test execution.
     * Removes previous test artifacts to ensure clean reporting.
     */
    public static void cleanupOldReports() {
        logger.info("Cleaning up old report files...");
        
        try {
            Path reportsDir = Paths.get(REPORTS_BASE_DIR);
            if (Files.exists(reportsDir)) {
                Files.walk(reportsDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            logger.debug("Deleted old report file: {}", path);
                        } catch (IOException e) {
                            logger.warn("Failed to delete old report file: {}", path, e);
                        }
                    });
            }
            
            logger.info("Old report cleanup completed");
            
        } catch (IOException e) {
            logger.error("Failed to cleanup old reports", e);
        }
    }
}