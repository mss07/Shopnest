# API Automation Testing Framework

REST API automation testing framework using Cucumber BDD and REST Assured for the e-commerce application.

## Project Structure

```
├── pom.xml                                    # Maven configuration with dependencies
├── src/test/java/com/ecommerce/api/
│   ├── client/                               # REST API client classes
│   ├── config/                               # Configuration management
│   ├── models/                               # Data model classes
│   ├── runners/                              # Cucumber test runners
│   ├── stepdefinitions/                      # Cucumber step definitions
│   └── utils/                                # Utility classes and helpers
├── src/test/resources/
│   ├── config/                               # Environment-specific configurations
│   ├── features/                             # Cucumber feature files
│   └── logback-test.xml                      # Logging configuration
├── target/
│   ├── logs/                                 # Test execution logs
│   └── reports/                              # Test reports (HTML, JSON)
```

## Dependencies

- **Cucumber**: BDD framework for writing readable test scenarios
- **REST Assured**: Java library for REST API testing
- **TestNG**: Testing framework for property-based testing
- **Jackson**: JSON processing library
- **SLF4J + Logback**: Logging framework
- **Cucumber Reporting**: Enhanced HTML and JSON reports

## Maven Profiles

- `dev` (default): Development environment (localhost:8000)
- `test`: Test environment
- `staging`: Staging environment

## Getting Started

1. Ensure Java 11+ and Maven are installed
2. Run tests: `mvn test`
3. Run with specific profile: `mvn test -P test`
4. View reports in `target/reports/` directory

## Configuration

Environment-specific configurations are located in `src/test/resources/config/`:
- `application-dev.properties`
- `application-test.properties`
- `application-staging.properties`