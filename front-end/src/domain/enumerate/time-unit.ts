/**
 * TimeUnit
 *
 * Represents the different time units used for temporal measurements.
 *
 * Values:
 * - DAY: Day time unit.
 * - WEEK: Week time unit.
 * - MONTH: Month time unit.
 * - YEAR: Year time unit.
 */
export enum TimeUnit {
  /** Day time unit. */
  DAY = 'DAY',
  /** Week time unit. */
  WEEK = 'WEEK',
  /** Month time unit. */
  MONTH = 'MONTH',
  /** Year time unit. */
  YEAR = 'YEAR',
}

/**
 * Returns an array with all TimeUnit values.
 * @returns {TimeUnit[]} array with allowed time units.
 */
export function timeUnitValues(): TimeUnit[] {
  return Object.values(TimeUnit) as TimeUnit[];
}

/**
 * Parse a string into TimeUnit.
 * @param {string} [s] String representation of the time unit.
 * @returns {TimeUnit} matching time unit.
 * @throws {Error} if undefined or invalid.
 */
export function timeUnitFromString(s?: string): TimeUnit {
  if (!s) throw new Error('Time unit is not defined');
  const key = s.trim().toUpperCase();
  for (const v of timeUnitValues()) {
    if (v === key) return v;
  }
  throw new Error('Invalid time unit');
}

/**
 * Checks whether the given string is a valid TimeUnit.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isTimeUnit(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of timeUnitValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Return a random TimeUnit (useful for testing).
 * @returns {TimeUnit} random time unit.
 */
export function timeUnitRandom(): TimeUnit {
  const values = timeUnitValues();
  return values[Math.floor(Math.random() * values.length)]!;
}

/**
 * Returns the options for a time unit selector.
 * @param {Function} t Translation function.
 * @returns {{ title: string; value: TimeUnit }[]} array of options.
 */
export function timeUnitToOptions(t: (key: string) => string): { title: string; value: TimeUnit }[] {
  return timeUnitValues().map((unit) => ({
    title: t(`common.timeUnits.${unit}`),
    value: unit,
  }));
}
