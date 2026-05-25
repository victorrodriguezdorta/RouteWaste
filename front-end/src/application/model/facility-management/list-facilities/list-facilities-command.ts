/** Command object for listing facilities with optional pagination, sorting and filtering. */
export interface ListFacilitiesCommand {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortOrder?: string;
    facilityType?: string;
    status?: string;
    location?: string;
}
