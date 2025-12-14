package com.ecommerce.api.context;

import com.ecommerce.api.client.RestApiClient;
import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.utils.TestDataManager;

/**
 * Shared test context for Cucumber scenarios
 * Provides shared instances of test components across step definitions
 */
public class TestContext {
    
    private static final ThreadLocal<TestContext> context = new ThreadLocal<>();
    
    private final RestApiClient apiClient;
    private final ConfigurationManager configManager;
    private final TestDataManager testDataManager;
    
    private TestContext() {
        this.configManager = ConfigurationManager.getInstance();
        this.apiClient = new RestApiClient();
        this.testDataManager = TestDataManager.getInstance();
    }
    
    /**
     * Get the current test context for the thread
     * @return TestContext instance
     */
    public static TestContext getInstance() {
        TestContext ctx = context.get();
        if (ctx == null) {
            ctx = new TestContext();
            context.set(ctx);
        }
        return ctx;
    }
    
    /**
     * Clear the test context for the current thread
     */
    public static void clear() {
        TestContext ctx = context.get();
        if (ctx != null) {
            ctx.apiClient.reset();
        }
        context.remove();
    }
    
    /**
     * Get the shared REST API client
     * @return RestApiClient instance
     */
    public RestApiClient getApiClient() {
        return apiClient;
    }
    
    /**
     * Get the configuration manager
     * @return ConfigurationManager instance
     */
    public ConfigurationManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get the test data manager
     * @return TestDataManager instance
     */
    public TestDataManager getTestDataManager() {
        return testDataManager;
    }
}