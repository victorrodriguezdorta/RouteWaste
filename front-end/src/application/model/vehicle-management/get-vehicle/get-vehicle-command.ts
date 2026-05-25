import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

// Command object for getting a vehicle by id
export interface GetVehicleCommand {
    vehicleId: UllUUID;
}
