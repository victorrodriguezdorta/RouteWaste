/**
 * CreateAlgorithmResult
 *
 * Result object for the algorithm execution use case.
 * Contains the processed data returned by the backend after resolving all
 * selected identifiers.
 */
export interface CreateAlgorithmResult {
  /**
   * Facilities and vehicles resolved from the backend.
   */
  facilitiesWithVehicles: AlgorithmFacilityVehiclesResult[];

  /**
   * Containers resolved from the backend.
   */
  selectedContainers: AlgorithmEntityResult[];

  /**
   * Number of planning days used in the request.
   */
  numberOfDays: number;

  /**
   * Average pickup time in minutes used in the request.
   */
  averagePickupTimeMinutes: number;
}

/**
 * AlgorithmFacilityVehiclesResult
 *
 * Represents one resolved facility together with the resolved vehicles
 * selected for it.
 */
export interface AlgorithmFacilityVehiclesResult {
  facility: AlgorithmEntityResult;
  selectedVehicles: AlgorithmEntityResult[];
}

/**
 * AlgorithmEntityResult
 *
 * Generic shape used to store the processed entities returned by the backend.
 */
export interface AlgorithmEntityResult {
  id: string;
  [key: string]: unknown;
}
