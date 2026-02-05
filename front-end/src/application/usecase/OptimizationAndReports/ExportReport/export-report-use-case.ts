// ExportReportUseCase.ts
// Use case contract for exporting reports

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ExportReportCommand } from './export-report-command';
import type { ExportReportResult } from './export-report-result';

/**
 * Use case for exporting reports to a file.
 * Input: reportId, export format
 * Output: ExportedReport
 */
export interface ExportReportUseCase {
    /**
     * Handles the export of a report to a file
     *
     * @param command Data needed to export the report
     * @returns Either a DataError or the exported report details
     */
    execute(command: ExportReportCommand): Promise<Either<DataError, ExportReportResult>>;
}

export type { ExportReportCommand } from './export-report-command';
export type { ExportReportResult } from './export-report-result';
