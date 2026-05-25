import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** Command object for deleting a facility. */
export interface DeleteFacilityCommand {
    facilityId: UllUUID;
}
