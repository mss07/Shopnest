package com.ecommerce.api.config;

/**
 * API Endpoints configuration class
 * Provides centralized access to all API endpoint paths
 */
public class ApiEndpoints {
    
    private static final ConfigurationManager configManager = ConfigurationManager.getInstance();
    
    // Endpoint configuration keys
    private static final String SIGNUP_ENDPOINT = "api.endpoints.signup";
    private static final String LOGIN_ENDPOINT = "api.endpoints.login";
    private static final String PRODUCTS_ENDPOINT = "api.endpoints.products";
    private static final String PRODUCT_BY_ID_ENDPOINT = "api.endpoints.product.by.id";
    
    /**
     * Private constructor to prevent instantiation
     */
    private ApiEndpoints() {
        // Utility class
    }
    
    /**
     * Get signup endpoint path
     * @return Signup endpoint path
     */
    public static String getSignupEndpoint() {
        return configManager.getProperty(SIGNUP_ENDPOINT, "/signup");
    }
    
    /**
     * Get login endpoint path
     * @return Login endpoint path
     */
    public static String getLoginEndpoint() {
        return configManager.getProperty(LOGIN_ENDPOINT, "/login");
    }
    
    /**
     * Get products list endpoint path
     * @return Products endpoint path
     */
    public static String getProductsEndpoint() {
        return configManager.getProperty(PRODUCTS_ENDPOINT, "/products");
    }
    
    /**
     * Get product by ID endpoint path template
     * @return Product by ID endpoint path template
     */
    public static String getProductByIdEndpoint() {
        return configManager.getProperty(PRODUCT_BY_ID_ENDPOINT, "/product/{id}");
    }
    
    /**
     * Get product by ID endpoint path with actual ID
     * @param productId Product ID to substitute
     * @return Product by ID endpoint path with actual ID
     */
    public static String getProductByIdEndpoint(String productId) {
        return getProductByIdEndpoint().replace("{id}", productId);
    }
    
    /**
     * Get full URL for signup endpoint
     * @return Full signup URL
     */
    public static String getFullSignupUrl() {
        return configManager.getBaseUrl() + getSignupEndpoint();
    }
    
    /**
     * Get full URL for login endpoint
     * @return Full login URL
     */
    public static String getFullLoginUrl() {
        return configManager.getBaseUrl() + getLoginEndpoint();
    }
    
    /**
     * Get full URL for products endpoint
     * @return Full products URL
     */
    public static String getFullProductsUrl() {
        return configManager.getBaseUrl() + getProductsEndpoint();
    }
    
    /**
     * Get full URL for product by ID endpoint
     * @param productId Product ID
     * @return Full product by ID URL
     */
    public static String getFullProductByIdUrl(String productId) {
        return configManager.getBaseUrl() + getProductByIdEndpoint(productId);
    }
}