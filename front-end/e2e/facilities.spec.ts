import { expect, test } from '@playwright/test';
import { apiBaseUrl } from './helpers/api';
import { skipWithoutBackend } from './helpers/backend';
import { findTableRow } from './helpers/paginated-list';

test.describe('Facilities', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/facilities');
    await expect(page.getByRole('button', { name: 'Agregar Instalación' })).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/facilities');
    await page.getByRole('button', { name: 'Agregar Instalación' }).click();
    await expect(page).toHaveURL(/\/facilities\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('shows a validation error when saving invalid data', async ({ page }) => {
    await page.goto('/facilities/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
  });

  test('deletes via UI a facility created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const facilityName = `Playwright Facility ${Date.now()}`;
    const base = apiBaseUrl();

    const create = await request.post(`${base}facilities/`, {
      headers: { 'Content-Type': 'application/json' },
      data: {
        name: facilityName,
        facilityType: 'TRANSFER_STATION',
        location: {
          latitude: 28.47,
          longitude: -16.25,
          postalAddress: 'Avenida Test Playwright 10',
          gisReference: 'GIS-FACILITY-PW',
        },
        storageCapacity: { value: 10000 },
        processingCapacity: { value: 5000 },
        unloadingTime: { timeValue: 30 },
        openingFixedCost: { amount: 1500, currency: 'EUR' },
        status: 'CANDIDATE',
      },
    });
    expect(create.ok(), `POST facilities failed: ${create.status()}`).toBeTruthy();

    const row = await findTableRow(page, '/facilities', facilityName);
    await expect(row).toBeVisible();
    await row.getByRole('button').nth(2).click();
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
