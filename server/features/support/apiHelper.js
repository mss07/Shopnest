import request from 'supertest';
import { expect } from 'chai';

/**
 * API Helper utilities for testing with Supertest
 */

export class APIHelper {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Make a POST request to the API
   * @param {string} endpoint - The API endpoint
   * @param {object} data - Request body data
   * @returns {Promise} - Response object
   */
  async postRequest(endpoint, data) {
    try {
      const response = await request(this.baseUrl)
        .post(endpoint)
        .send(data);
      return response;
    } catch (error) {
      return error.response || error;
    }
  }

  /**
   * Make a GET request to the API
   * @param {string} endpoint - The API endpoint
   * @returns {Promise} - Response object
   */
  async getRequest(endpoint) {
    try {
      const response = await request(this.baseUrl)
        .get(endpoint);
      return response;
    } catch (error) {
      return error.response || error;
    }
  }

  /**
   * Signup a new user
   * @param {object} userData - User data for signup
   * @returns {Promise} - Response object
   */
  async signup(userData) {
    return this.postRequest('/signup', userData);
  }

  /**
   * Login a user
   * @param {object} credentials - Login credentials (username, password)
   * @returns {Promise} - Response object
   */
  async login(credentials) {
    return this.postRequest('/login', credentials);
  }

  /**
   * Validate response status code
   * @param {object} response - Response object
   * @param {number} expectedStatus - Expected status code
   */
  validateStatusCode(response, expectedStatus) {
    expect(response.status).to.equal(expectedStatus);
  }

  /**
   * Validate response contains specific field
   * @param {object} response - Response object
   * @param {string} field - Field name to check
   */
  validateFieldExists(response, field) {
    expect(response.body).to.have.property(field);
  }

  /**
   * Validate response body equals expected value
   * @param {object} response - Response object
   * @param {string} field - Field name
   * @param {*} expectedValue - Expected value
   */
  validateFieldValue(response, field, expectedValue) {
    expect(response.body[field]).to.equal(expectedValue);
  }

  /**
   * Extract data from response
   * @param {object} response - Response object
   * @param {string} field - Field to extract
   * @returns {*} - Extracted value
   */
  extractFromResponse(response, field) {
    return response.body[field];
  }
}

export default APIHelper;
