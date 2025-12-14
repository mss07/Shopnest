package com.ecommerce.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test Isolation Manager for ensuring test data isolation
 * Manages test execution context and ensures proper cleanup
 * Provides thread-safe operations for parallel test execution
 */
public class TestIsolationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TestIsolationManager.class);
    private static TestIsolationManager instance;
    
    // Thread-local storage for test context
    private final ThreadLocal<TestContext> testContext = new ThreadLocal<>();
    
    // Global tracking of active test sessions
    private final Map<String, TestSession> activeSessions = new ConcurrentHashMap<>();
    
    /**
     * Private constructor for singleton pattern
     */
    private TestIsolationManager() {
        logger.info("TestIsolationManager initialized");
    }
    
    /**
     * Get singleton instance of TestIsolationManager
     * @return TestIsolationManager instance
     */
    public static synchronized TestIsolationManager getInstance() {
        if (instance == null) {
            instance = new TestIsolationManager();
        }
        return instance;
    }
    
    /**
     * Start a new test session
     * @param sessionId Unique session identifier
     * @param testName Name of the test
     * @return TestSession object
     */
    public TestSession startTestSession(String sessionId, String testName) {
        TestSession session = new TestSession(sessionId, testName);
        activeSessions.put(sessionId, session);
        
        // Set thread-local context
        TestContext context = new TestContext(sessionId, testName);
        testContext.set(context);
        
        logger.debug("Started test session: {} for test: {}", sessionId, testName);
        return session;
    }
    
    /**
     * Get current test context for the thread
     * @return TestContext or null if no active session
     */
    public TestContext getCurrentTestContext() {
        return testContext.get();
    }
    
    /**
     * Get current session ID for the thread
     * @return Session ID or null if no active session
     */
    public String getCurrentSessionId() {
        TestContext context = testContext.get();
        return context != null ? context.getSessionId() : null;
    }
    
    /**
     * Add a resource to the current test session for cleanup
     * @param resourceType Type of resource (e.g., "user", "product", "order")
     * @param resourceId Unique identifier of the resource
     */
    public void addResourceForCleanup(String resourceType, String resourceId) {
        TestContext context = testContext.get();
        if (context == null) {
            logger.warn("No active test context. Cannot add resource for cleanup: {} - {}", 
                       resourceType, resourceId);
            return;
        }
        
        TestSession session = activeSessions.get(context.getSessionId());
        if (session != null) {
            session.addResource(resourceType, resourceId);
            logger.debug("Added resource for cleanup: {} - {} in session: {}", 
                        resourceType, resourceId, context.getSessionId());
        }
    }
    
    /**
     * End the current test session and perform cleanup
     * @return CleanupResult with cleanup statistics
     */
    public CleanupResult endCurrentTestSession() {
        TestContext context = testContext.get();
        if (context == null) {
            logger.warn("No active test context to end");
            return new CleanupResult(0, 0);
        }
        
        return endTestSession(context.getSessionId());
    }
    
    /**
     * End a specific test session and perform cleanup
     * @param sessionId Session ID to end
     * @return CleanupResult with cleanup statistics
     */
    public CleanupResult endTestSession(String sessionId) {
        TestSession session = activeSessions.remove(sessionId);
        if (session == null) {
            logger.warn("No active session found for ID: {}", sessionId);
            return new CleanupResult(0, 0);
        }
        
        // Perform cleanup
        CleanupResult result = performCleanup(session);
        
        // Clear thread-local context if it matches this session
        TestContext context = testContext.get();
        if (context != null && sessionId.equals(context.getSessionId())) {
            testContext.remove();
        }
        
        logger.info("Ended test session: {} with cleanup result: {}", sessionId, result);
        return result;
    }
    
    /**
     * Perform cleanup for a test session
     * @param session TestSession to clean up
     * @return CleanupResult with statistics
     */
    private CleanupResult performCleanup(TestSession session) {
        int resourcesProcessed = 0;
        int resourcesCleaned = 0;
        
        TestDataManager testDataManager = TestDataManager.getInstance();
        
        for (Map.Entry<String, Set<String>> entry : session.getResources().entrySet()) {
            String resourceType = entry.getKey();
            Set<String> resourceIds = entry.getValue();
            
            for (String resourceId : resourceIds) {
                resourcesProcessed++;
                
                try {
                    boolean cleaned = false;
                    
                    // Handle different resource types
                    switch (resourceType.toLowerCase()) {
                        case "user":
                        case "testuser":
                            cleaned = testDataManager.cleanupTestUser(resourceId);
                            break;
                        case "session":
                            cleaned = testDataManager.cleanupTestSession(resourceId) > 0;
                            break;
                        default:
                            logger.warn("Unknown resource type for cleanup: {}", resourceType);
                            break;
                    }
                    
                    if (cleaned) {
                        resourcesCleaned++;
                        logger.debug("Successfully cleaned up {} resource: {}", resourceType, resourceId);
                    } else {
                        logger.warn("Failed to clean up {} resource: {}", resourceType, resourceId);
                    }
                    
                } catch (Exception e) {
                    logger.error("Error cleaning up {} resource: {}", resourceType, resourceId, e);
                }
            }
        }
        
        return new CleanupResult(resourcesProcessed, resourcesCleaned);
    }
    
    /**
     * Get statistics about active test sessions
     * @return IsolationStatistics object
     */
    public IsolationStatistics getIsolationStatistics() {
        int totalSessions = activeSessions.size();
        int totalResources = activeSessions.values().stream()
                                          .mapToInt(session -> session.getTotalResourceCount())
                                          .sum();
        
        return new IsolationStatistics(totalSessions, totalResources);
    }
    
    /**
     * Force cleanup of all active sessions (emergency cleanup)
     * @return Total number of resources cleaned up
     */
    public int forceCleanupAllSessions() {
        logger.warn("Performing force cleanup of all active sessions");
        
        int totalCleaned = 0;
        for (String sessionId : activeSessions.keySet()) {
            CleanupResult result = endTestSession(sessionId);
            totalCleaned += result.getResourcesCleaned();
        }
        
        // Clear all thread-local contexts
        testContext.remove();
        
        logger.info("Force cleanup completed. Total resources cleaned: {}", totalCleaned);
        return totalCleaned;
    }
    
    /**
     * Test Context class for thread-local storage
     */
    public static class TestContext {
        private final String sessionId;
        private final String testName;
        private final long startTime;
        
        public TestContext(String sessionId, String testName) {
            this.sessionId = sessionId;
            this.testName = testName;
            this.startTime = System.currentTimeMillis();
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public String getTestName() {
            return testName;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
    }
    
    /**
     * Test Session class for tracking session resources
     */
    public static class TestSession {
        private final String sessionId;
        private final String testName;
        private final long startTime;
        private final Map<String, Set<String>> resources = new ConcurrentHashMap<>();
        
        public TestSession(String sessionId, String testName) {
            this.sessionId = sessionId;
            this.testName = testName;
            this.startTime = System.currentTimeMillis();
        }
        
        public void addResource(String resourceType, String resourceId) {
            resources.computeIfAbsent(resourceType, k -> ConcurrentHashMap.newKeySet())
                     .add(resourceId);
        }
        
        public Map<String, Set<String>> getResources() {
            return resources;
        }
        
        public int getTotalResourceCount() {
            return resources.values().stream()
                           .mapToInt(Set::size)
                           .sum();
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public String getTestName() {
            return testName;
        }
        
        public long getStartTime() {
            return startTime;
        }
    }
    
    /**
     * Cleanup Result class
     */
    public static class CleanupResult {
        private final int resourcesProcessed;
        private final int resourcesCleaned;
        
        public CleanupResult(int resourcesProcessed, int resourcesCleaned) {
            this.resourcesProcessed = resourcesProcessed;
            this.resourcesCleaned = resourcesCleaned;
        }
        
        public int getResourcesProcessed() {
            return resourcesProcessed;
        }
        
        public int getResourcesCleaned() {
            return resourcesCleaned;
        }
        
        public boolean isFullySuccessful() {
            return resourcesProcessed == resourcesCleaned;
        }
        
        @Override
        public String toString() {
            return String.format("CleanupResult{processed=%d, cleaned=%d, success=%s}", 
                               resourcesProcessed, resourcesCleaned, isFullySuccessful());
        }
    }
    
    /**
     * Isolation Statistics class
     */
    public static class IsolationStatistics {
        private final int activeSessions;
        private final int totalResources;
        
        public IsolationStatistics(int activeSessions, int totalResources) {
            this.activeSessions = activeSessions;
            this.totalResources = totalResources;
        }
        
        public int getActiveSessions() {
            return activeSessions;
        }
        
        public int getTotalResources() {
            return totalResources;
        }
        
        @Override
        public String toString() {
            return String.format("IsolationStatistics{activeSessions=%d, totalResources=%d}", 
                               activeSessions, totalResources);
        }
    }
}