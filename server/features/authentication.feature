Feature: Authentication APIs
  Test the signup and login endpoints for the e-commerce application

  Background:
    Given the API server is running on "http://localhost:8000"

  Scenario: User signup with valid credentials
    When user submits signup request with:
      | firstname | John      |
      | lastname  | Doe       |
      | username  | johndoe   |
      | email     | john@example.com |
      | password  | password123 |
      | phone     | 9876543210  |
    Then response status code should be 200
    And response should contain user object

  Scenario: User signup with existing username
    Given a user already exists with username "existinguser"
    When user submits signup request with:
      | firstname | Jane      |
      | lastname  | Smith     |
      | username  | existinguser |
      | email     | jane@example.com |
      | password  | password456 |
      | phone     | 9876543211  |
    Then response status code should be 401
    And response message should contain "username already exist"

  Scenario: User signup with existing email
    Given a user already exists with email "existing@example.com"
    When user submits signup request with:
      | firstname | Bob       |
      | lastname  | Wilson    |
      | username  | bobwilson |
      | email     | existing@example.com |
      | password  | password789 |
      | phone     | 9876543212  |
    Then response status code should be 500

  Scenario: User signup with missing required fields
    When user submits signup request with:
      | firstname | Tom       |
      | lastname  | Hardy     |
      | username  | tomhardy  |
      | email     | tom@example.com |
    Then response status code should be 500

  Scenario: User login with valid credentials
    Given a user exists with username "testuser" and password "testpass123"
    When user submits login request with:
      | username | testuser      |
      | password | testpass123   |
    Then response status code should be 200
    And response should contain user data

  Scenario: User login with invalid username
    When user submits login request with:
      | username | nonexistentuser |
      | password | anypassword     |
    Then response status code should be 401
    And response should contain "invalid login"

  Scenario: User login with invalid password
    Given a user exists with username "validuser" and password "correctpass"
    When user submits login request with:
      | username | validuser   |
      | password | wrongpass   |
    Then response status code should be 401
    And response should contain "invalid login"

  Scenario: User login with missing credentials
    When user submits login request with:
      | username | testuser |
    Then response status code should be 401
