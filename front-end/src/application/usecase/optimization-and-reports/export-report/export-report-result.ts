/**
 * Represents an exported report result
 * TODO: ¿is it necesary to do VO for this?
 */
export interface ExportedReport {
    fileName: string;
    fileUrl: string;
    fileSize: number;
    exportedAt: Date;
    format: 'pdf' | 'csv' | 'xlsx' | 'json';
}

// Result type for the use case
export type ExportReportResult = ExportedReport;