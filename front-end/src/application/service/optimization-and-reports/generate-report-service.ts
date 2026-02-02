import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GenerateReportUseCase, GenerateReportCommand, GenerateReportResult } from '../../usecase/OptimizationAndReports/generate-report-use-case';
import type { OptimizationAndReportsRepository } from '../../repository/optimization-and-reports-repository';

/**
 * @brief Service implementing the GenerateReport use case.
 *
 * Delegates report generation to a `ReportRepository`. The repository should
 * produce a `GeneratedReport` wrapped in an `Either<DataError, GeneratedReport>`.
 */
export class GenerateReportService implements GenerateReportUseCase {
    private readonly reportRepository: OptimizationAndReportsRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param reportRepository Repository used to generate reports.
     */
    constructor(reportRepository: OptimizationAndReportsRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @brief Execute the generate report use case.
     * @param command Parameters describing the report to generate.
     * @return Either a `DataError` or the generated report.
     */
    async execute(command: GenerateReportCommand): Promise<Either<DataError, GenerateReportResult>> {
        return this.reportRepository.generateReport(command);
    }
}
