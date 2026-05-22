import { expect, test } from '@playwright/test';
import { apiBaseUrl } from './helpers/api';
import { skipWithoutBackend } from './helpers/backend';
import { findTableRow } from './helpers/paginated-list';

test.describe('Vehicles', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/vehicles');
    await expect(page.getByRole('button', { name: 'Agregar Vehículo' })).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/vehicles');
    await page.getByRole('button', { name: 'Agregar Vehículo' }).click();
    await expect(page).toHaveURL(/\/vehicles\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('validates cost per kilometer on create', async ({ page }) => {
    await page.goto('/vehicles/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
    await expect(
      page.getByText('El costo por kilómetro debe ser mayor que 0'),
    ).toBeVisible();
  });

  test('deletes via UI a vehicle created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const vehicleName = `Playwright Vehicle ${Date.now()}`;
    const base = apiBaseUrl();

    const create = await request.post(`${base}vehicles/`, {
      headers: { 'Content-Type': 'application/json' },
      data: {
        name: vehicleName,
        vehicleType: 'COLLECTION_TRUCK',
        capacityKilograms: 5000,
        CapacityLiters: 12000,
        costPerKilometer: { amount: 0.45, currency: 'EUR' },
      },
    });
    expect(create.ok(), `POST vehicles failed: ${create.status()}`).toBeTruthy();

    const row = await findTableRow(page, '/vehicles', vehicleName);
    await expect(row).toBeVisible();
    await row.getByRole('button').nth(2).click();
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
