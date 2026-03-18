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
