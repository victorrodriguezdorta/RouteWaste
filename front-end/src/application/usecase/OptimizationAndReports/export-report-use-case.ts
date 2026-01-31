// ExportReportUseCase.ts
// Use case contract for exporting reports

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Represents an exported report result
 */
export interface ExportedReport {
    fileName: string;
    fileUrl: string;
    fileSize: number;
    exportedAt: Date;
    format: 'pdf' | 'csv' | 'xlsx' | 'json';
}

/**
 * Use case for exporting reports to a file.
 * Input: reportId, export format
 * Output: ExportedReport
 */

// Command object for input data
export interface ExportReportCommand {
    reportId: string;
    format: 'pdf' | 'csv' | 'xlsx' | 'json';
    includeCharts?: boolean;
}

// Result type for the use case
export type ExportReportResult = ExportedReport;

// Use case contract
export interface ExportReportUseCase {
    /**
     * Handles the export of a report to a file
     *
     * @param command Data needed to export the report
     * @returns Either a DataError or the exported report details
     */
    execute(command: ExportReportCommand): Promise<Either<DataError, ExportReportResult>>;
}
