// UpdateServiceAssignmentUseCase.ts
// Use case contract for updating a service assignment

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { ServiceAssignment } from '../../../domain/entity/ServiceAssignment';
import { WasteDemand } from '../../../domain/valueobject/demand/WasteDemand';
import { Distance } from '../../../domain/valueobject/location/Distance';
import { ServiceTime } from '../../../domain/valueobject/location/ServiceTime';
import { TransportationVariableCost } from '../../../domain/valueobject/cost/TransportationVariableCost';

/**
 * Use case for updating an existing service assignment.
 * Input: assignmentId and fields to update
 * Output: ServiceAssignment (entity)
 */

// Command object for input data
export interface UpdateServiceAssignmentCommand {
    assignmentId: string;
    updatedFields: Partial<{
        wasteDemand: WasteDemand;
        distance: Distance;
        serviceTime: ServiceTime;
        transportCost: TransportationVariableCost;
    }>;
}

// Result type for the use case
export type UpdateServiceAssignmentResult = ServiceAssignment;

// Use case contract
export interface UpdateServiceAssignmentUseCase {
    /**
     * Handles the update of an existing service assignment
     *
     * @param command Data needed to update the service assignment
     * @returns Either a DataError or the updated ServiceAssignment
     */
    execute(command: UpdateServiceAssignmentCommand): Promise<Either<DataError, UpdateServiceAssignmentResult>>;
}
