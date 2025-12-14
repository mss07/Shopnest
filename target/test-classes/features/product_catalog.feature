Feature: Product Catalog API
  As a QA engineer
  I want to test product catalog REST APIs comprehensively
  So that I can verify product data retrieval and JSON response integrity

  Background:
    Given the API base URL is configured
    And the test environment is ready

  @product-catalog @positive @smoke
  Scenario: Get all products successfully
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should be a valid JSON array
    And the response should contain complete product list with all required fields
    And the response should have proper HTTP headers and content-type
    And each product should have id, title, description, price, and category fields

  @product-catalog @positive @smoke
  Scenario: Get product by valid ID successfully
    Given a product exists with ID "507f1f77bcf86cd799439011"
    When I send a GET request to "/product/507f1f77bcf86cd799439011"
    Then the response status code should be 200
    And the response should contain exact product JSON details matching the ID
    And the response JSON should have proper structure and data types
    And the product data should be consistent with the products list

  @product-catalog @negative @regression
  Scenario: Get product by invalid ID returns error
    When I send a GET request to "/product/invalid-product-id-123"
    Then the response status code should be 404
    And the response should contain appropriate HTTP error status
    And the response should contain error JSON message
    And the error message should indicate product not found

  @product-catalog @negative
  Scenario: Get product by non-existent valid format ID
    When I send a GET request to "/product/507f1f77bcf86cd799439999"
    Then the response status code should be 404
    And the response should contain error JSON message
    And the error message should indicate product not found

  @product-catalog @edge-case
  Scenario Outline: Get product with malformed ID formats
    When I send a GET request to "/product/<product_id>"
    Then the response status code should be <status_code>
    And the response should contain error JSON message
    
    Examples:
      | product_id | status_code |
      | ""         | 404         |
      | null       | 404         |
      | 123        | 404         |
      | abc        | 404         |
      | !@#$%      | 404         |

  @product-catalog @validation
  Scenario: Products endpoint JSON structure validation
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should be a valid JSON array
    And each product in the array should have required fields
    And each product should have valid data types for all fields
    And the JSON structure should be consistent across all products

  @product-catalog @validation
  Scenario: Product by ID JSON structure validation
    Given a product exists with ID "507f1f77bcf86cd799439011"
    When I send a GET request to "/product/507f1f77bcf86cd799439011"
    Then the response status code should be 200
    And the response should be a valid JSON object
    And the product JSON should contain "id" field with string value
    And the product JSON should contain "title" field with string value
    And the product JSON should contain "description" field with string value
    And the product JSON should contain "price" field with numeric value
    And the product JSON should contain "category" field with string value

  @product-catalog @consistency
  Scenario: Product data consistency between endpoints
    Given a product exists with ID "507f1f77bcf86cd799439011"
    When I send a GET request to "/products"
    And I send a GET request to "/product/507f1f77bcf86cd799439011"
    Then both responses should have status code 200
    And the product data from both endpoints should match exactly
    And the JSON structure should be consistent between responses

  @product-catalog @edge-case
  Scenario: Empty products list handling
    Given the product catalog is empty
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should be an empty JSON array
    And the response should have proper HTTP headers and content-type

  @product-catalog @performance
  Scenario: Products endpoint response time validation
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response time should be less than 5000 milliseconds

  @product-catalog @performance
  Scenario: Product by ID response time validation
    Given a product exists with ID "507f1f77bcf86cd799439011"
    When I send a GET request to "/product/507f1f77bcf86cd799439011"
    Then the response status code should be 200
    And the response time should be less than 3000 milliseconds

  @product-catalog @headers
  Scenario: Products endpoint HTTP headers validation
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should have "Content-Type" header with value "application/json"
    And the response should have proper cache control headers
    And the response should have appropriate server headers

  @product-catalog @headers
  Scenario: Product by ID HTTP headers validation
    Given a product exists with ID "507f1f77bcf86cd799439011"
    When I send a GET request to "/product/507f1f77bcf86cd799439011"
    Then the response status code should be 200
    And the response should have "Content-Type" header with value "application/json"
    And the response should have proper cache control headers

  @api-error-handling
  Scenario: Product catalog endpoint error handling for non-existent endpoints
    When I send a GET request to "/invalid-products-endpoint"
    Then the response status code should be 404
    And the response should contain appropriate error message

  @product-catalog @security
  Scenario: Product catalog endpoints security validation
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should not contain sensitive server information
    And the response should have appropriate security headers

  @product-catalog @large-dataset
  Scenario: Products endpoint with large dataset handling
    Given the product catalog contains more than 100 products
    When I send a GET request to "/products"
    Then the response status code should be 200
    And the response should contain all products in the catalog
    And the response should be properly formatted JSON array
    And the response time should be reasonable for the dataset size

  @product-catalog @concurrent
  Scenario: Concurrent requests to product endpoints
    When I send multiple concurrent GET requests to "/products"
    Then all responses should have status code 200
    And all responses should contain identical product data
    And no response should be corrupted or incomplete