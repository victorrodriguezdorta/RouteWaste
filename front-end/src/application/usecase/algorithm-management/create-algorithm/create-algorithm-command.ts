import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * FacilityVehicleCommand
 *
 * Represents a facility with its selected vehicles for algorithm execution.
 */
export interface FacilityVehicleCommand {
  facilityId: UllUUID;
  selectedVehicleIds: UllUUID[];
}

/**
 * CreateAlgorithmCommand
 *
 * Command object for the algorithm creation use case.
 * Contains all necessary data to execute the delivery planning algorithm.
 */
export interface CreateAlgorithmCommand {
  /**
   * Array of facilities with their respective selected vehicles
   */
  facilitiesWithVehicles: FacilityVehicleCommand[];

  /**
   * Array of container IDs to include in the algorithm execution
   */
  selectedContainerIds: UllUUID[];

  /**
   * Number of days for the algorithm to plan deliveries
   */
  numberOfDays: number;

  /**
   * Average pickup time in minutes for collecting a container
   */
  averagePickupTimeMinutes: number;
  /**
   * Optional maximum budget for the algorithm execution. Currency code expected (e.g., 'EUR').
   */
  maxBudget?: {
    amount: number;
    currency: string;
  };
}
