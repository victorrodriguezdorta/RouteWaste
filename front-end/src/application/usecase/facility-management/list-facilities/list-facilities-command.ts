
// Command object for input data with pagination, sorting and filtering
export interface ListFacilitiesCommand {
    page?: number;
    pageSize?: number;
    sortBy?: string; // Field to sort by: 'type', 'location', 'storageCapacity', 'processingCapacity', 'unloadingTime', 'openingCost', 'status'
    sortOrder?: string; // Sort order: 'asc' or 'desc'
    facilityType?: string; // Filter by facility type
    status?: string; // Filter by status
    location?: string; // Filter by location (postal address search)
}

