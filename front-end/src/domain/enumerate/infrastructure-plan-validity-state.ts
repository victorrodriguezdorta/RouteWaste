/**
 * Aligns with backend {@code InfrastructurePlanValidityState}:
 * whether the plan still matches the master data used when it was produced.
 */
export enum InfrastructurePlanValidityState {
  VALID = 'VALID',
  OBSOLETE = 'OBSOLETE',
}

const VALUES = new Set<string>(Object.values(InfrastructurePlanValidityState));

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
