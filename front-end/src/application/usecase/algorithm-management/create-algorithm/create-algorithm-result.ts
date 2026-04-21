import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

/**
 * CreateAlgorithmResult
 *
 * Result object for the algorithm creation use case.
 * Contains the execution ID and status of the algorithm request.
 */
export interface CreateAlgorithmResult {
  /**
   * Unique identifier for the algorithm execution request
   */
  executionId: UllUUID;

  /**
   * Status of the algorithm execution
   */
  status: string;

  /**
   * Message describing the result of the algorithm execution
   */
  message: string;
}
