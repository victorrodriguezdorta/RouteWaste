import type { Page } from '@playwright/test';

export async function clickNavDrawerItem(page: Page, label: string): Promise<void> {
  const item = page.getByRole('navigation').getByRole('option', { name: label });
  // Permanent Vuetify drawer: bounding box can be outside Playwright's viewport.
  await item.evaluate((node) => (node as HTMLElement).click());
}
