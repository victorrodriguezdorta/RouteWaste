import type { FacilityVehicleJson } from './facility-vehicle-json';
import type { FacilityVehicleCommand } from '@/adapter/http/dto/algorithm/facility-vehicle-command';
import type { CreateAlgorithmCommand } from '@/application/model/algorithm-management/create-algorithm/create-algorithm-command';

/**
 * AlgorithmExecutionRequestJson
 *
 * Represents the JSON body sent to the backend when executing the algorithm.
 * Contains primitive types only so it can be serialized directly to JSON.
 */
export class AlgorithmExecutionRequestJson {
  /**
   * Array of facilities with their respective selected vehicles
   */
  facilitiesWithVehicles: FacilityVehicleJson[];

  /**
   * Array of container IDs to include in the algorithm execution
   */
  selectedContainerIds: string[];

  /**
   * Number of days for the algorithm to plan deliveries
   */
  numberOfDays: number;

  /**
   * Average pickup time in minutes for collecting a container
   */
  averagePickupTimeMinutes: number;
  /**
   * Optional maximum budget to send to backend
   */
  maxBudget?: {
    amount: number;
    currency: string;
  };

  constructor(
    facilitiesWithVehicles: FacilityVehicleJson[],
    selectedContainerIds: string[],
    numberOfDays: number,
    averagePickupTimeMinutes: number
    , maxBudget?: { amount: number; currency: string }
  ) {
    this.facilitiesWithVehicles = facilitiesWithVehicles;
    this.selectedContainerIds = selectedContainerIds;
    this.numberOfDays = numberOfDays;
    this.averagePickupTimeMinutes = averagePickupTimeMinutes;
    this.maxBudget = maxBudget;
  }

  /**
   * Map a CreateAlgorithmCommand (domain input) into this DTO.
   * Converts UllUUID objects to string representations for JSON serialization.
   * @param data The CreateAlgorithmCommand containing UllUUID objects to convert.
   * @returns A new AlgorithmExecutionRequestJson instance with string values for serialization.
   */
  public static toRequest(data: CreateAlgorithmCommand): AlgorithmExecutionRequestJson {
    const facilitiesWithVehicles: FacilityVehicleJson[] = data.facilitiesWithVehicles.map(
      (facility: FacilityVehicleCommand) => ({
        facilityId: facility.facilityId.toString(),
        selectedVehicleIds: facility.selectedVehicleIds.map((id) => id.toString()),
      })
    );

    const selectedContainerIds = data.selectedContainerIds.map((id) => id.toString());

    const maxBudget = data.maxBudget ? { amount: data.maxBudget.amount, currency: data.maxBudget.currency } : undefined;

    return new AlgorithmExecutionRequestJson(
      facilitiesWithVehicles,
      selectedContainerIds,
      data.numberOfDays,
      data.averagePickupTimeMinutes,
      maxBudget
    );
  }
}
