import { FacilityVehicleSelection } from './facility-vehicle-selection';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

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
