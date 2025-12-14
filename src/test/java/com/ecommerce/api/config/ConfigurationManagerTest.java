package com.ecommerce.api.config;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for ConfigurationManager
 * Validates configuration loading and property access functionality
 */
public class ConfigurationManagerTest {
    
    private ConfigurationManager configManager;
    
    @BeforeMethod
    public void setUp() {
        configManager = ConfigurationManager.getInstance();
    }
    
    @Test
    public void testSingletonInstance() {
        ConfigurationManager instance1 = ConfigurationManager.getInstance();
        ConfigurationManager instance2 = ConfigurationManager.getInstance();
        
        Assert.assertSame(instance1, instance2, "ConfigurationManager should be singleton");
    }
    
    @Test
    public void testDefaultEnvironmentLoading() {
        // Should load dev environment by default
        Assert.assertEquals(configManager.getCurrentEnvironment(), "dev", 
                          "Default environment should be 'dev'");
        
        // Verify base URL is loaded
        String baseUrl = configManager.getBaseUrl();
        Assert.assertNotNull(baseUrl, "Base URL should not be null");
        Assert.assertEquals(baseUrl, "http://localhost:8000", 
                          "Base URL should match dev environment configuration");
    }
    
    @Test
    public void testPropertyAccess() {
        // Test string property
        String baseUrl = configManager.getProperty("base.url");
        Assert.assertNotNull(baseUrl, "Base URL property should not be null");
        
        // Test integer property
        Integer timeout = configManager.getIntProperty("api.timeout");
        Assert.assertNotNull(timeout, "API timeout should not be null");
        Assert.assertEquals(timeout, Integer.valueOf(30000), "API timeout should be 30000");
        
        // Test boolean property
        Boolean cleanup = configManager.getBooleanProperty("test.data.cleanup");
        Assert.assertNotNull(cleanup, "Test data cleanup should not be null");
        Assert.assertTrue(cleanup, "Test data cleanup should be true");
    }
    
    @Test
    public void testPropertyWithDefaultValue() {
        // Test existing property with default
        String baseUrl = configManager.getProperty("base.url", "http://default.com");
        Assert.assertEquals(baseUrl, "http://localhost:8000", 
                          "Should return actual value, not default");
        
        // Test non-existing property with default
        String nonExisting = configManager.getProperty("non.existing.property", "default_value");
        Assert.assertEquals(nonExisting, "default_value", 
                          "Should return default value for non-existing property");
    }
    
    @Test
    public void testConvenienceMethods() {
        // Test convenience methods
        Assert.assertNotNull(configManager.getBaseUrl(), "Base URL should not be null");
        Assert.assertNotNull(configManager.getApiTimeout(), "API timeout should not be null");
        Assert.assertNotNull(configManager.getApiRetryCount(), "API retry count should not be null");
        Assert.assertNotNull(configManager.getApiRetryDelay(), "API retry delay should not be null");
        Assert.assertNotNull(configManager.getLoggingLevel(), "Logging level should not be null");
        Assert.assertNotNull(configManager.isTestDataCleanupEnabled(), "Test data cleanup should not be null");
        Assert.assertNotNull(configManager.getTestDataPrefix(), "Test data prefix should not be null");
    }
    
    @Test
    public void testEnvironmentValidation() {
        // Test valid environments
        try {
            configManager.setEnvironment("test");
            Assert.assertEquals(configManager.getCurrentEnvironment(), "test");
            
            configManager.setEnvironment("staging");
            Assert.assertEquals(configManager.getCurrentEnvironment(), "staging");
            
            configManager.setEnvironment("dev");
            Assert.assertEquals(configManager.getCurrentEnvironment(), "dev");
        } catch (Exception e) {
            Assert.fail("Valid environments should not throw exceptions: " + e.getMessage());
        }
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidEnvironment() {
        configManager.setEnvironment("invalid_env");
    }
    
    @Test
    public void testConfigurationHelper() {
        // Test static helper methods
        Assert.assertNotNull(ConfigurationHelper.getBaseUrl(), "Helper base URL should not be null");
        Assert.assertNotNull(ConfigurationHelper.getApiTimeout(), "Helper API timeout should not be null");
        Assert.assertNotNull(ConfigurationHelper.getCurrentEnvironment(), "Helper current environment should not be null");
        
        // Test property access through helper
        String property = ConfigurationHelper.getProperty("base.url");
        Assert.assertNotNull(property, "Helper property access should work");
    }
    
    @Test
    public void testApiEndpoints() {
        // Test API endpoint methods
        Assert.assertNotNull(ApiEndpoints.getSignupEndpoint(), "Signup endpoint should not be null");
        Assert.assertNotNull(ApiEndpoints.getLoginEndpoint(), "Login endpoint should not be null");
        Assert.assertNotNull(ApiEndpoints.getProductsEndpoint(), "Products endpoint should not be null");
        Assert.assertNotNull(ApiEndpoints.getProductByIdEndpoint(), "Product by ID endpoint should not be null");
        
        // Test full URL methods
        Assert.assertNotNull(ApiEndpoints.getFullSignupUrl(), "Full signup URL should not be null");
        Assert.assertNotNull(ApiEndpoints.getFullLoginUrl(), "Full login URL should not be null");
        Assert.assertNotNull(ApiEndpoints.getFullProductsUrl(), "Full products URL should not be null");
        
        // Test product by ID with parameter
        String productUrl = ApiEndpoints.getFullProductByIdUrl("123");
        Assert.assertTrue(productUrl.contains("123"), "Product URL should contain the product ID");
    }
}