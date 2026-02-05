// Command object for input data
export interface ExportReportCommand {
    reportId: string;
    format: 'pdf' | 'csv' | 'xlsx' | 'json';
    includeCharts?: boolean;
}


