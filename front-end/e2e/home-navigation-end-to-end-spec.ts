import { clickNavDrawerItem } from './helpers/navigation';
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

  test('navigates to containers from the home card', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Editar' }).nth(1).click();
    await expect(page).toHaveURL(/\/containers$/);
  });

  test('navigates to facilities from the home card', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Editar' }).nth(2).click();
    await expect(page).toHaveURL(/\/facilities$/);
  });

  test('navigates to algorithm from the home card', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Calcular Ruta' }).click();
    await expect(page).toHaveURL(/\/algorithm$/);
  });
});

test.describe('Navigation drawer', () => {
  test('opens the containers list from the side menu', async ({ page }) => {
    await page.goto('/');
    await clickNavDrawerItem(page, 'Contenedores');
    await expect(page).toHaveURL(/\/containers$/);
    await expect(page.getByRole('button', { name: 'Agregar Contenedor' })).toBeVisible();
  });

  test('opens the facilities list from the side menu', async ({ page }) => {
    await page.goto('/');
    await clickNavDrawerItem(page, 'Instalaciones');
    await expect(page).toHaveURL(/\/facilities$/);
    await expect(page.getByRole('button', { name: 'Agregar Instalación' })).toBeVisible();
  });

  test('opens the algorithm execution screen from the side menu', async ({ page }) => {
    await page.goto('/');
    await clickNavDrawerItem(page, 'Ejecutar algoritmo');
    await expect(page).toHaveURL(/\/algorithm\/execute$/);
  });

  test('returns to home from the side menu', async ({ page }) => {
    await page.goto('/vehicles');
    await clickNavDrawerItem(page, 'Menú');
    await expect(page).toHaveURL(/\/$/);
    await expect(page.getByText('Bienvenido a Route Waste')).toBeVisible();
  });
});
