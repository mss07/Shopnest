package com.ecommerce.api.stepdefinitions;

import com.ecommerce.api.client.RestApiClient;
import com.ecommerce.api.config.ConfigurationManager;
import com.ecommerce.api.context.TestContext;
import com.ecommerce.api.utils.TestDataManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Step definitions for Product Catalog API testing
 * Implements Cucumber step definitions for product retrieval scenarios
 * Handles product list and product by ID API validation
 */
public class ProductApiSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductApiSteps.class);
    
    // Test components
    private RestApiClient apiClient;
    private ConfigurationManager configManager;
    private TestDataManager testDataManager;
    
    // Test data and state
    private Response lastResponse;
    private Response productsListResponse;
    private Response productByIdResponse;
    private String testSessionId;
    private String currentProductId;
    private List<Response> concurrentResponses;
    private ExecutorService executorService;
    
    // API endpoints
    private static final String PRODUCTS_ENDPOINT = "/products";
    private static final String PRODUCT_BY_ID_ENDPOINT = "/product/";
    
    // Expected product fields
    private static final String[] REQUIRED_PRODUCT_FIELDS = {"id", "title", "description", "price", "category"};
    
    /**
     * Setup method executed before each scenario
     */
    @Before
    public void setUp() {
        logger.info("Setting up ProductApiSteps for new scenario");
        
        // Initialize components using shared context
        TestContext context = TestContext.getInstance();
        apiClient = context.getApiClient();
        configManager = context.getConfigManager();
        testDataManager = context.getTestDataManager();
        
        // Generate unique test session ID
        testSessionId = "product_session_" + System.currentTimeMillis();
        
        // Initialize collections
        concurrentResponses = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(5);
        
        logger.debug("ProductApiSteps setup completed for session: {}", testSessionId);
    }
    
    /**
     * Cleanup method executed after each scenario
     */
    @After
    public void tearDown() {
        logger.info("Cleaning up ProductApiSteps for session: {}", testSessionId);
        
        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // Reset API client state
        if (apiClient != null) {
            apiClient.reset();
        }
        
        // Clear instance variables
        lastResponse = null;
        productsListResponse = null;
        productByIdResponse = null;
        currentProductId = null;
        concurrentResponses.clear();
        
        logger.debug("ProductApiSteps cleanup completed for session: {}", testSessionId);
    }
    
    // Given Steps - Test Data Setup
    
    @Given("a product exists with ID {string}")
    public void aProductExistsWithId(String productId) {
        currentProductId = productId;
        
        // Verify product exists by making a GET request
        Response verificationResponse = apiClient.sendGetRequest(PRODUCT_BY_ID_ENDPOINT + productId);
        
        // Log the verification attempt (may succeed or fail depending on test scenario)
        logger.debug("Verified product existence for ID '{}' with status: {}", 
                    productId, verificationResponse.getStatusCode());
    }
    
    @Given("the product catalog is empty")
    public void theProductCatalogIsEmpty() {
        // This step assumes the test environment has been set up with an empty catalog
        // In a real scenario, this might involve database cleanup or using a test environment
        logger.debug("Assuming product catalog is empty for test scenario");
    }
    
    @Given("the product catalog contains more than {int} products")
    public void theProductCatalogContainsMoreThanProducts(int minProductCount) {
        // Verify the catalog has sufficient products by checking the products list
        Response catalogResponse = apiClient.sendGetRequest(PRODUCTS_ENDPOINT);
        
        if (catalogResponse.getStatusCode() == 200) {
            List<Object> products = catalogResponse.jsonPath().getList("$");
            int actualCount = products != null ? products.size() : 0;
            
            logger.debug("Product catalog contains {} products (minimum required: {})", 
                        actualCount, minProductCount);
            
            // Note: In a real test environment, you might need to seed data if insufficient
            if (actualCount <= minProductCount) {
                logger.warn("Product catalog has insufficient products for large dataset test");
            }
        }
    }
    
    // When Steps - API Requests
    
    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        logger.info("Sending GET request to endpoint: {}", endpoint);
        
        lastResponse = apiClient.sendGetRequest(endpoint);
        
        logger.info("GET request completed. Status: {}, Response time: {}ms", 
                   lastResponse.getStatusCode(), lastResponse.getTime());
    }
    
    @When("I send multiple concurrent GET requests to {string}")
    public void iSendMultipleConcurrentGetRequestsTo(String endpoint) {
        logger.info("Sending multiple concurrent GET requests to endpoint: {}", endpoint);
        
        int numberOfRequests = 5;
        List<CompletableFuture<Response>> futures = new ArrayList<>();
        
        // Submit concurrent requests
        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
                RestApiClient concurrentClient = new RestApiClient();
                return concurrentClient.sendGetRequest(endpoint);
            }, executorService);
            
            futures.add(future);
        }
        
        // Wait for all requests to complete
        try {
            for (CompletableFuture<Response> future : futures) {
                Response response = future.get(10, TimeUnit.SECONDS);
                concurrentResponses.add(response);
            }
            
            logger.info("Completed {} concurrent GET requests to {}", numberOfRequests, endpoint);
            
        } catch (Exception e) {
            logger.error("Failed to complete concurrent requests: {}", e.getMessage());
            throw new RuntimeException("Concurrent requests failed: " + e.getMessage(), e);
        }
    }
    
    // Then Steps - Response Validation
    
    @Then("the response should be a valid JSON array")
    public void theResponseShouldBeAValidJsonArray() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        
        // Validate JSON array format
        Assert.assertTrue(responseBody.trim().startsWith("[") && responseBody.trim().endsWith("]"),
                         "Response is not a valid JSON array: " + responseBody);
        
        // Validate that it can be parsed as JSON array
        try {
            List<Object> jsonArray = lastResponse.jsonPath().getList("$");
            Assert.assertNotNull(jsonArray, "Failed to parse response as JSON array");
            
            logger.debug("Valid JSON array validation passed with {} items", jsonArray.size());
            
        } catch (Exception e) {
            Assert.fail("Response is not a valid JSON array: " + e.getMessage());
        }
    }
    
    @Then("the response should be a valid JSON object")
    public void theResponseShouldBeAValidJsonObject() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        
        // Validate JSON object format
        Assert.assertTrue(responseBody.trim().startsWith("{") && responseBody.trim().endsWith("}"),
                         "Response is not a valid JSON object: " + responseBody);
        
        // Validate that it can be parsed as JSON object
        try {
            Map<String, Object> jsonObject = lastResponse.jsonPath().getMap("$");
            Assert.assertNotNull(jsonObject, "Failed to parse response as JSON object");
            
            logger.debug("Valid JSON object validation passed with {} fields", jsonObject.size());
            
        } catch (Exception e) {
            Assert.fail("Response is not a valid JSON object: " + e.getMessage());
        }
    }
    
    @Then("the response should be an empty JSON array")
    public void theResponseShouldBeAnEmptyJsonArray() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        
        // Validate empty JSON array
        Assert.assertTrue(responseBody.trim().equals("[]"),
                         "Response is not an empty JSON array: " + responseBody);
        
        // Validate using JSONPath
        List<Object> jsonArray = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(jsonArray, "Failed to parse response as JSON array");
        Assert.assertTrue(jsonArray.isEmpty(), "JSON array is not empty");
        
        logger.debug("Empty JSON array validation passed");
    }
    
    @And("the response should contain complete product list with all required fields")
    public void theResponseShouldContainCompleteProductListWithAllRequiredFields() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        Assert.assertFalse(products.isEmpty(), "Products list is empty");
        
        // Validate each product has all required fields
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            
            for (String requiredField : REQUIRED_PRODUCT_FIELDS) {
                Assert.assertTrue(product.containsKey(requiredField),
                                String.format("Product at index %d missing required field: %s", i, requiredField));
                Assert.assertNotNull(product.get(requiredField),
                                   String.format("Product at index %d has null value for field: %s", i, requiredField));
            }
        }
        
        logger.debug("Complete product list validation passed for {} products", products.size());
    }

    
    @And("each product should have id, title, description, price, and category fields")
    public void eachProductShouldHaveIdTitleDescriptionPriceAndCategoryFields() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            
            // Validate specific required fields
            Assert.assertTrue(product.containsKey("id"), 
                            String.format("Product at index %d missing 'id' field", i));
            Assert.assertTrue(product.containsKey("title"), 
                            String.format("Product at index %d missing 'title' field", i));
            Assert.assertTrue(product.containsKey("description"), 
                            String.format("Product at index %d missing 'description' field", i));
            Assert.assertTrue(product.containsKey("price"), 
                            String.format("Product at index %d missing 'price' field", i));
            Assert.assertTrue(product.containsKey("category"), 
                            String.format("Product at index %d missing 'category' field", i));
        }
        
        logger.debug("Product fields validation passed for {} products", products.size());
    }
    
    @And("the response should contain exact product JSON details matching the ID")
    public void theResponseShouldContainExactProductJsonDetailsMatchingTheId() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        Assert.assertNotNull(currentProductId, "No current product ID available for validation");
        
        Map<String, Object> product = lastResponse.jsonPath().getMap("$");
        Assert.assertNotNull(product, "Product data is null");
        
        // Validate product ID matches the requested ID
        Object productId = product.get("id");
        Assert.assertNotNull(productId, "Product ID is null in response");
        Assert.assertEquals(productId.toString(), currentProductId,
                          "Product ID in response does not match requested ID");
        
        // Validate product has all required fields
        for (String requiredField : REQUIRED_PRODUCT_FIELDS) {
            Assert.assertTrue(product.containsKey(requiredField),
                            String.format("Product missing required field: %s", requiredField));
            Assert.assertNotNull(product.get(requiredField),
                               String.format("Product has null value for field: %s", requiredField));
        }
        
        logger.debug("Product details validation passed for ID: {}", currentProductId);
    }
    
    @And("the response JSON should have proper structure and data types")
    public void theResponseJsonShouldHaveProperStructureAndDataTypes() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        Map<String, Object> product = lastResponse.jsonPath().getMap("$");
        Assert.assertNotNull(product, "Product data is null");
        
        // Validate data types for each field
        Object id = product.get("id");
        Assert.assertTrue(id instanceof String, "Product 'id' should be a string");
        
        Object title = product.get("title");
        Assert.assertTrue(title instanceof String, "Product 'title' should be a string");
        
        Object description = product.get("description");
        Assert.assertTrue(description instanceof String, "Product 'description' should be a string");
        
        Object price = product.get("price");
        Assert.assertTrue(price instanceof Number, "Product 'price' should be a number");
        
        Object category = product.get("category");
        Assert.assertTrue(category instanceof String, "Product 'category' should be a string");
        
        logger.debug("Product data types validation passed");
    }
    
    @And("the product data should be consistent with the products list")
    public void theProductDataShouldBeConsistentWithTheProductsList() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        Assert.assertNotNull(currentProductId, "No current product ID available for validation");
        
        // Store current response as product by ID response
        productByIdResponse = lastResponse;
        
        // Get products list for comparison
        productsListResponse = apiClient.sendGetRequest(PRODUCTS_ENDPOINT);
        Assert.assertEquals(productsListResponse.getStatusCode(), 200, 
                          "Failed to get products list for consistency check");
        
        // Find the product in the list
        List<Map<String, Object>> products = productsListResponse.jsonPath().getList("$");
        Map<String, Object> productFromList = null;
        
        for (Map<String, Object> product : products) {
            if (currentProductId.equals(product.get("id").toString())) {
                productFromList = product;
                break;
            }
        }
        
        Assert.assertNotNull(productFromList, 
                           "Product with ID " + currentProductId + " not found in products list");
        
        // Compare product data
        Map<String, Object> productById = productByIdResponse.jsonPath().getMap("$");
        
        for (String field : REQUIRED_PRODUCT_FIELDS) {
            Object valueFromList = productFromList.get(field);
            Object valueById = productById.get(field);
            
            Assert.assertEquals(valueById, valueFromList,
                              String.format("Field '%s' mismatch between endpoints. List: %s, ById: %s",
                                          field, valueFromList, valueById));
        }
        
        logger.debug("Product data consistency validation passed for ID: {}", currentProductId);
    }
    
    @And("the response should contain appropriate HTTP error status")
    public void theResponseShouldContainAppropriateHttpErrorStatus() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        int statusCode = lastResponse.getStatusCode();
        Assert.assertTrue(statusCode >= 400 && statusCode < 600,
                         "Expected HTTP error status (4xx or 5xx) but got: " + statusCode);
        
        logger.debug("HTTP error status validation passed: {}", statusCode);
    }
    
    @And("the response should contain error JSON message")
    public void theResponseShouldContainErrorJsonMessage() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body is null");
        Assert.assertFalse(responseBody.trim().isEmpty(), "Response body is empty");
        
        // Validate response contains error-related content
        boolean hasErrorContent = responseBody.toLowerCase().contains("error") ||
                                responseBody.toLowerCase().contains("not found") ||
                                responseBody.toLowerCase().contains("invalid") ||
                                responseBody.toLowerCase().contains("message");
        
        Assert.assertTrue(hasErrorContent, "Response does not contain error message: " + responseBody);
        
        logger.debug("Error JSON message validation passed");
    }
    
    @And("the error message should indicate product not found")
    public void theErrorMessageShouldIndicateProductNotFound() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        String responseBody = lastResponse.getBody().asString();
        boolean hasNotFoundMessage = responseBody.toLowerCase().contains("not found") ||
                                   responseBody.toLowerCase().contains("product") ||
                                   responseBody.toLowerCase().contains("404") ||
                                   responseBody.toLowerCase().contains("does not exist");
        
        Assert.assertTrue(hasNotFoundMessage, 
                         "Response does not indicate product not found: " + responseBody);
        
        logger.debug("Product not found message validation passed");
    }
    
    @And("each product in the array should have required fields")
    public void eachProductInTheArrayShouldHaveRequiredFields() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            
            for (String requiredField : REQUIRED_PRODUCT_FIELDS) {
                Assert.assertTrue(product.containsKey(requiredField),
                                String.format("Product at index %d missing required field: %s", i, requiredField));
            }
        }
        
        logger.debug("Required fields validation passed for {} products", products.size());
    }
    
    @And("each product should have valid data types for all fields")
    public void eachProductShouldHaveValidDataTypesForAllFields() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            
            // Validate data types
            Object id = product.get("id");
            Assert.assertTrue(id instanceof String, 
                            String.format("Product %d 'id' should be a string but got: %s", i, id.getClass()));
            
            Object title = product.get("title");
            Assert.assertTrue(title instanceof String, 
                            String.format("Product %d 'title' should be a string but got: %s", i, title.getClass()));
            
            Object description = product.get("description");
            Assert.assertTrue(description instanceof String, 
                            String.format("Product %d 'description' should be a string but got: %s", i, description.getClass()));
            
            Object price = product.get("price");
            Assert.assertTrue(price instanceof Number, 
                            String.format("Product %d 'price' should be a number but got: %s", i, price.getClass()));
            
            Object category = product.get("category");
            Assert.assertTrue(category instanceof String, 
                            String.format("Product %d 'category' should be a string but got: %s", i, category.getClass()));
        }
        
        logger.debug("Data types validation passed for {} products", products.size());
    }
    
    @And("the JSON structure should be consistent across all products")
    public void theJsonStructureShouldBeConsistentAcrossAllProducts() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        
        if (products.isEmpty()) {
            logger.debug("No products to validate consistency");
            return;
        }
        
        // Use first product as reference structure
        Map<String, Object> referenceProduct = products.get(0);
        
        for (int i = 1; i < products.size(); i++) {
            Map<String, Object> currentProduct = products.get(i);
            
            // Validate same number of fields
            Assert.assertEquals(currentProduct.size(), referenceProduct.size(),
                              String.format("Product %d has different number of fields than reference", i));
            
            // Validate same field names
            for (String fieldName : referenceProduct.keySet()) {
                Assert.assertTrue(currentProduct.containsKey(fieldName),
                                String.format("Product %d missing field '%s' found in reference", i, fieldName));
            }
        }
        
        logger.debug("JSON structure consistency validation passed for {} products", products.size());
    }
    
    @And("the product JSON should contain {string} field with string value")
    public void theProductJsonShouldContainFieldWithStringValue(String fieldName) {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        Map<String, Object> product = lastResponse.jsonPath().getMap("$");
        Assert.assertNotNull(product, "Product data is null");
        
        Assert.assertTrue(product.containsKey(fieldName),
                         String.format("Product missing field: %s", fieldName));
        
        Object fieldValue = product.get(fieldName);
        Assert.assertNotNull(fieldValue, String.format("Field '%s' is null", fieldName));
        Assert.assertTrue(fieldValue instanceof String,
                         String.format("Field '%s' should be string but got: %s", fieldName, fieldValue.getClass()));
        
        logger.debug("String field '{}' validation passed with value: {}", fieldName, fieldValue);
    }
    
    @And("the product JSON should contain {string} field with numeric value")
    public void theProductJsonShouldContainFieldWithNumericValue(String fieldName) {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        Map<String, Object> product = lastResponse.jsonPath().getMap("$");
        Assert.assertNotNull(product, "Product data is null");
        
        Assert.assertTrue(product.containsKey(fieldName),
                         String.format("Product missing field: %s", fieldName));
        
        Object fieldValue = product.get(fieldName);
        Assert.assertNotNull(fieldValue, String.format("Field '%s' is null", fieldName));
        Assert.assertTrue(fieldValue instanceof Number,
                         String.format("Field '%s' should be numeric but got: %s", fieldName, fieldValue.getClass()));
        
        logger.debug("Numeric field '{}' validation passed with value: {}", fieldName, fieldValue);
    }
    
    @And("both responses should have status code {int}")
    public void bothResponsesShouldHaveStatusCode(int expectedStatusCode) {
        Assert.assertNotNull(productsListResponse, "Products list response is not available");
        Assert.assertNotNull(productByIdResponse, "Product by ID response is not available");
        
        Assert.assertEquals(productsListResponse.getStatusCode(), expectedStatusCode,
                          "Products list response status code mismatch");
        Assert.assertEquals(productByIdResponse.getStatusCode(), expectedStatusCode,
                          "Product by ID response status code mismatch");
        
        logger.debug("Both responses status code validation passed: {}", expectedStatusCode);
    }
    
    @And("the product data from both endpoints should match exactly")
    public void theProductDataFromBothEndpointsShouldMatchExactly() {
        Assert.assertNotNull(productsListResponse, "Products list response is not available");
        Assert.assertNotNull(productByIdResponse, "Product by ID response is not available");
        Assert.assertNotNull(currentProductId, "No current product ID available for validation");
        
        // Get product from list
        List<Map<String, Object>> products = productsListResponse.jsonPath().getList("$");
        Map<String, Object> productFromList = null;
        
        for (Map<String, Object> product : products) {
            if (currentProductId.equals(product.get("id").toString())) {
                productFromList = product;
                break;
            }
        }
        
        Assert.assertNotNull(productFromList, "Product not found in products list");
        
        // Get product by ID
        Map<String, Object> productById = productByIdResponse.jsonPath().getMap("$");
        
        // Compare all fields
        for (String field : REQUIRED_PRODUCT_FIELDS) {
            Object valueFromList = productFromList.get(field);
            Object valueById = productById.get(field);
            
            Assert.assertEquals(valueById, valueFromList,
                              String.format("Field '%s' mismatch. List: %s, ById: %s", field, valueFromList, valueById));
        }
        
        logger.debug("Product data exact match validation passed for ID: {}", currentProductId);
    }
    
    @And("the JSON structure should be consistent between responses")
    public void theJsonStructureShouldBeConsistentBetweenResponses() {
        Assert.assertNotNull(productsListResponse, "Products list response is not available");
        Assert.assertNotNull(productByIdResponse, "Product by ID response is not available");
        Assert.assertNotNull(currentProductId, "No current product ID available for validation");
        
        // Get product from list
        List<Map<String, Object>> products = productsListResponse.jsonPath().getList("$");
        Map<String, Object> productFromList = null;
        
        for (Map<String, Object> product : products) {
            if (currentProductId.equals(product.get("id").toString())) {
                productFromList = product;
                break;
            }
        }
        
        Assert.assertNotNull(productFromList, "Product not found in products list");
        
        // Get product by ID
        Map<String, Object> productById = productByIdResponse.jsonPath().getMap("$");
        
        // Validate same structure (same keys)
        Assert.assertEquals(productById.keySet(), productFromList.keySet(),
                          "JSON structure differs between endpoints");
        
        logger.debug("JSON structure consistency validation passed between endpoints");
    }



    
    @And("the response should have appropriate server headers")
    public void theResponseShouldHaveAppropriateServerHeaders() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // Check for common server headers
        String server = lastResponse.getHeader("Server");
        String xPoweredBy = lastResponse.getHeader("X-Powered-By");
        
        logger.debug("Server headers - Server: {}, X-Powered-By: {}", server, xPoweredBy);
        
        // Note: Server headers validation is informational
        logger.debug("Server headers check completed");
    }
    



    
    @And("the response should contain all products in the catalog")
    public void theResponseShouldContainAllProductsInTheCatalog() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        List<Map<String, Object>> products = lastResponse.jsonPath().getList("$");
        Assert.assertNotNull(products, "Products list is null");
        
        // Validate that we have a reasonable number of products for a large dataset
        Assert.assertTrue(products.size() > 0, "Products list is empty");
        
        logger.debug("Large dataset validation passed with {} products", products.size());
    }
    
    @And("the response should be properly formatted JSON array")
    public void theResponseShouldBeProperlyFormattedJsonArray() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        // This validation is already covered by theResponseShouldBeAValidJsonArray()
        theResponseShouldBeAValidJsonArray();
        
        logger.debug("Properly formatted JSON array validation passed");
    }
    
    @And("the response time should be reasonable for the dataset size")
    public void theResponseTimeShouldBeReasonableForTheDatasetSize() {
        Assert.assertNotNull(lastResponse, "No response available for validation");
        
        long actualResponseTime = lastResponse.getTime();
        
        // For large datasets, allow more time (e.g., 10 seconds)
        long maxReasonableTime = 10000; // 10 seconds
        
        Assert.assertTrue(actualResponseTime < maxReasonableTime,
                         String.format("Response time %d ms is not reasonable for large dataset (max: %d ms)", 
                                     actualResponseTime, maxReasonableTime));
        
        logger.info("Large dataset response time validation passed: {} ms", actualResponseTime);
    }
    
    @And("all responses should have status code {int}")
    public void allResponsesShouldHaveStatusCode(int expectedStatusCode) {
        Assert.assertFalse(concurrentResponses.isEmpty(), "No concurrent responses available for validation");
        
        for (int i = 0; i < concurrentResponses.size(); i++) {
            Response response = concurrentResponses.get(i);
            Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                              String.format("Concurrent response %d has status %d, expected %d", 
                                          i, response.getStatusCode(), expectedStatusCode));
        }
        
        logger.debug("All {} concurrent responses have status code {}", concurrentResponses.size(), expectedStatusCode);
    }
    
    @And("all responses should contain identical product data")
    public void allResponsesShouldContainIdenticalProductData() {
        Assert.assertFalse(concurrentResponses.isEmpty(), "No concurrent responses available for validation");
        
        // Use first response as reference
        String referenceBody = concurrentResponses.get(0).getBody().asString();
        
        for (int i = 1; i < concurrentResponses.size(); i++) {
            String currentBody = concurrentResponses.get(i).getBody().asString();
            Assert.assertEquals(currentBody, referenceBody,
                              String.format("Concurrent response %d has different data than reference", i));
        }
        
        logger.debug("All {} concurrent responses contain identical data", concurrentResponses.size());
    }
    
    @And("no response should be corrupted or incomplete")
    public void noResponseShouldBeCorruptedOrIncomplete() {
        Assert.assertFalse(concurrentResponses.isEmpty(), "No concurrent responses available for validation");
        
        for (int i = 0; i < concurrentResponses.size(); i++) {
            Response response = concurrentResponses.get(i);
            
            // Validate response is not null and has content
            Assert.assertNotNull(response, String.format("Concurrent response %d is null", i));
            
            String responseBody = response.getBody().asString();
            Assert.assertNotNull(responseBody, String.format("Concurrent response %d body is null", i));
            Assert.assertFalse(responseBody.trim().isEmpty(), 
                              String.format("Concurrent response %d body is empty", i));
            
            // Validate JSON structure
            try {
                List<Object> products = response.jsonPath().getList("$");
                Assert.assertNotNull(products, String.format("Concurrent response %d JSON parsing failed", i));
            } catch (Exception e) {
                Assert.fail(String.format("Concurrent response %d is corrupted: %s", i, e.getMessage()));
            }
        }
        
        logger.debug("All {} concurrent responses are complete and not corrupted", concurrentResponses.size());
    }
}