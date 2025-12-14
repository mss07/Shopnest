package com.ecommerce.api.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * Utility class for configuring logging levels and behavior at runtime
 * Provides methods to adjust logging configuration based on test requirements
 */
public class LoggingConfiguration {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingConfiguration.class);
    
    /**
     * Set logging level for HTTP interactions
     * @param level Logging level (DEBUG, INFO, WARN, ERROR)
     */
    public static void setHttpLoggingLevel(String level) {
        setLoggerLevel("HTTP_INTERACTIONS", level);
        setLoggerLevel("com.ecommerce.api.client", level);
        logger.info("HTTP logging level set to: {}", level);
    }
    
    /**
     * Set logging level for API errors
     * @param level Logging level (DEBUG, INFO, WARN, ERROR)
     */
    public static void setErrorLoggingLevel(String level) {
        setLoggerLevel("API_ERRORS", level);
        logger.info("Error logging level set to: {}", level);
    }
    
    /**
     * Set logging level for performance metrics
     * @param level Logging level (DEBUG, INFO, WARN, ERROR)
     */
    public static void setMetricsLoggingLevel(String level) {
        setLoggerLevel("com.ecommerce.api.utils.PerformanceMetrics", level);
        logger.info("Metrics logging level set to: {}", level);
    }
    
    /**
     * Enable verbose logging for debugging
     * Sets all loggers to DEBUG level
     */
    public static void enableVerboseLogging() {
        setHttpLoggingLevel("DEBUG");
        setErrorLoggingLevel("DEBUG");
        setMetricsLoggingLevel("DEBUG");
        setLoggerLevel("com.ecommerce.api.stepdefinitions", "DEBUG");
        setLoggerLevel("com.ecommerce.api.utils", "DEBUG");
        logger.info("Verbose logging enabled - all loggers set to DEBUG");
    }
    
    /**
     * Enable quiet logging for CI/CD environments
     * Sets most loggers to WARN level to reduce noise
     */
    public static void enableQuietLogging() {
        setHttpLoggingLevel("WARN");
        setErrorLoggingLevel("ERROR");
        setMetricsLoggingLevel("INFO");
        setLoggerLevel("com.ecommerce.api.stepdefinitions", "WARN");
        setLoggerLevel("com.ecommerce.api.utils", "WARN");
        setLoggerLevel("io.restassured", "ERROR");
        setLoggerLevel("io.cucumber", "WARN");
        logger.info("Quiet logging enabled - reduced verbosity for CI/CD");
    }
    
    /**
     * Reset logging to default levels
     */
    public static void resetToDefaultLogging() {
        setHttpLoggingLevel("INFO");
        setErrorLoggingLevel("ERROR");
        setMetricsLoggingLevel("INFO");
        setLoggerLevel("com.ecommerce.api.stepdefinitions", "DEBUG");
        setLoggerLevel("com.ecommerce.api.utils", "INFO");
        setLoggerLevel("io.restassured", "WARN");
        setLoggerLevel("io.cucumber", "INFO");
        logger.info("Logging reset to default levels");
    }
    
    /**
     * Configure logging based on environment
     * @param environment Environment name (dev, test, staging, prod)
     */
    public static void configureForEnvironment(String environment) {
        if (environment == null) {
            environment = "dev";
        }
        
        switch (environment.toLowerCase()) {
            case "dev":
            case "development":
                enableVerboseLogging();
                logger.info("Logging configured for development environment");
                break;
                
            case "test":
            case "testing":
                resetToDefaultLogging();
                logger.info("Logging configured for test environment");
                break;
                
            case "staging":
                setHttpLoggingLevel("INFO");
                setErrorLoggingLevel("ERROR");
                setMetricsLoggingLevel("INFO");
                setLoggerLevel("io.restassured", "WARN");
                logger.info("Logging configured for staging environment");
                break;
                
            case "prod":
            case "production":
                enableQuietLogging();
                logger.info("Logging configured for production environment");
                break;
                
            default:
                resetToDefaultLogging();
                logger.warn("Unknown environment '{}', using default logging configuration", environment);
                break;
        }
    }
    
    /**
     * Set logging level for a specific logger
     * @param loggerName Name of the logger
     * @param level Logging level as string
     */
    private static void setLoggerLevel(String loggerName, String level) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger targetLogger = loggerContext.getLogger(loggerName);
            
            Level logbackLevel = Level.toLevel(level.toUpperCase());
            targetLogger.setLevel(logbackLevel);
            
            logger.debug("Set logger '{}' to level '{}'", loggerName, level);
            
        } catch (Exception e) {
            logger.error("Failed to set logging level for logger '{}' to '{}'", loggerName, level, e);
        }
    }
    
    /**
     * Get current logging level for a logger
     * @param loggerName Name of the logger
     * @return Current logging level as string
     */
    public static String getLoggerLevel(String loggerName) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger targetLogger = loggerContext.getLogger(loggerName);
            
            Level level = targetLogger.getLevel();
            return level != null ? level.toString() : "INHERITED";
            
        } catch (Exception e) {
            logger.error("Failed to get logging level for logger '{}'", loggerName, e);
            return "UNKNOWN";
        }
    }
    
    /**
     * Log current configuration for debugging
     */
    public static void logCurrentConfiguration() {
        logger.info("=== Current Logging Configuration ===");
        logger.info("HTTP Interactions: {}", getLoggerLevel("HTTP_INTERACTIONS"));
        logger.info("API Errors: {}", getLoggerLevel("API_ERRORS"));
        logger.info("Performance Metrics: {}", getLoggerLevel("com.ecommerce.api.utils.PerformanceMetrics"));
        logger.info("API Client: {}", getLoggerLevel("com.ecommerce.api.client"));
        logger.info("Step Definitions: {}", getLoggerLevel("com.ecommerce.api.stepdefinitions"));
        logger.info("REST Assured: {}", getLoggerLevel("io.restassured"));
        logger.info("Cucumber: {}", getLoggerLevel("io.cucumber"));
        logger.info("=====================================");
    }
}