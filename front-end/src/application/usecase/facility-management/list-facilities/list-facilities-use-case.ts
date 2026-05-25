import type { ListFacilitiesCommand } from '@/application/model/facility-management/list-facilities/list-facilities-command';
import type { ListFacilitiesResult } from '@/application/model/facility-management/list-facilities/list-facilities-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for listing all facilities
 */
export interface ListFacilitiesUseCase {
    /**
     * Handles listing all facilities
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Facility entities
     */
    execute(command?: ListFacilitiesCommand): Promise<Either<DataError, ListFacilitiesResult>>;
}

export type { ListFacilitiesCommand } from '@/application/model/facility-management/list-facilities/list-facilities-command';
export type { ListFacilitiesResult } from '@/application/model/facility-management/list-facilities/list-facilities-result';
