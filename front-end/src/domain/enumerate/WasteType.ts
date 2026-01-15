/**
 * WasteType
 *
 * Represents the different types of waste for collection and recycling.
 *
 * Values:
 * - ORGANIC: Organic waste.
 * - PACKAGING: Packaging and containers.
 * - PAPER_CARDBOARD: Paper and cardboard.
 * - GLASS: Glass waste.
 * - RESIDUAL: Remaining general waste.
 */
export enum WasteType {
  /** Organic waste. */
  ORGANIC = 'ORGANIC',
  /** Packaging and containers. */
  PACKAGING = 'PACKAGING',
  /** Paper and cardboard. */
  PAPER_CARDBOARD = 'PAPER_CARDBOARD',
  /** Glass waste. */
  GLASS = 'GLASS',
  /** Remaining general waste. */
  RESIDUAL = 'RESIDUAL',
}

/**
 * Returns an array with all WasteType values.
 * @returns {WasteType[]} array with allowed waste types.
 */
export function wasteTypeValues(): WasteType[] {
  return Object.values(WasteType) as WasteType[];
}

/**
 * Parse a string into WasteType.
 * @param {string} [s] String representation of the waste type.
 * @returns {WasteType} matching waste type.
 * @throws {Error} if undefined or invalid.
 */
export function wasteTypeFromString(s?: string): WasteType {
  if (!s) throw new Error('Waste type is not defined');
  const key = s.trim().toUpperCase();
  for (const v of wasteTypeValues()) {
    if (v === key) return v;
  }
  throw new Error('Waste type is invalid');
}

/**
 * Returns the ordinal index of the given waste type string.
 * @param {string} [s] String representation of the waste type.
 * @returns {number} Ordinal index of the waste type.
 */
export function wasteTypeIndexOf(s?: string): number {
  return wasteTypeValues().indexOf(wasteTypeFromString(s));
}

/**
 * Checks whether the given string is a valid WasteType.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isWasteType(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of wasteTypeValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Return a random WasteType (useful for testing).
 * @returns {WasteType} random waste type.
 */
export function wasteTypeRandom(): WasteType {
  const values = wasteTypeValues();
  return values[Math.floor(Math.random() * values.length)]!;
}

/**
 * Returns a comma-separated string of all allowed waste type values.
 * @returns {string} allowed values.
 */
export function wasteTypeAllowedValues(): string {
  return wasteTypeValues().join(', ');
}
