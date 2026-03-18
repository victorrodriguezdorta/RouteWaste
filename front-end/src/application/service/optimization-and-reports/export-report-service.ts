import type { OptimizationAndReportsRepository } from '@/application/repository/optimization-and-reports-repository';
import type { ExportReportCommand, ExportReportResult, ExportReportUseCase } from '@/application/usecase/optimization-and-reports/export-report/export-report-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ExportReport use case.
 *
 * Delegates exporting of reports to a `ReportRepository`. The repository is
 * expected to handle the export format and return a URL or identifier.
 */
export class ExportReportService implements ExportReportUseCase {
    private readonly reportRepository: OptimizationAndReportsRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param reportRepository Repository used to export reports.
     */
    constructor(reportRepository: OptimizationAndReportsRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @brief Execute the export report use case.
     * @param command Parameters describing the report to export and format.
     * @return Either a `DataError` or an export result (e.g., URL or file id).
     */
    async execute(command: ExportReportCommand): Promise<Either<DataError, ExportReportResult>> {
        return this.reportRepository.exportReport(command);
    }
}
