// ListServiceAssignmentsUseCase.ts
// Use case contract for listing all service assignments

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { ServiceAssignment } from '../../../domain/entity/service-assignment';

/**
 * Use case for listing all service assignments in the system.
 * Input: none (optionally pagination params or filters)
 * Output: ServiceAssignment[] (entity array)
 */

// Command object for input data (optional pagination and filters)
export interface ListServiceAssignmentsCommand {
    page?: number;
    pageSize?: number;
    containerId?: string;
    facilityId?: string;
}

// Result type for the use case
export type ListServiceAssignmentsResult = ServiceAssignment[];

// Use case contract
export interface ListServiceAssignmentsUseCase {
    /**
     * Handles listing all service assignments
     *
     * @param command Optional pagination and filter parameters
     * @returns Either a DataError or a list of ServiceAssignment entities
     */
    execute(command?: ListServiceAssignmentsCommand): Promise<Either<DataError, ListServiceAssignmentsResult>>;
}
