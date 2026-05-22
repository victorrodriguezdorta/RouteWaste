import type { Locator } from '@playwright/test';

export type RowAction = 'view' | 'edit' | 'delete';

const actionIndex: Record<RowAction, number> = {
  view: 0,
  edit: 1,
  delete: 2,
};

export async function clickRowAction(row: Locator, action: RowAction): Promise<void> {
  await row.getByRole('button').nth(actionIndex[action]).click();
}
