import { expect, test } from '@playwright/test';

test.describe('Home', () => {
  test('shows the main screen with all modules', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText('Bienvenido a Route Waste')).toBeVisible();
    await expect(page.getByText('Vehículos').first()).toBeVisible();
    await expect(page.getByText('Contenedores').first()).toBeVisible();
    await expect(page.getByText('Instalaciones').first()).toBeVisible();
    await expect(page.getByText('Algoritmo de Recogida').first()).toBeVisible();
  });

  test('navigates to vehicles from the home card', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Editar' }).first().click();
    await expect(page).toHaveURL(/\/vehicles$/);
  });
});
