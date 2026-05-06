// Command object for input data (optional pagination)
export interface ListInfrastructurePlansCommand {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    wasteType?: string;
    serviceZone?: string;
    location?: string;
}