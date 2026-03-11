import type { ExportReportCommand } from '../../../application/usecase/optimization-and-reports/export-report/export-report-use-case';

/**
 * ExportReportJsonRequest
 * 
 * JSON request adapter for the ExportReport use case.
 * Transforms ExportReportCommand to JSON format for HTTP requests.
 */
export class ExportReportJsonRequest {
  /**
   * Convert ExportReportCommand to JSON request body.
   * 
   * @param command ExportReportCommand from use case layer
   * @returns JSON object ready for HTTP transmission
   */
  static toRequest(command: ExportReportCommand): any {
    return {
      // TODO: Map command properties to JSON structure
      // This is a placeholder implementation
      ...command
    };
  }
}
