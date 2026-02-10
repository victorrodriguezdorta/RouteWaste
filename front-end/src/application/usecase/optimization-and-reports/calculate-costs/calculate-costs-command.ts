
import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

// Command object for input data
export interface CalculateCostsCommand {
    planId?: UllUUID;
    facilityIds?: UllUUID[];
    includeTransportation?: boolean;
}

