// @ts-check
import { test, expect } from '@playwright/test';
import { faker } from '@faker-js/faker';


test('check ShopNest page title', async ({ page }) => {
  await page.goto('http://localhost:3000');

  // 2. Assert that the tab title is exactly "ShopNest"
  await expect(page).toHaveTitle('ShopNest');
});


const fakeFirstName = faker.person.firstName();
const fakeLastName = faker.person.lastName();
const fakeUsername = faker.internet.username().toLowerCase();
const fakeEmail = faker.internet.email();
const fakePhone = faker.string.numeric('##########'); // 10-digit number
test('User should be able to create an account', async ({ page }) => {

  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByText('New to flipkart? create').click();
  await page.getByRole('textbox', { name: 'Enter first name' }).fill(fakeFirstName);
  await page.getByRole('textbox', { name: 'Enter last name' }).fill(fakeLastName);
  await page.getByRole('textbox', { name: 'Enter username' }).fill(fakeUsername);
  await page.getByRole('textbox', { name: 'Enter Email' }).fill(fakeEmail);
  await page.getByRole('textbox', { name: 'Enter password' }).fill('12345678');
  await page.getByRole('textbox', { name: 'Enter phone' }).fill(fakePhone);
  await page.locator("//button[normalize-space()='Continue']").click();

  const firstname = page.locator(`//p[normalize-space()='${fakeFirstName}']`);
  await expect(firstname).toBeVisible();
});

test('User should be able to login with valid email and password', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByRole('textbox', { name: 'Enter username' }).fill(`rahul22`);
  await page.getByRole('textbox', { name: 'password' }).fill('12345678');
  await page.getByRole('button', { name: 'Login' }).click();
  const firstname = page.locator(`//p[normalize-space()='Rahul']`);
  await expect(firstname).toBeVisible();
});

test('User should not be able to login with invalid password', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByRole('textbox', { name: 'Enter username' }).fill(`rahul22`);
  await page.getByRole('textbox', { name: 'password' }).fill('123456789');
  await page.getByRole('button', { name: 'Login' }).click();
  const firstname = page.locator(`//p[normalize-space()='Rahul']`);
  await expect(firstname).not.toBeVisible();
});


test('User should be able to add items in the cart', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('link', { name: 'Headphones Minimum 50% Off' }).first().click();
  await page.getByRole('button', { name: 'Add to Cart' }).click();
  const itemName = page.getByText('boAt Rockerz 235v2 with ASAP');
  await expect(itemName).toBeVisible();
});


