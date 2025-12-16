import { Given, When, Then, Before, After } from '@cucumber/cucumber';
import { expect } from 'chai';
import request from 'supertest';

let apiUrl;
let response;
let responseBody;
let testUserData = {};

// Store current test data
const testData = {
  signupData: {},
  loginData: {},
  existingUsers: {}
};

Given('the API server is running on {string}', function(url) {
  apiUrl = url;
});

Given('a user already exists with username {string}', async function(username) {
  // Create a test user with this username
  const userData = {
    firstname: 'Test',
    lastname: 'User',
    username: username,
    email: `${username}@example.com`,
    password: 'testpass123',
    phone: '9876543210'
  };
  
  try {
    await request(apiUrl)
      .post('/signup')
      .send(userData);
    
    testData.existingUsers[username] = userData;
  } catch (error) {
    console.log(`User ${username} might already exist`);
  }
});

Given('a user already exists with email {string}', async function(email) {
  // Create a test user with this email
  const userData = {
    firstname: 'Test',
    lastname: 'User',
    username: `user_${Date.now()}`,
    email: email,
    password: 'testpass123',
    phone: '9876543210'
  };
  
  try {
    await request(apiUrl)
      .post('/signup')
      .send(userData);
    
    testData.existingUsers[email] = userData;
  } catch (error) {
    console.log(`User with email ${email} might already exist`);
  }
});

Given('a user exists with username {string} and password {string}', async function(username, password) {
  const userData = {
    firstname: 'Test',
    lastname: 'User',
    username: username,
    email: `${username}@example.com`,
    password: password,
    phone: '9876543210'
  };
  
  try {
    const signupResponse = await request(apiUrl)
      .post('/signup')
      .send(userData);
    
    console.log(`User ${username} created with response status: ${signupResponse.status}`);
    testData.existingUsers[username] = userData;
  } catch (error) {
    // User might already exist, which is fine for this step
    console.log(`User ${username} already exists or error occurred: ${error.message}`);
  }
});

When('user submits signup request with:', async function(dataTable) {
  const signupData = dataTable.rowsHash();
  
  testData.signupData = signupData;
  
  response = await request(apiUrl)
    .post('/signup')
    .send(signupData)
    .catch(err => err.response);
  
  responseBody = response.body;
});

When('user submits login request with:', async function(dataTable) {
  const loginData = dataTable.rowsHash();
  
  testData.loginData = loginData;
  
  // Add a small delay to ensure database is ready
  await new Promise(resolve => setTimeout(resolve, 100));
  
  response = await request(apiUrl)
    .post('/login')
    .send(loginData)
    .catch(err => err.response);
  
  responseBody = response.body;
  
  console.log(`Login attempt for ${loginData.username}: status ${response.status}, response: ${JSON.stringify(responseBody)}`);
});

Then('response status code should be {int}', function(expectedStatus) {
  expect(response.status).to.equal(expectedStatus);
});

Then('response should contain user object', function() {
  expect(responseBody).to.be.an('object');
  expect(responseBody.message).to.exist;
  
  // Verify the response contains user data
  const userData = responseBody.message;
  expect(userData).to.be.an('object');
  expect(userData.username).to.exist;
  expect(userData.email).to.exist;
});

Then('response should contain user data', function() {
  expect(responseBody).to.be.an('object');
  expect(responseBody.data).to.exist;
  
  const userData = responseBody.data;
  expect(userData).to.be.an('object');
  expect(userData.username).to.equal(testData.loginData.username);
});

Then('response message should contain {string}', function(expectedMessage) {
  const message = typeof responseBody.message === 'string' 
    ? responseBody.message 
    : JSON.stringify(responseBody.message);
  
  expect(message).to.include(expectedMessage);
});

Then('response should contain {string}', function(expectedMessage) {
  const bodyString = typeof responseBody === 'string' 
    ? responseBody 
    : JSON.stringify(responseBody);
  
  expect(bodyString).to.include(expectedMessage);
});
