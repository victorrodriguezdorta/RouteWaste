/**
 * Command object for listing containers (optional pagination, sort and filter)
 */
export interface ListContainersCommand {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    wasteType?: string;
    serviceZone?: string;
    location?: string;
}
