import type { OptimizeRoutesCommand } from '../../../application/usecase/optimization-and-reports/optimize-routes/optimize-routes-use-case';

/**
 * OptimizeRoutesJsonRequest
 * 
 * JSON request adapter for the OptimizeRoutes use case.
 * Transforms OptimizeRoutesCommand to JSON format for HTTP requests.
 */
export class OptimizeRoutesJsonRequest {
  /**
   * Convert OptimizeRoutesCommand to JSON request body.
   * 
   * @param command OptimizeRoutesCommand from use case layer
   * @returns JSON object ready for HTTP transmission
   */
  static toRequest(command: OptimizeRoutesCommand): any {
    return {
      // TODO: Map command properties to JSON structure
      // This is a placeholder implementation
      ...command
    };
  }
}
