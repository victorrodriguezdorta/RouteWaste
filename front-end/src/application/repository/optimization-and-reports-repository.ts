import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { CalculateCostsCommand, CostCalculationResult } from '../usecase/optimization-and-reports/calculate-costs/calculate-costs-use-case';
import type { ExportReportCommand, ExportReportResult } from '../usecase/optimization-and-reports/export-report/export-report-use-case';
import type { GenerateReportCommand, GenerateReportResult } from '../usecase/optimization-and-reports/generate-report/generate-report-use-case';
import type { OptimizeRoutesCommand, OptimizeRoutesResult } from '../usecase/optimization-and-reports/optimize-routes/optimize-routes-use-case';

/**
 * @brief Repository interface for optimization and reports.
 * Defines methods for report generation, export, cost calculation and route optimization.
 */
export interface OptimizationAndReportsRepository {
  /**
   * @brief Generate a report.
   * @param command Parameters describing the report to generate.
   * @return Either a DataError or the generated report.
   */
  generateReport(command: GenerateReportCommand): Promise<Either<DataError, GenerateReportResult>>;

  /**
   * @brief Export a report.
   * @param command Parameters describing the report to export and format.
   * @return Either a DataError or an export result (e.g., URL or file id).
   */
  exportReport(command: ExportReportCommand): Promise<Either<DataError, ExportReportResult>>;

  /**
   * @brief Calculate costs for a given scenario.
   * @param command Input data required to compute costs.
   * @return Either a DataError or the computed cost value.
   */
  calculateCosts(command: CalculateCostsCommand): Promise<Either<DataError, CostCalculationResult>>;

  /**
   * @brief Optimize routes for containers, facilities, vehicles, etc.
   * @param command Input data with containers, facilities, vehicles and params.
   * @return Either a DataError or the optimized routes result.
   */
  optimizeRoutes(command: OptimizeRoutesCommand): Promise<Either<DataError, OptimizeRoutesResult>>;
}
