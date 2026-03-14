// Command object for input data (optional pagination, sort and filter)
export interface ListVehiclesCommand {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    vehicleType?: string;
}