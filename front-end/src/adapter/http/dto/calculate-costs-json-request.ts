import type { CalculateCostsCommand } from '../../../application/usecase/optimization-and-reports/calculate-costs/calculate-costs-use-case';

/**
 * CalculateCostsJsonRequest
 * 
 * JSON request adapter for the CalculateCosts use case.
 * Transforms CalculateCostsCommand to JSON format for HTTP requests.
 */
export class CalculateCostsJsonRequest {
  /**
   * Convert CalculateCostsCommand to JSON request body.
   * 
   * @param command CalculateCostsCommand from use case layer
   * @returns JSON object ready for HTTP transmission
   */
  static toRequest(command: CalculateCostsCommand): any {
    return {
      // TODO: Map command properties to JSON structure
      // This is a placeholder implementation
      ...command
    };
  }
}
