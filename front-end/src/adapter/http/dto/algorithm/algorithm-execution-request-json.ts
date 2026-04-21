import type { CreateAlgorithmCommand, FacilityVehicleCommand } from '@/application/usecase/algorithm-management/create-algorithm/create-algorithm-command';

/**
 * FacilityVehicleJson
 *
 * JSON representation of a facility with its selected vehicles for HTTP communication.
 */
export interface FacilityVehicleJson {
  facilityId: string;
  selectedVehicleIds: string[];
}

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

  constructor(
    facilitiesWithVehicles: FacilityVehicleJson[],
    selectedContainerIds: string[],
    numberOfDays: number,
    averagePickupTimeMinutes: number
  ) {
    this.facilitiesWithVehicles = facilitiesWithVehicles;
    this.selectedContainerIds = selectedContainerIds;
    this.numberOfDays = numberOfDays;
    this.averagePickupTimeMinutes = averagePickupTimeMinutes;
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

    return new AlgorithmExecutionRequestJson(
      facilitiesWithVehicles,
      selectedContainerIds,
      data.numberOfDays,
      data.averagePickupTimeMinutes
    );
  }
}
