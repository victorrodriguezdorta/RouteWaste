/**
 * FacilityStatus
 *
 * Represents the current lifecycle state of a facility.
 *
 * Values:
 * - CANDIDATE: Facility is a candidate and not yet selected.
 * - PLANNED: Facility is planned but not yet open.
 * - OPEN: Facility is open and operational.
 * - DISCARDED: Facility has been discarded and cannot be used.
 */
export enum FacilityStatus {
  /** Facility is a candidate and not yet selected. */
  CANDIDATE = 'CANDIDATE',
  /** Facility is planned but not yet open. */
  PLANNED = 'PLANNED',
  /** Facility is open and operational. */
  OPEN = 'OPEN',
  /** Facility has been discarded and cannot be used. */
  DISCARDED = 'DISCARDED',
}

/**
 * Returns an array with all FacilityStatus values.
 * @returns {FacilityStatus[]} array with all allowed facility statuses.
 */
export function facilityStatusValues(): FacilityStatus[] {
  return Object.values(FacilityStatus) as FacilityStatus[];
}

/**
 * Parse a string into FacilityStatus.
 * @param {string} [s] String representation of the status.
 * @returns {FacilityStatus} matching facility status.
 * @throws {Error} if the provided string is undefined or invalid.
 */
export function facilityStatusFromString(s?: string): FacilityStatus {
  if (!s) throw new Error('Facility status is not defined');
  const key = s.trim().toUpperCase();
  for (const v of facilityStatusValues()) {
    if (v === key) return v;
  }
  throw new Error('Facility status is invalid');
}

/**
 * Return the ordinal index of a given status string.
 * @param {string} [s] String representation of the status.
 * @returns {number} ordinal index of the status.
 */
export function facilityStatusIndexOf(s?: string): number {
  return facilityStatusValues().indexOf(facilityStatusFromString(s));
}

/**
 * Check whether a string is a valid FacilityStatus.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isFacilityStatus(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of facilityStatusValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Return a random FacilityStatus (useful for testing).
 * @returns {FacilityStatus} random facility status.
 */
export function facilityStatusRandom(): FacilityStatus {
  const values = facilityStatusValues();
  return values[Math.floor(Math.random() * values.length)]!;
}

/**
 * Checks whether the given status allows service assignments.
 * @param {FacilityStatus} status Status to check.
 * @returns {boolean} True if containers can be assigned to the facility.
 */
export function facilityStatusAllowsServiceAssignments(status: FacilityStatus): boolean {
  return status === FacilityStatus.PLANNED || status === FacilityStatus.OPEN;
}

/**
 * Checks whether the given status is DISCARDED.
 * @param {FacilityStatus} status Status to check.
 * @returns {boolean} True if the facility is discarded.
 */
export function facilityStatusIsDiscarded(status: FacilityStatus): boolean {
  return status === FacilityStatus.DISCARDED;
}

/**
 * Returns the UI color for a given facility status.
 * @param {FacilityStatus} status - Facility status.
 * @returns {string} Color name suitable for Vuetify components.
 */
export function facilityStatusColor(status: FacilityStatus | string): string {
  const s = typeof status === 'string' ? facilityStatusFromString(status) : status;
  switch (s) {
    case FacilityStatus.CANDIDATE: return 'grey';
    case FacilityStatus.PLANNED: return 'orange';
    case FacilityStatus.OPEN: return 'green';
    case FacilityStatus.DISCARDED: return 'red';
    default: return 'grey';
  }
}

/**
 * Returns the options for a facility status selector.
 * @param {Function} t Translation function.
 * @returns {{ title: string; value: FacilityStatus }[]} array of options.
 */
export function facilityStatusToOptions(t: (key: string) => string): { title: string; value: FacilityStatus }[] {
  return facilityStatusValues().map((status) => ({
    title: t(`facility.add.statuses.${status}`),
    value: status,
  }));
}
