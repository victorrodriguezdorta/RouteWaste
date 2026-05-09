/**
 * CreateAlgorithmResult
 *
 * Result object returned by the backend after attempting to execute
 * and persist the infrastructure plan.
 */
export interface CreateAlgorithmResult {
  /**
   * Backend execution status.
   */
  status: 'success' | 'error';

  /**
   * Human-readable backend message.
   */
  message: string;

  /**
   * Optional backend error details.
   */
  details?: string;

  /**
   * Persisted infrastructure plan identifier when the operation succeeds.
   */
  infrastructurePlanId?: string;
}

/**
 * Legacy response shapes kept temporarily for backward compatibility with
 * any older consumers that still import these helper interfaces.
 */
export interface AlgorithmFacilityVehiclesResult {
  facility: AlgorithmEntityResult;
  selectedVehicles: AlgorithmEntityResult[];
}

/**
 * Generic shape used to store resolved entities.
 */
export interface AlgorithmEntityResult {
  id: string;
  [key: string]: unknown;
}
