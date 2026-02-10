import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

// Command object for input data (optional pagination and filters)
export interface ListServiceAssignmentsCommand {
    page?: number;
    pageSize?: number;
    containerId?: UllUUID;
    facilityId?: UllUUID;
}
