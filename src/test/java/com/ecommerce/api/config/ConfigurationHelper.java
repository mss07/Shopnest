package com.ecommerce.api.config;

/**
 * Configuration Helper class providing static utility methods
 * for easy access to configuration values throughout the framework
 */
public class ConfigurationHelper {
    
    private static final ConfigurationManager configManager = ConfigurationManager.getInstance();
    
    /**
     * Private constructor to prevent instantiation
     */
    private ConfigurationHelper() {
        // Utility class
    }
    
    /**
     * Get base URL for API endpoints
     * @return Base URL
     */
    public static String getBaseUrl() {
        return configManager.getBaseUrl();
    }
    
    /**
     * Get API timeout in milliseconds
     * @return API timeout
     */
    public static Integer getApiTimeout() {
        return configManager.getApiTimeout();
    }
    
    /**
     * Get API retry count
     * @return Retry count
     */
    public static Integer getApiRetryCount() {
        return configManager.getApiRetryCount();
    }
    
    /**
     * Get API retry delay in milliseconds
     * @return Retry delay
     */
    public static Integer getApiRetryDelay() {
        return configManager.getApiRetryDelay();
    }
    
    /**
     * Get logging level
     * @return Logging level
     */
    public static String getLoggingLevel() {
        return configManager.getLoggingLevel();
    }
    
    /**
     * Check if test data cleanup is enabled
     * @return true if cleanup is enabled, false otherwise
     */
    public static Boolean isTestDataCleanupEnabled() {
        return configManager.isTestDataCleanupEnabled();
    }
    
    /**
     * Get test data prefix
     * @return Test data prefix
     */
    public static String getTestDataPrefix() {
        return configManager.getTestDataPrefix();
    }
    
    /**
     * Get current environment name
     * @return Current environment
     */
    public static String getCurrentEnvironment() {
        return configManager.getCurrentEnvironment();
    }
    
    /**
     * Get configuration property as String
     * @param key Property key
     * @return Property value or null if not found
     */
    public static String getProperty(String key) {
        return configManager.getProperty(key);
    }
    
    /**
     * Get configuration property as String with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return configManager.getProperty(key, defaultValue);
    }
    
    /**
     * Get configuration property as Integer
     * @param key Property key
     * @return Property value as Integer or null if not found or invalid
     */
    public static Integer getIntProperty(String key) {
        return configManager.getIntProperty(key);
    }
    
    /**
     * Get configuration property as Integer with default value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value as Integer or default value
     */
    public static Integer getIntProperty(String key, Integer defaultValue) {
        return configManager.getIntProperty(key, defaultValue);
    }
    
    /**
     * Get configuration property as Boolean
     * @param key Property key
     * @return Property value as Boolean or null if not found
     */
    public static Boolean getBooleanProperty(String key) {
        return configManager.getBooleanProperty(key);
    }
    
    /**
     * Get configuration property as Boolean with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as Boolean or default value
     */
    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        return configManager.getBooleanProperty(key, defaultValue);
    }
}