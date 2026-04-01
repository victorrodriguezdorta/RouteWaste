/**
 * ServiceZone
 *
 * Represents the different types of geographical service zones for waste collection.
 *
 * Values:
 * - NEIGHBORHOOD: Neighborhood-level service zone.
 * - DISTRICT: District-level service zone.
 * - GEOGRAPHICAL_AREA: Geographical area service zone.
 */
export enum ServiceZone {
  /** Neighborhood-level service zone. */
  NEIGHBORHOOD = 'NEIGHBORHOOD',
  /** District-level service zone. */
  DISTRICT = 'DISTRICT',
  /** Geographical area service zone. */
  GEOGRAPHICAL_AREA = 'GEOGRAPHICAL_AREA',
}

/**
 * Returns an array with all ServiceZone values.
 * @returns {ServiceZone[]} array with allowed service zones.
 */
export function serviceZoneValues(): ServiceZone[] {
  return Object.values(ServiceZone) as ServiceZone[];
}

/**
 * Parse a string into ServiceZone.
 * @param {string} [s] String representation of the service zone.
 * @returns {ServiceZone} matching service zone.
 * @throws {Error} if undefined or invalid.
 */
export function serviceZoneFromString(s?: string): ServiceZone {
  if (!s) throw new Error('Service zone is not defined');
  const key = s.trim().toUpperCase();
  for (const v of serviceZoneValues()) {
    if (v === key) return v;
  }
  throw new Error('Service zone is invalid');
}

/**
 * Returns the ordinal index of the given service zone string.
 * @param {string} [s] String representation of the service zone.
 * @returns {number} Ordinal index of the service zone.
 */
export function serviceZoneIndexOf(s?: string): number {
  return serviceZoneValues().indexOf(serviceZoneFromString(s));
}

/**
 * Checks whether the given string is a valid ServiceZone.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isServiceZone(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of serviceZoneValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Return a random ServiceZone (useful for testing).
 * @returns {ServiceZone} random service zone.
 */
export function serviceZoneRandom(): ServiceZone {
  const values = serviceZoneValues();
  return values[Math.floor(Math.random() * values.length)]!;
}

/**
 * Returns a comma-separated string of all allowed service zone values.
 * @returns {string} allowed values.
 */
export function serviceZoneAllowedValues(): string {
  return serviceZoneValues().join(', ');
}

/**
 * Returns the UI color for a given service zone.
 * @param {ServiceZone} zone - Service zone.
 * @returns {string} Color name suitable for Vuetify components.
 */
export function serviceZoneColor(zone: ServiceZone | string): string {
  const z = typeof zone === 'string' ? serviceZoneFromString(zone) : zone;
  switch (z) {
    case ServiceZone.NEIGHBORHOOD: return 'teal';
    case ServiceZone.DISTRICT: return 'indigo';
    case ServiceZone.GEOGRAPHICAL_AREA: return 'purple';
    default: return 'grey';
  }
}

/**
 * Returns the options for a service zone selector.
 * @param {Function} t Translation function.
 * @returns {{ title: string; value: ServiceZone }[]} array of options.
 */
export function serviceZoneToOptions(t: (key: string) => string): { title: string; value: ServiceZone }[] {
  return serviceZoneValues().map((zone) => ({
    title: t(`container.add.serviceZones.${zone}`),
    value: zone,
  }));
}
