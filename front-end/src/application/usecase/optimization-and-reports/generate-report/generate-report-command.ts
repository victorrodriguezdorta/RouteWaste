import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

// Command object for input data
export interface GenerateReportCommand {
    reportType: 'containers' | 'facilities' | 'routes' | 'costs' | 'summary';
    startDate?: Date;
    endDate?: Date;
    filters?: {
        zoneIds?: UllUUID[];
        facilityIds?: UllUUID[];
        containerIds?: UllUUID[];
    };
    format?: 'pdf' | 'html' | 'json';
}
