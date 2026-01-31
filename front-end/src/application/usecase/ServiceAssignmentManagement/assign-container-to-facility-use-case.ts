// AssignContainerToFacilityUseCase.ts
// Use case contract for assigning a container to a facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { ServiceAssignment } from '../../../domain/entity/service-assignment';
import { WasteDemand } from '../../../domain/valueobject/demand/waste-demand';
import { Distance } from '../../../domain/valueobject/location/distance';
import { ServiceTime } from '../../../domain/valueobject/location/service-time';
import { TransportationVariableCost } from '../../../domain/valueobject/cost/transportation-variable-cost';

/**
 * Use case for assigning a container to a facility.
 * Input: containerId, facilityId, wasteDemand, distance, serviceTime, transportCost
 * Output: ServiceAssignment (entity)
 */

// Command object for input data
export interface AssignContainerToFacilityCommand {
    containerId: string;
    facilityId: string;
    wasteDemand: WasteDemand;
    distance: Distance;
    serviceTime: ServiceTime;
    transportCost: TransportationVariableCost;
}

// Result type for the use case
export type AssignContainerToFacilityResult = ServiceAssignment;

// Use case contract
export interface AssignContainerToFacilityUseCase {
    /**
     * Handles the assignment of a container to a facility
     *
     * @param command Data needed to create the service assignment
     * @returns Either a DataError or the created ServiceAssignment
     */
    execute(command: AssignContainerToFacilityCommand): Promise<Either<DataError, AssignContainerToFacilityResult>>;
}
