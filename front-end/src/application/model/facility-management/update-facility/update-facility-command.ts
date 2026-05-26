import type { UpdateFacilityUpdatedFields } from '@/application/model/facility-management/update-facility/update-facility-updated-fields';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** Command object for updating a facility. */
export interface UpdateFacilityCommand {
    facilityId: UllUUID;
    updatedFields: UpdateFacilityUpdatedFields;
}
