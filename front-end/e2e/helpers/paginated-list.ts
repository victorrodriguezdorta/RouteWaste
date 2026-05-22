import { expect, type Locator, type Page } from '@playwright/test';

/** Opens a CRUD list and walks pages until a row with the given text appears. */
export async function findTableRow(
  page: Page,
  listPath: string,
  rowText: string,
): Promise<Locator> {
  const row = page.getByRole('row').filter({ hasText: rowText });
  const nextPage = page.getByRole('button', { name: 'Next page' });

  await page.goto(listPath, { waitUntil: 'domcontentloaded' });
  await page.waitForLoadState('load');

  for (let pageIndex = 0; pageIndex < 20; pageIndex++) {
    try {
      await expect(row.first()).toBeVisible({ timeout: 5_000 });
      return row.first();
    } catch {
      if (await nextPage.isDisabled()) {
        break;
      }
      await nextPage.click();
      await page.waitForLoadState('load');
    }
  }

  await expect(row.first()).toBeVisible({ timeout: 10_000 });
  return row.first();
}
