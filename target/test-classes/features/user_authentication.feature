Feature: User Authentication API
  As a QA engineer
  I want to validate user authentication and registration REST APIs
  So that I can ensure user management HTTP endpoints work correctly

  Background:
    Given the API base URL is configured
    And the test environment is ready

  @user-registration @positive @smoke
  Scenario: Valid user registration with unique username
    Given I have valid user registration data
    When I send a POST request to "/signup" with the user data
    Then the response status code should be 200
    And the response should contain success message
    And the response should contain user data with proper structure
    And the user should be created successfully

  @user-registration @negative @regression
  Scenario: User registration with duplicate username
    Given a user already exists with username "testuser123"
    And I have registration data with username "testuser123"
    When I send a POST request to "/signup" with the duplicate username data
    Then the response status code should be 401
    And the response should contain error message about duplicate username
    And no new user should be created

  @user-registration @edge-case
  Scenario Outline: User registration with invalid data
    Given I have user registration data with <field> as "<value>"
    When I send a POST request to "/signup" with the invalid data
    Then the response status code should be 400
    And the response should contain validation error message
    
    Examples:
      | field    | value |
      | username | ""    |
      | password | ""    |
      | email    | ""    |
      | username | null  |
      | password | null  |
      | email    | null  |

  @user-registration @edge-case
  Scenario: User registration with malformed JSON
    Given I have malformed JSON data for user registration
    When I send a POST request to "/signup" with malformed JSON
    Then the response status code should be 400
    And the response should contain JSON parsing error message

  @user-login @positive @smoke
  Scenario: Valid user login with correct credentials
    Given a user exists with username "validuser" and password "validpass123"
    And I have valid login credentials for username "validuser" and password "validpass123"
    When I send a POST request to "/login" with the valid credentials
    Then the response status code should be 200
    And the response should contain user data JSON
    And the response should include authentication token or session data

  @user-login @negative @regression
  Scenario: User login with invalid username
    Given I have login credentials with username "nonexistentuser" and password "anypassword"
    When I send a POST request to "/login" with the invalid username
    Then the response status code should be 401
    And the response should contain error message about invalid credentials
    And no authentication token should be provided

  @user-login @negative
  Scenario: User login with invalid password
    Given a user exists with username "validuser" and password "validpass123"
    And I have login credentials with username "validuser" and password "wrongpassword"
    When I send a POST request to "/login" with the invalid password
    Then the response status code should be 401
    And the response should contain error message about invalid credentials
    And no authentication token should be provided

  @user-login @edge-case
  Scenario Outline: User login with empty or null credentials
    Given I have login credentials with username "<username>" and password "<password>"
    When I send a POST request to "/login" with the credentials
    Then the response status code should be 400
    And the response should contain validation error message
    
    Examples:
      | username | password |
      | ""       | "pass123"|
      | "user"   | ""       |
      | ""       | ""       |
      | null     | "pass123"|
      | "user"   | null     |

  @user-login @edge-case
  Scenario: User login with malformed JSON
    Given I have malformed JSON data for user login
    When I send a POST request to "/login" with malformed JSON
    Then the response status code should be 400
    And the response should contain JSON parsing error message

  @user-registration @security
  Scenario: User registration response structure validation
    Given I have valid user registration data
    When I send a POST request to "/signup" with the user data
    Then the response status code should be 200
    And the response JSON should contain "username" field
    And the response JSON should contain "email" field
    And the response JSON should not contain "password" field
    And the response JSON should have proper data types for all fields

  @user-login @security
  Scenario: User login response structure validation
    Given a user exists with username "testuser" and password "testpass123"
    And I have valid login credentials for username "testuser" and password "testpass123"
    When I send a POST request to "/login" with the valid credentials
    Then the response status code should be 200
    And the response JSON should contain user data
    And the response JSON should not contain "password" field
    And the response should have proper content-type header

  @api-error-handling
  Scenario: API endpoint error handling for non-existent endpoints
    Given I have valid user registration data
    When I send a POST request to "/invalid-endpoint" with the user data
    Then the response status code should be 404
    And the response should contain appropriate error message

  @api-performance
  Scenario: User registration response time validation
    Given I have valid user registration data
    When I send a POST request to "/signup" with the user data
    Then the response status code should be 200
    And the response time should be less than 5000 milliseconds

  @api-performance
  Scenario: User login response time validation
    Given a user exists with username "perfuser" and password "perfpass123"
    And I have valid login credentials for username "perfuser" and password "perfpass123"
    When I send a POST request to "/login" with the valid credentials
    Then the response status code should be 200
    And the response time should be less than 3000 milliseconds