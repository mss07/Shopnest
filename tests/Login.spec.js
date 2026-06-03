// @ts-check
import { test, expect } from '@playwright/test';
import { faker } from '@faker-js/faker';
import LoginPage from './pages/loginpage.js';


test.beforeEach(async ({ page }) => {
  await page.goto('http://localhost:3000/');
});


test('check ShopNest page title', async ({ page }) => {
  // Assert that the tab title is exactly "ShopNest"
  await expect(page).toHaveTitle('ShopNest');
});


const fakeFirstName = faker.person.firstName();
const fakeLastName = faker.person.lastName();
const fakeUsername = faker.internet.username().toLowerCase();
const fakeEmail = faker.internet.email();
const fakePhone = faker.string.numeric('##########'); // 10-digit number
test('User should be able to create an account', async ({ page }) => {
  const Login = new LoginPage(page);

  await Login.signup({
    firstName: fakeFirstName,
    lastName: fakeLastName,
    username: fakeUsername,
    email: fakeEmail,
    password: '12345678',
    phone: fakePhone
  });

  await expect(Login.userProfile(fakeFirstName)).toBeVisible();
});


test('User should be able to login with valid email and password', async ({ page }) => {
  const Login = new LoginPage(page);

  await Login.login('rahul22', '12345678');
  await expect(Login.userProfile('Rahul')).toBeVisible();
});


test('User should not be able to login with invalid password', async ({ page }) => {
  const Login = new LoginPage(page);

  await Login.login('rahul22', '123456789');
  await expect(Login.userProfile('Rahul')).not.toBeVisible();
});




