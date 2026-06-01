/**
 * Aligns with backend {@code InfrastructurePlanValidityState}:
 * running, up to date, or obsolete relative to master data.
 */
export enum InfrastructurePlanValidityState {
  RUNNING = 'RUNNING',
  VALID = 'VALID',
  OBSOLETE = 'OBSOLETE',
}

const VALUES = new Set<string>(Object.values(InfrastructurePlanValidityState));

const INFRASTRUCTURE_PLAN_VALIDITY_I18N_PREFIX = 'infrastructurePlan.list.table.validity';

/**
 * Parses API / persistence string into {@link InfrastructurePlanValidityState}.
 * Unknown or missing values default to {@link InfrastructurePlanValidityState.VALID}.
 */
export function infrastructurePlanValidityStateFromString(
  raw: string | undefined | null,
): InfrastructurePlanValidityState {
  if (raw == null || typeof raw !== 'string') {
    return InfrastructurePlanValidityState.VALID;
  }
  const normalized = raw.trim().toUpperCase();
  if (VALUES.has(normalized)) {
    return normalized as InfrastructurePlanValidityState;
  }
  return InfrastructurePlanValidityState.VALID;
}

/** Locale key segment under validity.* in i18n JSON. */
export function infrastructurePlanValidityStateLocaleKey(
  state: InfrastructurePlanValidityState,
): string {
  switch (state) {
    case InfrastructurePlanValidityState.RUNNING:
      return 'running';
    case InfrastructurePlanValidityState.OBSOLETE:
      return 'obsolete';
    case InfrastructurePlanValidityState.VALID:
    default:
      return 'valid';
  }
}

/**
 * Returns the translated label for an infrastructure plan validity state.
 */
export function infrastructurePlanValidityStateLabel(
  t: (key: string) => string,
  state: InfrastructurePlanValidityState | string | null | undefined,
): string {
  const value = typeof state === 'string'
    ? infrastructurePlanValidityStateFromString(state)
    : (state ?? InfrastructurePlanValidityState.VALID);

  return t(
    `${INFRASTRUCTURE_PLAN_VALIDITY_I18N_PREFIX}.${infrastructurePlanValidityStateLocaleKey(value)}`,
  );
}

/**
 * Returns the Vuetify color for an infrastructure plan validity state chip.
 */
export function infrastructurePlanValidityStateColor(
  state: InfrastructurePlanValidityState | string,
): string {
  const value = typeof state === 'string'
    ? infrastructurePlanValidityStateFromString(state)
    : state;

  switch (value) {
    case InfrastructurePlanValidityState.RUNNING:
      return 'warning';
    case InfrastructurePlanValidityState.OBSOLETE:
      return 'error';
    case InfrastructurePlanValidityState.VALID:
    default:
      return 'success';
  }
}
