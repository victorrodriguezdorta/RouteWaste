import { expect, test } from '@playwright/test';
import { apiBaseUrl } from './helpers/api';
import { skipWithoutBackend } from './helpers/backend';
import { findTableRow } from './helpers/paginated-list';

test.describe('Containers', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/containers');
    await expect(page.getByRole('button', { name: 'Agregar Contenedor' })).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/containers');
    await page.getByRole('button', { name: 'Agregar Contenedor' }).click();
    await expect(page).toHaveURL(/\/containers\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('shows a validation error when saving invalid data', async ({ page }) => {
    await page.goto('/containers/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
  });

  test('deletes via UI a container created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const containerName = `Playwright Container ${Date.now()}`;
    const base = apiBaseUrl();

    const create = await request.post(`${base}containers/`, {
      headers: { 'Content-Type': 'application/json' },
      data: {
        name: containerName,
        location: {
          latitude: 28.4636,
          longitude: -16.2518,
          postalAddress: 'Calle Test Playwright 1',
          gisReference: 'GIS-PLAYWRIGHT-TEST',
        },
        wasteType: 'ORGANIC',
        capacityLiters: { liters: 1000 },
        dailyDemandLitersPerDay: { litersPerDay: 80 },
        serviceZone: 'NEIGHBORHOOD',
      },
    });
    expect(create.ok(), `POST containers failed: ${create.status()}`).toBeTruthy();

    const row = await findTableRow(page, '/containers', containerName);
    await expect(row).toBeVisible();
    await row.getByRole('button').nth(2).click();
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
