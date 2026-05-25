import { enumToLocaleKey } from '@/domain/util/enum-to-locale-key';

/** Returns the locale key segment for a vehicle type enum value. */
export function vehicleTypeLocaleKey(type: VehicleType | string): string {
  const value = typeof type === 'string' ? vehicleTypeFromString(type) : type;
  return enumToLocaleKey(value);
}

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

/**
 * Returns the options for a vehicle type selector.
 * @param {Function} t Translation function.
 * @returns {{ title: string; value: VehicleType }[]} array of options.
 */
export function vehicleTypeToOptions(t: (key: string) => string): { title: string; value: VehicleType }[] {
  return vehicleTypeValues().map((type) => ({
    title: t(`vehicle.add.vehicleTypes.${vehicleTypeLocaleKey(type)}`),
    value: type,
  }));
}

/**
 * Returns the UI color for a given vehicle type.
 * @param {VehicleType} type - Vehicle type.
 * @returns {string} Color name suitable for Vuetify components.
 */
export function vehicleTypeColor(type: VehicleType | string): string {
  const t = typeof type === 'string' ? vehicleTypeFromString(type) : type;
  switch (t) {
    case VehicleType.COLLECTION_TRUCK: return 'blue';
    case VehicleType.TRANSFER_TRUCK: return 'green';
    case VehicleType.SUPPORT_VEHICLE: return 'orange';
    default: return 'grey';
  }
}
