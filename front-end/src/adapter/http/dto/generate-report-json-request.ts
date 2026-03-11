import type { GenerateReportCommand } from '../../../application/usecase/optimization-and-reports/generate-report/generate-report-use-case';

/**
 * GenerateReportJsonRequest
 * 
 * JSON request adapter for the GenerateReport use case.
 * Transforms GenerateReportCommand to JSON format for HTTP requests.
 */
export class GenerateReportJsonRequest {
  /**
   * Convert GenerateReportCommand to JSON request body.
   * 
   * @param command GenerateReportCommand from use case layer
   * @returns JSON object ready for HTTP transmission
   */
  static toRequest(command: GenerateReportCommand): any {
    return {
      // TODO: Map command properties to JSON structure
      // This is a placeholder implementation
      ...command
    };
  }
}
