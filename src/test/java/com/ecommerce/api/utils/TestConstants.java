package com.ecommerce.api.utils;

/**
 * Constants class for shared test values and configuration
 * Provides centralized constants for API testing framework
 */
public final class TestConstants {
    
    // Prevent instantiation
    private TestConstants() {
        throw new UnsupportedOperationException("TestConstants is a utility class and cannot be instantiated");
    }
    
    // ========== API ENDPOINTS ==========
    
    /**
     * User management API endpoints
     */
    public static final class UserEndpoints {
        public static final String SIGNUP = "/signup";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String PROFILE = "/profile";
        public static final String UPDATE_PROFILE = "/profile/update";
        public static final String DELETE_ACCOUNT = "/profile/delete";
    }
    
    /**
     * Product catalog API endpoints
     */
    public static final class ProductEndpoints {
        public static final String PRODUCTS = "/products";
        public static final String PRODUCT_BY_ID = "/product/{id}";
        public static final String PRODUCT_SEARCH = "/products/search";
        public static final String PRODUCT_CATEGORIES = "/products/categories";
        public static final String PRODUCTS_BY_CATEGORY = "/products/category/{category}";
    }
    
    /**
     * Cart and order API endpoints
     */
    public static final class CartEndpoints {
        public static final String CART = "/cart";
        public static final String ADD_TO_CART = "/cart/add";
        public static final String REMOVE_FROM_CART = "/cart/remove";
        public static final String UPDATE_CART = "/cart/update";
        public static final String CLEAR_CART = "/cart/clear";
        public static final String CHECKOUT = "/cart/checkout";
    }
    
    // ========== HTTP STATUS CODES ==========
    
    /**
     * HTTP status codes for API responses
     */
    public static final class StatusCodes {
        // Success codes
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int ACCEPTED = 202;
        public static final int NO_CONTENT = 204;
        
        // Client error codes
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int CONFLICT = 409;
        public static final int UNPROCESSABLE_ENTITY = 422;
        public static final int TOO_MANY_REQUESTS = 429;
        
        // Server error codes
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int BAD_GATEWAY = 502;
        public static final int SERVICE_UNAVAILABLE = 503;
        public static final int GATEWAY_TIMEOUT = 504;
    }
    
    // ========== CONTENT TYPES ==========
    
    /**
     * HTTP content types
     */
    public static final class ContentTypes {
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_XML = "application/xml";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String TEXT_HTML = "text/html";
        public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    }
    
    // ========== HTTP HEADERS ==========
    
    /**
     * HTTP header names
     */
    public static final class Headers {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String AUTHORIZATION = "Authorization";
        public static final String USER_AGENT = "User-Agent";
        public static final String X_CORRELATION_ID = "X-Correlation-ID";
        public static final String X_REQUEST_ID = "X-Request-ID";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String ETAG = "ETag";
        public static final String LAST_MODIFIED = "Last-Modified";
    }
    
    // ========== TEST DATA LIMITS ==========
    
    /**
     * Limits and constraints for test data
     */
    public static final class DataLimits {
        // String length limits
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = 20;
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = 50;
        public static final int MAX_EMAIL_LENGTH = 100;
        public static final int MAX_NAME_LENGTH = 50;
        public static final int MAX_DESCRIPTION_LENGTH = 500;
        
        // Numeric limits
        public static final double MIN_PRICE = 0.01;
        public static final double MAX_PRICE = 99999.99;
        public static final int MIN_QUANTITY = 1;
        public static final int MAX_QUANTITY = 1000;
        public static final int MAX_CART_ITEMS = 50;
        
        // Collection limits
        public static final int MAX_PRODUCTS_PER_PAGE = 100;
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_SEARCH_RESULTS = 500;
    }
    
    // ========== TIMEOUT VALUES ==========
    
    /**
     * Timeout values for API operations (in milliseconds)
     */
    public static final class Timeouts {
        public static final long DEFAULT_REQUEST_TIMEOUT = 30000; // 30 seconds
        public static final long FAST_REQUEST_TIMEOUT = 5000;     // 5 seconds
        public static final long SLOW_REQUEST_TIMEOUT = 60000;    // 60 seconds
        public static final long CONNECTION_TIMEOUT = 10000;      // 10 seconds
        public static final long READ_TIMEOUT = 30000;            // 30 seconds
        
        // Performance thresholds
        public static final long FAST_RESPONSE_THRESHOLD = 1000;  // 1 second
        public static final long ACCEPTABLE_RESPONSE_THRESHOLD = 3000; // 3 seconds
        public static final long SLOW_RESPONSE_THRESHOLD = 5000;  // 5 seconds
    }
    
    // ========== TEST ENVIRONMENT SETTINGS ==========
    
    /**
     * Environment-specific settings
     */
    public static final class Environment {
        public static final String DEV = "dev";
        public static final String TEST = "test";
        public static final String STAGING = "staging";
        public static final String PRODUCTION = "production";
        
        // Default ports
        public static final int DEFAULT_HTTP_PORT = 8000;
        public static final int DEFAULT_HTTPS_PORT = 8443;
    }
    
    // ========== JSON FIELD NAMES ==========
    
    /**
     * Common JSON field names used in API responses
     */
    public static final class JsonFields {
        // User fields
        public static final String USER_ID = "id";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String FIRST_NAME = "firstname";
        public static final String LAST_NAME = "lastname";
        public static final String PHONE = "phone";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";
        
        // Product fields
        public static final String PRODUCT_ID = "id";
        public static final String PRODUCT_TITLE = "title";
        public static final String PRODUCT_DESCRIPTION = "description";
        public static final String PRODUCT_PRICE = "price";
        public static final String PRODUCT_CATEGORY = "category";
        public static final String PRODUCT_IMAGE = "image";
        public static final String PRODUCT_RATING = "rating";
        public static final String PRODUCT_COUNT = "count";
        
        // Response metadata fields
        public static final String SUCCESS = "success";
        public static final String MESSAGE = "message";
        public static final String ERROR = "error";
        public static final String DATA = "data";
        public static final String STATUS = "status";
        public static final String TIMESTAMP = "timestamp";
        public static final String TOTAL = "total";
        public static final String PAGE = "page";
        public static final String SIZE = "size";
    }
    
    // ========== ERROR MESSAGES ==========
    
    /**
     * Common error messages expected from API
     */
    public static final class ErrorMessages {
        // Authentication errors
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
        public static final String TOKEN_EXPIRED = "Token expired";
        public static final String INVALID_TOKEN = "Invalid token";
        
        // Validation errors
        public static final String REQUIRED_FIELD_MISSING = "Required field is missing";
        public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
        public static final String PASSWORD_TOO_WEAK = "Password does not meet requirements";
        public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
        public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
        
        // Product errors
        public static final String PRODUCT_NOT_FOUND = "Product not found";
        public static final String INVALID_PRODUCT_ID = "Invalid product ID";
        public static final String OUT_OF_STOCK = "Product is out of stock";
        public static final String INVALID_QUANTITY = "Invalid quantity";
        
        // General errors
        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
        public static final String BAD_REQUEST = "Bad request";
        public static final String RESOURCE_NOT_FOUND = "Resource not found";
        public static final String METHOD_NOT_ALLOWED = "Method not allowed";
    }
    
    // ========== SUCCESS MESSAGES ==========
    
    /**
     * Common success messages expected from API
     */
    public static final class SuccessMessages {
        public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
        public static final String LOGIN_SUCCESSFUL = "Login successful";
        public static final String LOGOUT_SUCCESSFUL = "Logout successful";
        public static final String PROFILE_UPDATED = "Profile updated successfully";
        public static final String PASSWORD_CHANGED = "Password changed successfully";
        public static final String PRODUCT_ADDED_TO_CART = "Product added to cart";
        public static final String CART_UPDATED = "Cart updated successfully";
        public static final String ORDER_PLACED = "Order placed successfully";
    }
    
    // ========== TEST DATA PATTERNS ==========
    
    /**
     * Patterns for generating test data
     */
    public static final class TestDataPatterns {
        // Username patterns
        public static final String VALID_USERNAME_PATTERN = "testuser_%s";
        public static final String ADMIN_USERNAME_PATTERN = "admin_%s";
        public static final String GUEST_USERNAME_PATTERN = "guest_%s";
        
        // Email patterns
        public static final String VALID_EMAIL_PATTERN = "test_%s@example.com";
        public static final String ADMIN_EMAIL_PATTERN = "admin_%s@example.com";
        public static final String INVALID_EMAIL_PATTERN = "invalid_email_%s";
        
        // Product patterns
        public static final String PRODUCT_TITLE_PATTERN = "Test Product %s";
        public static final String PRODUCT_DESCRIPTION_PATTERN = "Description for test product %s";
        
        // Test prefixes
        public static final String TEST_PREFIX = "TEST_";
        public static final String AUTO_PREFIX = "AUTO_";
        public static final String TEMP_PREFIX = "TEMP_";
    }
    
    // ========== RETRY SETTINGS ==========
    
    /**
     * Retry configuration for flaky operations
     */
    public static final class RetrySettings {
        public static final int DEFAULT_MAX_RETRIES = 3;
        public static final int FAST_MAX_RETRIES = 2;
        public static final int SLOW_MAX_RETRIES = 5;
        public static final long DEFAULT_RETRY_DELAY_MS = 1000; // 1 second
        public static final long FAST_RETRY_DELAY_MS = 500;     // 0.5 seconds
        public static final long SLOW_RETRY_DELAY_MS = 2000;    // 2 seconds
    }
    
    // ========== LOGGING SETTINGS ==========
    
    /**
     * Logging configuration constants
     */
    public static final class Logging {
        public static final String REQUEST_LOG_FORMAT = "REQUEST: {} {} - {}";
        public static final String RESPONSE_LOG_FORMAT = "RESPONSE: {} - {} ms - {}";
        public static final String ERROR_LOG_FORMAT = "ERROR: {} - {}";
        public static final String TEST_START_FORMAT = "TEST STARTED: {}";
        public static final String TEST_END_FORMAT = "TEST COMPLETED: {} - {}";
        
        // Log levels
        public static final String DEBUG_LEVEL = "DEBUG";
        public static final String INFO_LEVEL = "INFO";
        public static final String WARN_LEVEL = "WARN";
        public static final String ERROR_LEVEL = "ERROR";
    }
    
    // ========== VALIDATION RULES ==========
    
    /**
     * Validation rules and patterns
     */
    public static final class ValidationRules {
        // Regex patterns
        public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String PHONE_REGEX = "^[+]?[1-9]\\d{1,14}$";
        public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
        public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        public static final String URL_REGEX = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$";
        public static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        
        // Numeric ranges
        public static final double MIN_RATING = 0.0;
        public static final double MAX_RATING = 5.0;
        public static final int MIN_AGE = 13;
        public static final int MAX_AGE = 120;
    }
    
    // ========== DATABASE SETTINGS ==========
    
    /**
     * Database-related constants for testing
     */
    public static final class Database {
        public static final String TEST_DB_PREFIX = "test_";
        public static final String CLEANUP_QUERY_TIMEOUT = "30";
        public static final int MAX_CONNECTIONS = 10;
        public static final int CONNECTION_TIMEOUT_SECONDS = 30;
        public static final String DEFAULT_CHARSET = "UTF-8";
    }
    
    // ========== FEATURE FLAGS ==========
    
    /**
     * Feature flags for conditional testing
     */
    public static final class FeatureFlags {
        public static final String ENABLE_PERFORMANCE_TESTING = "enable.performance.testing";
        public static final String ENABLE_SECURITY_TESTING = "enable.security.testing";
        public static final String ENABLE_LOAD_TESTING = "enable.load.testing";
        public static final String ENABLE_MOCK_SERVICES = "enable.mock.services";
        public static final String ENABLE_DEBUG_LOGGING = "enable.debug.logging";
        public static final String SKIP_CLEANUP = "skip.cleanup";
    }
}