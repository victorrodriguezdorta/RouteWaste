import { skipWithoutBackend } from './helpers/backend';
import { createFacility, deleteEntity } from './helpers/entity-api';
import { findTableRow } from './helpers/paginated-list';
import { clickRowAction } from './helpers/table';
import { expect, test } from '@playwright/test';

test.describe('Facilities', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/facilities');
    await expect(page.getByRole('button', { name: 'Agregar Instalación' })).toBeVisible();
  });

  test('shows data table column headers', async ({ page }) => {
    await page.goto('/facilities');
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(
      page.getByRole('columnheader', { name: 'Tipo de Instalación' }),
    ).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/facilities');
    await page.getByRole('button', { name: 'Agregar Instalación' }).click();
    await expect(page).toHaveURL(/\/facilities\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('returns to the list when canceling create', async ({ page }) => {
    await page.goto('/facilities/add');
    await page.getByRole('button', { name: 'Cancelar' }).click();
    await expect(page).toHaveURL(/\/facilities$/);
  });

  test('shows a validation error when saving invalid data', async ({ page }) => {
    await page.goto('/facilities/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
  });

  test('opens detail view for a facility created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const facilityName = `Playwright Facility View ${Date.now()}`;
    const created = await createFacility(request, facilityName);

    try {
      const row = await findTableRow(page, '/facilities', facilityName);
      await clickRowAction(row, 'view');
      await expect(page).toHaveURL(new RegExp(`/facilities/${created.id}$`));
      await expect(page.getByLabel('Nombre')).toHaveValue(facilityName, { timeout: 15_000 });
    } finally {
      await deleteEntity(request, 'facilities', created.id);
    }
  });

  test('opens edit form from the list for a facility created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const facilityName = `Playwright Facility Edit ${Date.now()}`;
    const created = await createFacility(request, facilityName);

    try {
      const row = await findTableRow(page, '/facilities', facilityName);
      await clickRowAction(row, 'edit');
      await expect(page).toHaveURL(new RegExp(`/facilities/${created.id}/edit$`));
      await expect(page.getByLabel('Nombre')).toBeVisible();
    } finally {
      await deleteEntity(request, 'facilities', created.id);
    }
  });

  test('deletes via UI a facility created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const facilityName = `Playwright Facility ${Date.now()}`;
    await createFacility(request, facilityName);

    const row = await findTableRow(page, '/facilities', facilityName);
    await expect(row).toBeVisible();
    await clickRowAction(row, 'delete');
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
