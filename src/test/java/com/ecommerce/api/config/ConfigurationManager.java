package com.ecommerce.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for API Automation Testing Framework
 * Handles loading and managing environment-specific configuration properties
 * Supports dev, test, and staging environments
 */
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private Properties properties;
    private String currentEnvironment;
    
    // Configuration keys
    public static final String BASE_URL = "base.url";
    public static final String API_TIMEOUT = "api.timeout";
    public static final String API_RETRY_COUNT = "api.retry.count";
    public static final String API_RETRY_DELAY = "api.retry.delay";
    public static final String LOGGING_LEVEL = "logging.level";
    public static final String LOGGING_PATTERN = "logging.pattern";
    public static final String TEST_DATA_CLEANUP = "test.data.cleanup";
    public static final String TEST_DATA_PREFIX = "test.data.prefix";
    
    // Default environment
    private static final String DEFAULT_ENVIRONMENT = "dev";
    
    /**
     * Private constructor to implement singleton pattern
     */
    private ConfigurationManager() {
        loadConfiguration();
    }
    
    /**
     * Get singleton instance of ConfigurationManager
     * @return ConfigurationManager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    /**
     * Load configuration properties based on environment
     */
    private void loadConfiguration() {
        // Get environment from system property or use default
        currentEnvironment = System.getProperty("environment", DEFAULT_ENVIRONMENT);
        
        // Validate environment
        if (!isValidEnvironment(currentEnvironment)) {
            logger.warn("Invalid environment '{}' specified. Using default environment '{}'", 
                       currentEnvironment, DEFAULT_ENVIRONMENT);
            currentEnvironment = DEFAULT_ENVIRONMENT;
        }
        
        String configFileName = String.format("config/application-%s.properties", currentEnvironment);
        
        properties = new Properties();
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + configFileName);
            }
            
            properties.load(inputStream);
            logger.info("Successfully loaded configuration for environment: {}", currentEnvironment);
            
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", configFileName, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    /**
     * Check if the environment is valid
     * @param environment Environment name to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEnvironment(String environment) {
        return "dev".equals(environment) || "test".equals(environment) || "staging".equals(environment);
    }
    
    /**
     * Get configuration property as String
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Configuration property '{}' not found for environment '{}'", key, currentEnvironment);
        }
        return value;
    }
    
    /**
     * Get configuration property as String with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        if (properties.getProperty(key) == null) {
            logger.debug("Using default value '{}' for property '{}'", defaultValue, key);
        }
        return value;
    }
    
    /**
     * Get configuration property as Integer
     * @param key Property key
     * @return Property value as Integer or null if not found or invalid
     */
    public Integer getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid integer value '{}' for property '{}'", value, key);
            return null;
        }
    }
    
    /**
     * Get configuration property as Integer with default value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value as Integer or default value
     */
    public Integer getIntProperty(String key, Integer defaultValue) {
        Integer value = getIntProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get configuration property as Boolean
     * @param key Property key
     * @return Property value as Boolean or null if not found
     */
    public Boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get configuration property as Boolean with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as Boolean or default value
     */
    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        Boolean value = getBooleanProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get base URL for API endpoints
     * @return Base URL
     */
    public String getBaseUrl() {
        return getProperty(BASE_URL);
    }
    
    /**
     * Get API timeout in milliseconds
     * @return API timeout
     */
    public Integer getApiTimeout() {
        return getIntProperty(API_TIMEOUT, 30000);
    }
    
    /**
     * Get API retry count
     * @return Retry count
     */
    public Integer getApiRetryCount() {
        return getIntProperty(API_RETRY_COUNT, 3);
    }
    
    /**
     * Get API retry delay in milliseconds
     * @return Retry delay
     */
    public Integer getApiRetryDelay() {
        return getIntProperty(API_RETRY_DELAY, 1000);
    }
    
    /**
     * Get logging level
     * @return Logging level
     */
    public String getLoggingLevel() {
        return getProperty(LOGGING_LEVEL, "INFO");
    }
    
    /**
     * Get logging pattern
     * @return Logging pattern
     */
    public String getLoggingPattern() {
        return getProperty(LOGGING_PATTERN);
    }
    
    /**
     * Check if test data cleanup is enabled
     * @return true if cleanup is enabled, false otherwise
     */
    public Boolean isTestDataCleanupEnabled() {
        return getBooleanProperty(TEST_DATA_CLEANUP, true);
    }
    
    /**
     * Get test data prefix
     * @return Test data prefix
     */
    public String getTestDataPrefix() {
        return getProperty(TEST_DATA_PREFIX, "test_");
    }
    
    /**
     * Get current environment name
     * @return Current environment
     */
    public String getCurrentEnvironment() {
        return currentEnvironment;
    }
    
    /**
     * Reload configuration (useful for testing or environment changes)
     */
    public void reloadConfiguration() {
        logger.info("Reloading configuration for environment: {}", currentEnvironment);
        loadConfiguration();
    }
    
    /**
     * Set environment and reload configuration
     * @param environment Environment name (dev, test, staging)
     */
    public void setEnvironment(String environment) {
        if (!isValidEnvironment(environment)) {
            throw new IllegalArgumentException("Invalid environment: " + environment + 
                                             ". Valid environments are: dev, test, staging");
        }
        
        System.setProperty("environment", environment);
        this.currentEnvironment = environment;
        loadConfiguration();
        logger.info("Environment changed to: {}", environment);
    }
    
    /**
     * Get all properties for debugging purposes
     * @return Properties object
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }
}