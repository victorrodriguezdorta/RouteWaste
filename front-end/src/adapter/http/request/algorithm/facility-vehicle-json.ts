/**
 * JSON representation of a facility with its selected vehicles for HTTP communication.
 */
export interface FacilityVehicleJson {
  facilityId: string;
  selectedVehicleIds: string[];
}
