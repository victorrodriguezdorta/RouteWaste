import { VehicleJsonResponse } from './vehicle-json-response';

/**
 * VehiclesResponse
 *
 * Alias type for an array of `VehicleJsonResponse` objects returned by the
 * backend when listing vehicles. Kept as a distinct type for clarity and
 * potential extension (pagination metadata) in the future.
 */
export type VehiclesResponse = VehicleJsonResponse[];
