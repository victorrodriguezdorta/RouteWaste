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

// Result type for the use case
export type GenerateReportResult = GeneratedReport;