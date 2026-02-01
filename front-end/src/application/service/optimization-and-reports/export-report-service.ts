import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ExportReportUseCase, ExportReportCommand, ExportReportResult } from '../../usecase/OptimizationAndReports/export-report-use-case';
import { ReportRepository } from '../../repository/report/report-repository';

/**
 * @brief Service implementing the ExportReport use case.
 *
 * Delegates exporting of reports to a `ReportRepository`. The repository is
 * expected to handle the export format and return a URL or identifier.
 */
export class ExportReportService implements ExportReportUseCase {
    private readonly reportRepository: ReportRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param reportRepository Repository used to export reports.
     */
    constructor(reportRepository: ReportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @brief Execute the export report use case.
     * @param command Parameters describing the report to export and format.
     * @return Either a `DataError` or an export result (e.g., URL or file id).
     */
    async execute(command: ExportReportCommand): Promise<Either<DataError, ExportReportResult>> {
        return this.reportRepository.export(command);
    }
}
