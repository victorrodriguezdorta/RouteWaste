import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** Facility with selected vehicles for algorithm execution. */
export interface FacilityVehicleCommand {
  facilityId: UllUUID;
  selectedVehicleIds: UllUUID[];
}
