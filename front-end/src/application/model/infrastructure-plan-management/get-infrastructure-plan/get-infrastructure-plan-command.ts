import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** Command object for retrieving an infrastructure plan. */
export interface GetInfrastructurePlanCommand {
    planId: UllUUID;
}
