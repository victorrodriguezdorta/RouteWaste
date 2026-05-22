import { expect, test } from '@playwright/test';

test.describe('Algorithm / infrastructure plans', () => {
  test('shows the plans list', async ({ page }) => {
    await page.goto('/algorithm');
    await expect(page).toHaveURL(/\/algorithm$/);
    await expect(
      page.getByRole('heading', { name: 'Algoritmo de Recogida' }),
    ).toBeVisible({ timeout: 15_000 });
    await expect(
      page.getByRole('main').getByRole('button', { name: /Ejecutar algoritmo/i }),
    ).toBeVisible({ timeout: 15_000 });
  });

  test('opens the algorithm execution screen', async ({ page }) => {
    await page.goto('/algorithm');
    await page
      .getByRole('main')
      .getByRole('button', { name: /Ejecutar algoritmo/i })
      .click();
    await expect(page).toHaveURL(/\/algorithm\/execute$/);
    await expect(
      page.getByRole('heading', { name: /Ejecutar Algoritmo/i }),
    ).toBeVisible({ timeout: 15_000 });
  });
});
