// GenerateReportUseCase.ts
// Use case contract for generating reports

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GenerateReportCommand } from './generate-report-command';
import type { GenerateReportResult } from './generate-report-result';


/**
 * Use case for generating reports.
 * Input: report type, date range, filters
 * Output: GeneratedReport
 */
export interface GenerateReportUseCase {
    /**
     * Handles the generation of a report
     *
     * @param command Data needed to generate the report
     * @returns Either a DataError or the generated report
     */
    execute(command: GenerateReportCommand): Promise<Either<DataError, GenerateReportResult>>;
}

export type { GenerateReportCommand } from './generate-report-command';
export type { GenerateReportResult } from './generate-report-result';