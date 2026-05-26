import type { UpdateVehicleUpdatedFields } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-updated-fields';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** Command object for updating a vehicle. */
export interface UpdateVehicleCommand {
    vehicleId: UllUUID;
    updatedFields: UpdateVehicleUpdatedFields;
}
