import { skipWithoutBackend } from './helpers/backend';
import { expect, test } from '@playwright/test';

test.describe('Algorithm / infrastructure plans', () => {
  test('shows the plans list', async ({ page }) => {
    await page.goto('/algorithm');
    await expect(page).toHaveURL(/\/algorithm$/);
    await expect(
      page.getByRole('heading', { name: 'Algoritmo de Recogida' }),
    ).toBeVisible({ timeout: 15_000 });
    await expect(
      page
        .locator('.list-title-actions')
        .getByRole('button', { name: /Ejecutar algoritmo/i }),
    ).toBeVisible({ timeout: 15_000 });
  });

  test('shows infrastructure plan table headers', async ({ page }) => {
    await page.goto('/algorithm');
    await expect(
      page.getByRole('columnheader', { name: 'Fecha de Ejecución' }),
    ).toBeVisible({ timeout: 15_000 });
    await expect(
      page.getByRole('columnheader', { name: 'Costo Total Estimado' }),
    ).toBeVisible();
    await expect(
      page.getByRole('columnheader', { name: 'Estado del plan' }),
    ).toBeVisible();
  });

  test('opens the algorithm execution screen from the list action', async ({ page }) => {
    await page.goto('/algorithm');
    await page
      .locator('.list-title-actions')
      .getByRole('button', { name: /Ejecutar algoritmo/i })
      .click();
    await expect(page).toHaveURL(/\/algorithm\/execute$/);
    await expect(
      page.getByRole('heading', { name: /Ejecutar Algoritmo/i }),
    ).toBeVisible({ timeout: 15_000 });
  });

  test('shows the first step of the execution wizard', async ({ page }) => {
    await page.goto('/algorithm/execute');
    await expect(
      page.getByRole('heading', { name: /Ejecutar Algoritmo/i }),
    ).toBeVisible({ timeout: 15_000 });
    await expect(page.getByText('Instalaciones y Vehículos').first()).toBeVisible();
  });
});

test.describe('Algorithm / infrastructure plans with API', () => {
  test('loads the plans table when the back-end is available', async ({ page }) => {
    await skipWithoutBackend();
    await page.goto('/algorithm');
    await expect(page.getByRole('table')).toBeVisible({ timeout: 15_000 });
    const pagination = page.getByText(/\d+-\d+ of \d+/);
    await expect(pagination.or(page.getByText(/0-0 of 0/))).toBeVisible({
      timeout: 15_000,
    });
  });
});
