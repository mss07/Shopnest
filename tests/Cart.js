import { test, expect } from '@playwright/test';

test('User should be able to add items in the cart', async ({ page }) => {
  await page.getByRole('link', { name: 'Headphones Minimum 50% Off' }).first().click();
  await page.getByRole('button', { name: 'Add to Cart' }).click();
  const itemName = page.getByText('boAt Rockerz 235v2 with ASAP');
  await expect(itemName).toBeVisible();
});
