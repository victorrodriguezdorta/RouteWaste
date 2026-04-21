import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * FacilityVehicleSelection
 *
 * DTO representing a facility and its selected vehicles for the stepper form.
 * Used in Vue.js for displaying and managing facility and vehicle selections.
 */
export class FacilityVehicleSelection {
  /**
   * ID of the facility
   */
  public facilityId: UllUUID;

  /**
   * Location coordinates of the facility
   */
  public facilityLocation: {
    latitude: number;
    longitude: number;
  };

  /**
   * Type of facility (e.g., OPERATIONAL_BASE, TRANSFER_STATION)
   */
  public facilityType: string;

  /**
   * Name or identifier of the facility for display
   */
  public facilityName: string;

  /**
   * IDs of vehicles selected for this facility
   */
  public selectedVehicleIds: UllUUID[];

  constructor(
    facilityId: UllUUID,
    latitude: number,
    longitude: number,
    facilityType: string,
    facilityName: string
  ) {
    this.facilityId = facilityId;
    this.facilityLocation = { latitude, longitude };
    this.facilityType = facilityType;
    this.facilityName = facilityName;
    this.selectedVehicleIds = [];
  }
}

/**
 * AlgorithmExecutionRequest
 *
 * DTO that stores all the data necessary for executing the delivery planning algorithm.
 * Used in Vue.js stepper for collecting and managing algorithm execution parameters.
 */
export class AlgorithmExecutionRequest {
  /**
   * Facilities with their selected vehicles
   */
  public facilitiesWithVehicles: FacilityVehicleSelection[];

  /**
   * IDs of containers selected for the algorithm
   */
  public selectedContainerIds: UllUUID[];

  /**
   * Number of days for the algorithm to plan deliveries
   */
  public numberOfDays: number;

  /**
   * Average time in minutes to pick up a container
   */
  public averagePickupTimeMinutes: number;

  constructor() {
    this.facilitiesWithVehicles = [];
    this.selectedContainerIds = [];
    this.numberOfDays = 1;
    this.averagePickupTimeMinutes = 0;
  }
}
