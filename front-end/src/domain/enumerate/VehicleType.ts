/**
 * VehicleType
 *
 * Represents the functional type of a vehicle within the system.
 *
 * Values:
 * - COLLECTION_TRUCK: Vehicle used for direct waste collection from containers.
 * - TRANSFER_TRUCK: Vehicle used to transport waste between facilities.
 * - SUPPORT_VEHICLE: Support vehicle used for auxiliary or operational tasks.
 */
export enum VehicleType {
  /** Vehicle used for direct waste collection from containers. */
  COLLECTION_TRUCK = 'COLLECTION_TRUCK',
  /** Vehicle used to transport waste between facilities. */
  TRANSFER_TRUCK = 'TRANSFER_TRUCK',
  /** Support vehicle used for auxiliary or operational tasks. */
  SUPPORT_VEHICLE = 'SUPPORT_VEHICLE',
}

/**
 * Returns an array with all VehicleType values.
 * @returns {VehicleType[]} array with allowed vehicle types.
 */
export function vehicleTypeValues(): VehicleType[] {
  return Object.values(VehicleType) as VehicleType[];
}

/**
 * Parse a string into VehicleType.
 * @param {string} [s] String representation of the type.
 * @returns {VehicleType} matching vehicle type.
 * @throws {Error} if undefined or invalid.
 */
export function vehicleTypeFromString(s?: string): VehicleType {
  if (!s) throw new Error('Vehicle type is not defined');
  const key = s.trim().toUpperCase();
  for (const v of vehicleTypeValues()) {
    if (v === key) return v;
  }
  throw new Error('Vehicle type is invalid');
}

/**
 * Returns the ordinal index of the given vehicle type string.
 * @param {string} [s] String representation of the type.
 * @returns {number} Ordinal index of the vehicle type.
 */
export function vehicleTypeIndexOf(s?: string): number {
  return vehicleTypeValues().indexOf(vehicleTypeFromString(s));
}

/**
 * Checks whether the given string is a valid VehicleType.
 * @param {string} [s] String to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
export function isVehicleType(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of vehicleTypeValues()) {
    if (v === key) return true;
  }
  return false;
}

/**
 * Return a random VehicleType (useful for testing).
 * @returns {VehicleType} random vehicle type.
 */
export function vehicleTypeRandom(): VehicleType {
  const values = vehicleTypeValues();
  return values[Math.floor(Math.random() * values.length)]!;
}
