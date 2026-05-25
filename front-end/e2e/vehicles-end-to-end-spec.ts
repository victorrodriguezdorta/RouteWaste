import { skipWithoutBackend } from './helpers/backend';
import { createVehicle, deleteEntity } from './helpers/entity-api';
import { findTableRow } from './helpers/paginated-list';
import { clickRowAction } from './helpers/table';
import { expect, test } from '@playwright/test';

test.describe('Vehicles', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/vehicles');
    await expect(page.getByRole('button', { name: 'Agregar Vehículo' })).toBeVisible();
  });

  test('shows data table column headers', async ({ page }) => {
    await page.goto('/vehicles');
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(
      page.getByRole('columnheader', { name: 'Tipo de Vehículo' }),
    ).toBeVisible();
    await expect(
      page.getByRole('columnheader', { name: 'Costo por Km (EUR)' }),
    ).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/vehicles');
    await page.getByRole('button', { name: 'Agregar Vehículo' }).click();
    await expect(page).toHaveURL(/\/vehicles\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('returns to the list when canceling create', async ({ page }) => {
    await page.goto('/vehicles/add');
    await page.getByRole('button', { name: 'Cancelar' }).click();
    await expect(page).toHaveURL(/\/vehicles$/);
  });

  test('validates cost per kilometer on create', async ({ page }) => {
    await page.goto('/vehicles/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
    await expect(
      page.getByText('El costo por kilómetro debe ser mayor que 0'),
    ).toBeVisible();
  });

  test('opens detail view for a vehicle created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const vehicleName = `Playwright Vehicle View ${Date.now()}`;
    const created = await createVehicle(request, vehicleName);

    try {
      const row = await findTableRow(page, '/vehicles', vehicleName);
      await clickRowAction(row, 'view');
      await expect(page).toHaveURL(new RegExp(`/vehicles/${created.id}$`));
      await expect(page.getByLabel('Nombre')).toHaveValue(vehicleName, { timeout: 15_000 });
      await expect(page.getByRole('button', { name: 'Editar' })).toBeVisible();
    } finally {
      await deleteEntity(request, 'vehicles', created.id);
    }
  });

  test('opens edit form from the list for a vehicle created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const vehicleName = `Playwright Vehicle Edit ${Date.now()}`;
    const created = await createVehicle(request, vehicleName);

    try {
      const row = await findTableRow(page, '/vehicles', vehicleName);
      await clickRowAction(row, 'edit');
      await expect(page).toHaveURL(new RegExp(`/vehicles/${created.id}/edit$`));
      await expect(page.getByLabel('Nombre')).toBeVisible();
      await expect(page.getByRole('button', { name: 'Actualizar' })).toBeVisible();
    } finally {
      await deleteEntity(request, 'vehicles', created.id);
    }
  });

  test('deletes via UI a vehicle created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const vehicleName = `Playwright Vehicle ${Date.now()}`;
    await createVehicle(request, vehicleName);

    const row = await findTableRow(page, '/vehicles', vehicleName);
    await expect(row).toBeVisible();
    await clickRowAction(row, 'delete');
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
