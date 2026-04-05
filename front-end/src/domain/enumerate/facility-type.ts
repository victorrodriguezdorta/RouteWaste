/**
 * FacilityType
 *
 * Represents the different types of waste management facilities.
 *
 * Values:
 * - OPERATIONAL_BASE: Operational base for waste collection vehicles and operations.
 * - TRANSFER_STATION: Transfer station for temporary waste storage and distribution.
 * - TREATMENT_PLANT: Treatment plant for waste processing and final disposal.
 */
export enum FacilityType {
  /** Operational base for waste collection vehicles and operations. */
  OPERATIONAL_BASE = 'OPERATIONAL_BASE',
  /** Transfer station for temporary waste storage and distribution. */
  TRANSFER_STATION = 'TRANSFER_STATION',
  /** Treatment plant for waste processing and final disposal. */
  TREATMENT_PLANT = 'TREATMENT_PLANT',
}

/**
 * Returns an array with all FacilityType values.
 * @returns {FacilityType[]} array with allowed facility types.
 */
export function facilityTypeValues(): FacilityType[] {
  return Object.values(FacilityType) as FacilityType[];
}

/**
 * Parse a string into FacilityType.
 * @param {string} [s] String representation of the type.
 * @returns {FacilityType} matching facility type.
 * @throws {Error} if undefined or invalid.
 */
export function facilityTypeFromString(s?: string): FacilityType {
  if (!s) throw new Error('Facility type is not defined');
  const key = s.trim().toUpperCase();
  for (const v of facilityTypeValues()) {
    if (v === key) return v;
  }
  throw new Error('Facility type is invalid');
}

/**
 * Returns the ordinal index of the given facility type string.
 * @param {string} [s] String representation of the facility type.
 * @returns {number} Ordinal index of the facility type.
 */
export function facilityTypeIndexOf(s?: string): number {
  return facilityTypeValues().indexOf(facilityTypeFromString(s));
}

/**
 * Checks whether the given string is a valid FacilityType.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isFacilityType(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of facilityTypeValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Returns a random FacilityType. Useful for testing purposes.
 * @returns {FacilityType} Random facility type.
 */
export function facilityTypeRandom(): FacilityType {
  const values = facilityTypeValues();
  return values[Math.floor(Math.random() * values.length)]!;
}
/**
 * Returns the UI color for a given facility type.
 * @param {FacilityType} type - Facility type.
 * @returns {string} Color name suitable for Vuetify components.
 */
export function facilityTypeColor(type: FacilityType | string): string {
  const t = typeof type === 'string' ? facilityTypeFromString(type) : type;
  switch (t) {
    case FacilityType.OPERATIONAL_BASE: return 'blue';
    case FacilityType.TRANSFER_STATION: return 'orange';
    case FacilityType.TREATMENT_PLANT: return 'green';
    default: return 'grey';
  }
}

/**
 * Returns the options for a facility type selector.
 * @param {Function} t Translation function.
 * @returns {{ title: string; value: FacilityType }[]} array of options.
 */
export function facilityTypeToOptions(t: (key: string) => string): { title: string; value: FacilityType }[] {
  return facilityTypeValues().map((type) => ({
    title: t(`facility.add.facilityTypes.${type}`),
    value: type,
  }));
}