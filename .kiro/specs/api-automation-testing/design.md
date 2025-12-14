# API Automation Testing Design Document

## Overview

This design document outlines the architecture and implementation approach for comprehensive REST API automation testing using Cucumber BDD framework and REST Assured library. The solution will provide a robust testing framework that validates the e-commerce application's REST endpoints including user authentication (/signup, /login) and product catalog (/products, /product/:id) APIs.

The framework will be built using Java with Maven as the build tool, incorporating Cucumber for behavior-driven development and REST Assured for HTTP API testing. The design emphasizes maintainability, readability, and comprehensive test coverage while providing detailed reporting and CI/CD integration capabilities.

## Architecture

The testing framework follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Test Execution Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │ Cucumber Runner │  │   Test Reports  │  │  CI/CD      │  │
│  │                 │  │   (HTML/JSON)   │  │ Integration │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    BDD Scenario Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ User Management │  │ Product Catalog │  │   Common    │ │
│  │   Features      │  │    Features     │  │  Features   │ │
│  │   (.feature)    │  │   (.feature)    │  │ (.feature)  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   Step Definition Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ User API Steps  │  │ Product API     │  │   Common    │ │
│  │                 │  │     Steps       │  │    Steps    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    API Client Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  REST Assured   │  │  HTTP Request   │  │  Response   │ │
│  │    Client       │  │    Builder      │  │  Validator  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   Data Management Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  Test Data      │  │   Configuration │  │   Utilities │ │
│  │   Manager       │  │    Manager      │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Components and Interfaces

### 1. REST API Client (REST Assured Integration)
- **Purpose**: Handles HTTP requests and responses using REST Assured library
- **Key Methods**:
  - `sendGetRequest(String endpoint)`: Execute GET requests
  - `sendPostRequest(String endpoint, Object requestBody)`: Execute POST requests
  - `validateStatusCode(int expectedCode)`: Validate HTTP status codes
  - `validateJsonResponse(String jsonPath, Object expectedValue)`: Validate JSON response content
  - `extractResponseData(String jsonPath)`: Extract data from JSON responses

### 2. Step Definition Classes
- **UserApiSteps**: Implements step definitions for user authentication scenarios
- **ProductApiSteps**: Implements step definitions for product catalog scenarios  
- **CommonSteps**: Implements shared step definitions for common API operations

### 3. Test Data Manager
- **Purpose**: Manages test data creation, setup, and cleanup
- **Key Methods**:
  - `createTestUser()`: Generate test user data
  - `getValidUserCredentials()`: Provide valid login credentials
  - `getInvalidUserCredentials()`: Provide invalid login credentials
  - `cleanupTestData()`: Remove test data after scenarios

### 4. Configuration Manager
- **Purpose**: Manages environment configuration and API endpoints
- **Key Properties**:
  - Base URL configuration
  - Environment-specific settings
  - API endpoint mappings
  - Test execution parameters

## Data Models

### User Test Data Model
```java
public class TestUser {
    private String username;
    private String password;
    private String email;
    private String phone;
    // Constructors, getters, setters
}
```

### API Response Model
```java
public class ApiResponse {
    private int statusCode;
    private String responseBody;
    private Map<String, String> headers;
    private long responseTime;
    // Constructors, getters, setters
}
```

### Product Data Model
```java
public class Product {
    private String id;
    private String title;
    private String description;
    private double price;
    private String category;
    // Constructors, getters, setters
}
```
## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

Property 1: Test data lifecycle management
*For any* test scenario execution, when test data is created by the API_Test_Data_Manager, it should be properly cleaned up after scenario completion
**Validates: Requirements 1.5**

Property 2: Valid user registration consistency
*For any* valid user registration request with unique username, the POST /signup endpoint should return HTTP 200 status and success JSON response
**Validates: Requirements 2.1**

Property 3: Duplicate username rejection
*For any* user registration request with existing username, the POST /signup endpoint should return HTTP 401 status and error JSON message
**Validates: Requirements 2.2**

Property 4: Valid authentication success
*For any* valid login credentials, the POST /login endpoint should return HTTP 200 status and user data JSON
**Validates: Requirements 2.3**

Property 5: Invalid authentication rejection
*For any* invalid login credentials, the POST /login endpoint should return HTTP 401 status and error JSON response
**Validates: Requirements 2.4**

Property 6: Registration response structure consistency
*For any* successful user registration, the JSON response should contain user data with proper structure and required fields
**Validates: Requirements 2.5**

Property 7: Products endpoint reliability
*For any* GET /products request, the endpoint should return HTTP 200 status and complete product list JSON with all required fields
**Validates: Requirements 3.1**

Property 8: Product by ID retrieval accuracy
*For any* valid product ID, GET /product/:id should return HTTP 200 status and exact product JSON details matching the requested ID
**Validates: Requirements 3.2**

Property 9: Invalid product ID error handling
*For any* invalid product ID, GET /product/:id should return appropriate HTTP error status and error JSON message
**Validates: Requirements 3.3**

Property 10: Product response consistency
*For any* product data retrieval via REST API, the JSON structure should remain consistent across multiple requests
**Validates: Requirements 3.4**

Property 11: Products response format compliance
*For any* GET /products request, the response should be valid JSON array with proper HTTP headers and content-type
**Validates: Requirements 3.5**

Property 12: HTTP method support
*For any* REST API request, the REST_Assured_Client should properly support GET and POST HTTP methods with correct headers and content-type
**Validates: Requirements 4.1**

Property 13: Response validation completeness
*For any* HTTP response received, the REST_Assured_Client should validate HTTP status codes, response headers, and JSON response body structure
**Validates: Requirements 4.2**

Property 14: JSON validation accuracy
*For any* JSON response processed, the REST_Assured_Client should perform JSON schema validation and data type verification
**Validates: Requirements 4.3**

Property 15: Error information capture
*For any* REST API error occurrence, the REST_Assured_Client should capture detailed HTTP error information and response body for debugging
**Validates: Requirements 4.4**

Property 16: Request serialization reliability
*For any* Java object requiring serialization, the REST_Assured_Client should convert it to JSON automatically for POST requests
**Validates: Requirements 4.5**

Property 17: Test failure documentation
*For any* REST API test failure, the framework should capture HTTP request/response details and JSON error messages
**Validates: Requirements 5.2**

Property 18: HTTP interaction logging
*For any* REST API test execution, all HTTP interactions should be logged with timestamps and request correlation IDs
**Validates: Requirements 5.3**

Property 19: Execution metrics collection
*For any* test report generation, the framework should include REST API execution metrics and HTTP response time data
**Validates: Requirements 5.4**

## Error Handling

The testing framework implements comprehensive error handling across multiple layers:

### API Client Error Handling
- **Connection Failures**: Retry mechanism with exponential backoff for network issues
- **Timeout Handling**: Configurable request timeouts with proper error reporting
- **HTTP Error Responses**: Detailed capture of 4xx and 5xx responses with full context
- **JSON Parsing Errors**: Graceful handling of malformed JSON responses with error details

### Test Execution Error Handling
- **Step Definition Failures**: Clear error messages with context about failed assertions
- **Data Setup Failures**: Rollback mechanisms for test data creation issues
- **Configuration Errors**: Validation of configuration parameters with helpful error messages
- **Resource Cleanup**: Guaranteed cleanup of test resources even when tests fail

### Reporting Error Handling
- **Report Generation Failures**: Fallback mechanisms to ensure test results are captured
- **File System Errors**: Alternative storage options for test artifacts
- **CI/CD Integration Errors**: Multiple output formats to ensure pipeline compatibility

## Testing Strategy

The testing framework employs a dual testing approach combining unit testing and property-based testing to ensure comprehensive coverage and correctness validation.

### Unit Testing Approach
Unit tests will focus on:
- **Framework Components**: Testing individual classes like TestDataManager, ConfigurationManager
- **Step Definition Logic**: Validating step definition implementations with specific examples
- **API Client Functionality**: Testing REST Assured wrapper methods with mock responses
- **Error Scenarios**: Specific error conditions and edge cases
- **Configuration Loading**: Validation of configuration file parsing and environment setup

### Property-Based Testing Approach
Property-based tests will verify universal properties using **TestNG** as the property-based testing framework:
- **Minimum 100 iterations** per property test to ensure thorough validation
- **Smart test data generation** that creates realistic API test scenarios
- **Property test tagging** with explicit references to design document properties
- **Comprehensive input space coverage** for API request variations

**Property-Based Testing Library**: TestNG with custom data providers for generating test scenarios
**Test Configuration**: Each property-based test configured to run minimum 100 iterations
**Test Tagging Format**: Each property-based test tagged with comment: '**Feature: api-automation-testing, Property {number}: {property_text}**'

### Integration Testing
- **End-to-End API Workflows**: Complete user registration and login flows
- **Cross-Endpoint Dependencies**: Testing scenarios that involve multiple API calls
- **Environment Integration**: Validation against different test environments
- **Performance Validation**: Response time and throughput testing for API endpoints

### Test Data Strategy
- **Dynamic Test Data Generation**: Runtime creation of unique test data for each scenario
- **Data Isolation**: Each test scenario uses independent test data to avoid conflicts
- **Cleanup Automation**: Automatic removal of test data after scenario completion
- **Realistic Data Patterns**: Test data that mirrors production data characteristics