const CONTAINER_STATUS_I18N_PREFIX = 'infrastructurePlan.show.daily.monitoring.statuses';

/**
 * ContainerStatus
 *
 * Represents the monitoring status of a container for a given plan day.
 * Values mirror backend Java enum used by the API.
 */
export enum ContainerStatus {
  /** Container state is correct (no overflow). */
  CORRECT = 'CORRECT',
  /** Container has overflowed. */
  OVERFLOWED = 'OVERFLOWED',
}

export function containerStatusValues(): ContainerStatus[] {
  return Object.values(ContainerStatus) as ContainerStatus[];
}

export function containerStatusFromString(s?: string): ContainerStatus {
  if (!s) throw new Error('Container status is not defined');
  const key = s.trim().toUpperCase();
  for (const v of containerStatusValues()) {
    if (v === key) return v;
  }
  throw new Error('Container status is invalid');
}

export function isContainerStatus(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of containerStatusValues()) {
    if (v === key) return true;
  }
  return false;
}

/** Locale key segment for monitoring UI labels (maps CORRECT to "ok"). */
export function containerStatusLocaleKey(status: ContainerStatus): string {
  switch (status) {
    case ContainerStatus.OVERFLOWED:
      return 'overflowed';
    case ContainerStatus.CORRECT:
    default:
      return 'ok';
  }
}

/**
 * Returns the translated monitoring label for a container status.
 * Missing or unknown values resolve to the "no data" label.
 */
export function containerStatusLabel(
  t: (key: string) => string,
  status: ContainerStatus | string | null | undefined,
): string {
  if (status == null || (typeof status === 'string' && status.trim() === '')) {
    return t(`${CONTAINER_STATUS_I18N_PREFIX}.noData`);
  }

  const value = typeof status === 'string'
    ? (isContainerStatus(status) ? containerStatusFromString(status) : null)
    : status;

  if (!value) {
    return t(`${CONTAINER_STATUS_I18N_PREFIX}.noData`);
  }

  return t(`${CONTAINER_STATUS_I18N_PREFIX}.${containerStatusLocaleKey(value)}`);
}

/**
 * Returns the Vuetify color for a container monitoring status chip.
 * Missing or unknown values use a neutral tone.
 */
export function containerStatusColor(status: ContainerStatus | string | null | undefined): string {
  if (status == null || (typeof status === 'string' && status.trim() === '')) {
    return 'grey';
  }

  const value = typeof status === 'string'
    ? (isContainerStatus(status) ? containerStatusFromString(status) : null)
    : status;

  if (!value) {
    return 'grey';
  }

  switch (value) {
    case ContainerStatus.OVERFLOWED:
      return 'error';
    case ContainerStatus.CORRECT:
    default:
      return 'success';
  }
}
