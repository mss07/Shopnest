# Cucumber Testing Framework Guide

## Overview

This guide explains how the Cucumber BDD (Behavior Driven Development) testing framework works in your e-commerce project, including integration with Supertest for API testing and Chai for assertions.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Test Execution Flow](#test-execution-flow)
3. [File Structure](#file-structure)
4. [Key Components](#key-components)
5. [Step-by-Step Example](#step-by-step-example)
6. [Testing Tools Integration](#testing-tools-integration)
7. [Best Practices](#best-practices)

## Architecture Overview

```mermaid
graph TB
    subgraph BDD["🎯 BDD Framework Layer"]
        FF["📄 Feature Files<br/>.feature"]:::feature
        SD["⚙️ Step Definitions<br/>.js"]:::stepdef
        HK["🔧 Hooks & Utils<br/>hooks.js"]:::support
    end
    
    subgraph TOOLS["🛠️ Testing Tools"]
        ST["📡 Supertest<br/>HTTP Client"]:::supertest
        CH["✅ Chai<br/>Assertions"]:::chai
    end
    
    subgraph APP["🚀 Application Layer"]
        RT["📂 Routes<br/>/signup, /login"]:::routes
        CT["🎮 Controllers<br/>usersignup, userlogin"]:::controller
        MD["📊 Middleware<br/>bodyParser, cors"]:::middleware
    end
    
    subgraph DB["🗄️ Data Layer"]
        MG["🔐 MongoDB<br/>User Collection"]:::database
    end
    
    FF -->|"Triggers"| SD
    SD -->|"Uses"| ST
    SD -->|"Uses"| CH
    HK -->|"Starts"| APP
    ST -->|"HTTP POST"| RT
    RT -->|"Routes to"| CT
    CT -->|"Query/Insert"| MG
    CT -->|"HTTP Response"| ST
    CH -->|"Validates"| SD
    
    linkStyle 0,1,2,3,4,5,6,7,8 stroke:#ff6b6b,stroke-width:3px,color:#ff6b6b
    
    classDef feature fill:#c8e6c9,stroke:#2e7d32,stroke-width:3px,color:#1b5e20,font-weight:bold
    classDef stepdef fill:#a5d6a7,stroke:#388e3c,stroke-width:3px,color:#1b5e20,font-weight:bold
    classDef support fill:#81c784,stroke:#43a047,stroke-width:2px,color:#fff,font-weight:bold
    classDef supertest fill:#ffe0b2,stroke:#f57f17,stroke-width:3px,color:#e65100,font-weight:bold
    classDef chai fill:#ffcc80,stroke:#fbc02d,stroke-width:3px,color:#f57f17,font-weight:bold
    classDef routes fill:#bbdefb,stroke:#1565c0,stroke-width:3px,color:#0d47a1,font-weight:bold
    classDef controller fill:#90caf9,stroke:#1976d2,stroke-width:3px,color:#0d47a1,font-weight:bold
    classDef middleware fill:#64b5f6,stroke:#1976d2,stroke-width:2px,color:#fff,font-weight:bold
    classDef database fill:#f8bbd0,stroke:#c2185b,stroke-width:3px,color:#880e4f,font-weight:bold
    
    style BDD fill:#e8f5e9,stroke:#2e7d32,stroke-width:3px
    style TOOLS fill:#fff3e0,stroke:#f57f17,stroke-width:3px
    style APP fill:#e3f2fd,stroke:#1565c0,stroke-width:3px
    style DB fill:#fce4ec,stroke:#c2185b,stroke-width:3px
```

## Test Execution Flow

```mermaid
graph TD
    A["🏁 npm test"]:::start
    A --> B["📖 Cucumber Reads<br/>authentication.feature"]:::read
    
    B --> C{{"🔧 BeforeAll Hook<br/>Initialize Test Environment"}}:::hook
    C --> C1["✅ Create Express App"]:::init
    C --> C2["✅ Setup Middleware<br/>bodyParser, cors"]:::init
    C --> C3["✅ Connect MongoDB"]:::init
    C --> C4["✅ Load Default Data"]:::init
    
    C4 --> D["📋 Parse Scenario<br/>Scenario 1/8"]:::scenario
    
    D --> E{{"🧹 Before Hook<br/>Cleanup Test Data"}}:::hook
    E --> E1["🗑️ Delete test users<br/>from MongoDB"]:::cleanup
    
    E1 --> F["⏩ Execute Given Steps"]:::given
    F --> F1["📍 Setup initial state<br/>Create test data"]:::given
    
    F1 --> G["⏩ Execute When Steps"]:::when
    G --> G1["🔨 Perform action<br/>Make HTTP request"]:::when
    G1 --> G2["📤 Send POST request<br/>via Supertest"]:::when
    
    G2 --> H["⏩ Execute Then Steps"]:::then
    H --> H1["✔️ Validate assertions<br/>Status code, response body"]:::then
    
    H1 --> I{{Check scenarios<br/>remaining?}}:::decision
    
    I -->|"Yes"| D
    I -->|"No"| J{{"🛑 AfterAll Hook<br/>Cleanup Resources"}}:::hook
    
    J --> J1["❌ Close Express Server"]:::cleanup
    J --> J2["❌ Disconnect MongoDB"]:::cleanup
    
    J2 --> K["✅ All Tests Complete"]:::finish
    K --> L["📊 Generate Reports<br/>HTML, JSON"]:::report
    
    classDef start fill:#4caf50,stroke:#2e7d32,stroke-width:3px,color:#fff,font-weight:bold
    classDef read fill:#2196f3,stroke:#1565c0,stroke-width:2px,color:#fff
    classDef hook fill:#ff9800,stroke:#f57f17,stroke-width:3px,color:#fff,font-weight:bold
    classDef init fill:#4db6ac,stroke:#00695c,stroke-width:2px,color:#fff
    classDef scenario fill:#9c27b0,stroke:#6a1b9a,stroke-width:2px,color:#fff,font-weight:bold
    classDef cleanup fill:#e64a19,stroke:#bf360c,stroke-width:2px,color:#fff
    classDef given fill:#673ab7,stroke:#512da8,stroke-width:2px,color:#fff,font-weight:bold
    classDef when fill:#3f51b5,stroke:#283593,stroke-width:2px,color:#fff,font-weight:bold
    classDef then fill:#00bcd4,stroke:#00838f,stroke-width:2px,color:#fff,font-weight:bold
    classDef decision fill:#e91e63,stroke:#880e4f,stroke-width:3px,color:#fff,font-weight:bold
    classDef finish fill:#4caf50,stroke:#2e7d32,stroke-width:3px,color:#fff,font-weight:bold
    classDef report fill:#ffc107,stroke:#ff8f00,stroke-width:2px,color:#000,font-weight:bold
```

## File Structure

```
server/
├── features/
│   ├── authentication.feature          # Gherkin scenarios
│   ├── step_definitions/
│   │   └── authSteps.js               # Step implementations
│   └── support/
│       ├── hooks.js                   # Test lifecycle hooks
│       └── apiHelper.js               # Utility functions
├── cucumber.js                        # Cucumber configuration
└── package.json                       # Dependencies and scripts
```

## Key Components

### 1. Feature Files (Gherkin)

**Purpose**: Define test scenarios in business-readable language

**Example**:
```gherkin
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
```

**Key Elements**:
- **Feature**: High-level description
- **Background**: Common setup for all scenarios
- **Scenario**: Individual test case
- **Given/When/Then**: BDD step types
- **Data Tables**: Structured test data

### 2. Step Definitions

**Purpose**: Implement the logic for each Gherkin step

**Structure**:
```javascript
import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from 'chai';
import request from 'supertest';

// Given steps - Setup initial state
Given('the API server is running on {string}', function(url) {
  apiUrl = url;
});

// When steps - Perform actions
When('user submits signup request with:', async function(dataTable) {
  const signupData = dataTable.rowsHash();
  response = await request(apiUrl)
    .post('/signup')
    .send(signupData);
});

// Then steps - Verify outcomes
Then('response status code should be {int}', function(expectedStatus) {
  expect(response.status).to.equal(expectedStatus);
});
```

### 3. Hooks (Test Lifecycle)

**Purpose**: Manage test setup and cleanup

```javascript
import { BeforeAll, AfterAll, Before } from '@cucumber/cucumber';

BeforeAll(async function() {
  // Start test server
  // Connect to database
  // Load default data
});

Before(async function() {
  // Clean up test data before each scenario
});

AfterAll(async function() {
  // Close server and cleanup resources
});
```

## Step-by-Step Example

Let's trace through the "User signup with valid credentials" scenario:

### Step 1: Test Initialization

```mermaid
graph LR
    A[BeforeAll Hook] --> B[Create Express App]
    B --> C[Setup Middleware]
    C --> D[Define Routes]
    D --> E[Connect to Database]
    E --> F[Start Server on Port 8000]
    F --> G[Load Default Data]
```

### Step 2: Scenario Setup

```javascript
// Before Hook runs before each scenario
Before(async function() {
  // Clean up any existing test users
  const testUsernames = ['johndoe', 'existinguser', 'testuser'];
  await User.deleteMany({ username: { $in: testUsernames } });
});
```

### Step 3: Execute Given Steps

```javascript
Given('the API server is running on {string}', function(url) {
  apiUrl = url; // Sets apiUrl = "http://localhost:8000"
});
```

### Step 4: Execute When Steps

```javascript
When('user submits signup request with:', async function(dataTable) {
  // 1. Parse the data table from Gherkin
  const signupData = dataTable.rowsHash();
  /* Result:
  {
    firstname: 'John',
    lastname: 'Doe',
    username: 'johndoe',
    email: 'john@example.com',
    password: 'password123',
    phone: '9876543210'
  }
  */
  
  // 2. Make HTTP request using Supertest
  response = await request(apiUrl)
    .post('/signup')           // POST to http://localhost:8000/signup
    .send(signupData)          // Send user data in request body
    .catch(err => err.response); // Handle any errors
  
  // 3. Store response for assertions
  responseBody = response.body;
});
```

### Step 5: Execute Then Steps

```javascript
Then('response status code should be {int}', function(expectedStatus) {
  // Use Chai to assert the HTTP status code
  expect(response.status).to.equal(expectedStatus); // expect(200).to.equal(200)
});

Then('response should contain user object', function() {
  // Multiple Chai assertions to verify response structure
  expect(responseBody).to.be.an('object');
  expect(responseBody.message).to.exist;
  
  const userData = responseBody.message;
  expect(userData).to.be.an('object');
  expect(userData.username).to.exist;
  expect(userData.email).to.exist;
});
```

## Testing Tools Integration

### Supertest - HTTP Testing

**Purpose**: Make HTTP requests to your API endpoints

```javascript
// Basic request structure
const response = await request('http://localhost:8000')
  .post('/signup')                    // HTTP method and endpoint
  .send({ username: 'test' })         // Request body
  .set('Authorization', 'Bearer ...')  // Headers (if needed)
  .expect(200);                       // Quick status assertion
```

**Key Features**:
- Makes real HTTP requests
- Supports all HTTP methods (GET, POST, PUT, DELETE)
- Can set headers, cookies, and request body
- Returns response object with status, body, headers

### Chai - Assertions

**Purpose**: Verify that responses match expectations

```javascript
// Different assertion styles
expect(response.status).to.equal(200);           // Equality
expect(responseBody).to.be.an('object');         // Type checking
expect(responseBody.message).to.exist;           // Property existence
expect(responseBody.username).to.include('john'); // String contains
expect(responseBody.users).to.have.length(5);    // Array length
```

**Common Patterns**:
- **Status Codes**: `expect(response.status).to.equal(200)`
- **Response Structure**: `expect(response.body).to.have.property('data')`
- **String Content**: `expect(message).to.include('error')`
- **Array Operations**: `expect(array).to.have.length.above(0)`

## Data Flow Visualization

```mermaid
graph TD
    A[Gherkin Scenario] --> B[Parse Data Table]
    B --> C[Extract User Data]
    C --> D[Supertest HTTP Request]
    D --> E[Express Route Handler]
    E --> F[Controller Logic]
    F --> G[Database Operation]
    G --> H[Return Response]
    H --> I[Supertest Response Object]
    I --> J[Store in Variables]
    J --> K[Chai Assertions]
    K --> L[Test Pass/Fail]
    
    style A fill:#e3f2fd
    style D fill:#f1f8e9
    style K fill:#fff3e0
    style L fill:#fce4ec
```

## Configuration Files

### cucumber.js
```javascript
export default {
  default: {
    require: ['features/step_definitions/**/*.js'],  // Step definition files
    require_module: ['features/support/hooks.js'],   // Hook files
    format: [
      'progress-bar',                                // Console output
      'html:cucumber-report.html',                   // HTML report
      'json:cucumber-report.json'                    // JSON report
    ],
    formatOptions: {
      snippetInterface: 'async-await'                // Use async/await syntax
    }
  }
};
```

### package.json Scripts
```json
{
  "scripts": {
    "test": "cucumber-js",
    "test:report": "cucumber-js --format html:test-results/cucumber-report.html"
  }
}
```

## Best Practices

### 1. Test Data Management
```javascript
// Clean slate for each test
Before(async function() {
  await User.deleteMany({ username: { $in: testUsernames } });
});

// Use unique identifiers
const userData = {
  username: `testuser_${Date.now()}`,
  email: `test_${Date.now()}@example.com`
};
```

### 2. Error Handling
```javascript
// Graceful error handling for negative test cases
response = await request(apiUrl)
  .post('/signup')
  .send(signupData)
  .catch(err => err.response); // Capture error responses for testing
```

### 3. Readable Assertions
```javascript
// Clear, descriptive assertions
expect(response.status, 'Signup should return 200 for valid data').to.equal(200);
expect(responseBody.message, 'Response should contain user object').to.exist;
```

### 4. Scenario Independence
- Each scenario should be independent
- Use Before hooks to ensure clean state
- Don't rely on data from previous scenarios

## Running Tests

```bash
# Run all tests
npm test

# Run with HTML report
npm run test:report

# Run specific feature
npx cucumber-js features/authentication.feature

# Run with specific tags (if using @tags)
npx cucumber-js --tags "@signup"
```

## Common Patterns

### Testing Success Cases
```gherkin
Scenario: Successful operation
  Given initial state is set up
  When user performs valid action
  Then system responds with success
  And response contains expected data
```

### Testing Error Cases
```gherkin
Scenario: Invalid input handling
  Given system is ready
  When user provides invalid input
  Then system responds with error code
  And error message explains the problem
```

### Testing Edge Cases
```gherkin
Scenario: Boundary conditions
  Given specific conditions exist
  When edge case is triggered
  Then system handles it gracefully
```

This framework provides a robust foundation for API testing that's both human-readable and technically comprehensive.