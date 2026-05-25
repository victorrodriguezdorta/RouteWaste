import { skipWithoutBackend } from './helpers/backend';
import { createContainer, deleteEntity } from './helpers/entity-api';
import { findTableRow } from './helpers/paginated-list';
import { clickRowAction } from './helpers/table';
import { expect, test } from '@playwright/test';

test.describe('Containers', () => {
  test('shows the list view', async ({ page }) => {
    await page.goto('/containers');
    await expect(page.getByRole('button', { name: 'Agregar Contenedor' })).toBeVisible();
  });

  test('shows data table column headers', async ({ page }) => {
    await page.goto('/containers');
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(
      page.getByRole('columnheader', { name: 'Tipo de Residuo' }),
    ).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Ubicación' })).toBeVisible();
  });

  test('opens the create form', async ({ page }) => {
    await page.goto('/containers');
    await page.getByRole('button', { name: 'Agregar Contenedor' }).click();
    await expect(page).toHaveURL(/\/containers\/add$/);
    await expect(page.getByLabel('Nombre')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  });

  test('returns to the list when canceling create', async ({ page }) => {
    await page.goto('/containers/add');
    await page.getByRole('button', { name: 'Cancelar' }).click();
    await expect(page).toHaveURL(/\/containers$/);
  });

  test('shows a validation error when saving invalid data', async ({ page }) => {
    await page.goto('/containers/add');
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Error de Validación')).toBeVisible();
  });

  test('opens detail view for a container created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const containerName = `Playwright Container View ${Date.now()}`;
    const created = await createContainer(request, containerName);

    try {
      const row = await findTableRow(page, '/containers', containerName);
      await clickRowAction(row, 'view');
      await expect(page).toHaveURL(new RegExp(`/containers/${created.id}$`));
      await expect(page.getByLabel('Nombre')).toHaveValue(containerName, { timeout: 15_000 });
    } finally {
      await deleteEntity(request, 'containers', created.id);
    }
  });

  test('opens edit form from the list for a container created through the API', async ({
    page,
    request,
  }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const containerName = `Playwright Container Edit ${Date.now()}`;
    const created = await createContainer(request, containerName);

    try {
      const row = await findTableRow(page, '/containers', containerName);
      await clickRowAction(row, 'edit');
      await expect(page).toHaveURL(new RegExp(`/containers/${created.id}/edit$`));
      await expect(page.getByLabel('Nombre')).toBeVisible();
    } finally {
      await deleteEntity(request, 'containers', created.id);
    }
  });

  test('deletes via UI a container created through the API', async ({ page, request }) => {
    await skipWithoutBackend();
    test.setTimeout(90_000);

    const containerName = `Playwright Container ${Date.now()}`;
    await createContainer(request, containerName);

    const row = await findTableRow(page, '/containers', containerName);
    await expect(row).toBeVisible();
    await clickRowAction(row, 'delete');
    await expect(page.getByText('Confirmar Eliminación')).toBeVisible();
    await page.getByRole('dialog').getByRole('button', { name: 'Eliminar' }).click();
    await expect(row).toHaveCount(0, { timeout: 20_000 });
  });
});
