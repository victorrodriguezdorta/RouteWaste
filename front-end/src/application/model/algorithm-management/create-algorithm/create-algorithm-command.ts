import type { FacilityVehicleCommand } from '@/adapter/http/request/algorithm/facility-vehicle-command';
import type { AlgorithmMaxBudgetCommand } from '@/application/model/algorithm-management/create-algorithm/algorithm-max-budget-command';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';
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
   * Time when the collection workday starts, expressed as "HH:mm"
   */
  collectionStartTime: string;

  /**
   * Average time in minutes a vehicle takes to move between two points
   */
  averageTransferTimeMinutes: number;

  /**
   * Greedy scoring weights for distance and fill used by the algorithm
   */
  greedyWeights: AlgorithmGreedyWeightsCommand;

  /**
   * Optional maximum budget for the algorithm execution. Currency code expected (e.g., 'EUR').
   */
  maxBudget?: AlgorithmMaxBudgetCommand;
}

/**
 * AlgorithmGreedyWeightsCommand
 *
 * Distance and fill weights used by the greedy scoring of the algorithm.
 * Both weights must add up to 1.
 */
export interface AlgorithmGreedyWeightsCommand {
  distanceWeight: number;
  fillWeight: number;
}
