import { i18n } from '@/adapter/vuejs/i18n';

export type StoreSuccessNotificationKey =
  | 'common.notifications.vehicleAdded'
  | 'common.notifications.vehicleUpdated'
  | 'common.notifications.vehicleDeleted'
  | 'common.notifications.containerAdded'
  | 'common.notifications.containerUpdated'
  | 'common.notifications.containerDeleted'
  | 'common.notifications.facilityAdded'
  | 'common.notifications.facilityUpdated'
  | 'common.notifications.facilityDeleted'
  | 'common.notifications.infrastructurePlanDeleted';

/**
 * Localized title and message for CRUD success snackbars in Pinia stores.
 */
export function resolveStoreSuccessNotification(
  messageKey: StoreSuccessNotificationKey
): { title: string; message: string } {
  return {
    title: i18n.global.t('common.errors.titles.success'),
    message: i18n.global.t(messageKey),
  };
}
