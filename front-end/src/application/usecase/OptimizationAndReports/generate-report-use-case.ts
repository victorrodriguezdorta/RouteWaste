// GenerateReportUseCase.ts
// Use case contract for generating reports

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Represents a generated report
 */
export interface GeneratedReport {
    reportId: string;
    title: string;
    generatedAt: Date;
    content: string;
    format: 'pdf' | 'html' | 'json';
}

/**
 * Use case for generating reports.
 * Input: report type, date range, filters
 * Output: GeneratedReport
 */

// Command object for input data
export interface GenerateReportCommand {
    reportType: 'containers' | 'facilities' | 'routes' | 'costs' | 'summary';
    startDate?: Date;
    endDate?: Date;
    filters?: {
        zoneIds?: string[];
        facilityIds?: string[];
        containerIds?: string[];
    };
    format?: 'pdf' | 'html' | 'json';
}

// Result type for the use case
export type GenerateReportResult = GeneratedReport;

// Use case contract
export interface GenerateReportUseCase {
    /**
     * Handles the generation of a report
     *
     * @param command Data needed to generate the report
     * @returns Either a DataError or the generated report
     */
    execute(command: GenerateReportCommand): Promise<Either<DataError, GenerateReportResult>>;
}
