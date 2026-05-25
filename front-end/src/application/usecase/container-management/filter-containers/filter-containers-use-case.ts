import type { FilterContainersCommand } from '@/application/model/container-management/filter-containers/filter-containers-command';
import type { FilterContainersResult } from '@/application/model/container-management/filter-containers/filter-containers-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for filtering containers
 */
export interface FilterContainersUseCase {
    /**
     * Handles filtering containers based on provided criteria
     * @param command Filter parameters
     * @returns Either a DataError or a list of Container entities matching the filters
     */
    execute(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>>;
}

export type { FilterContainersCommand } from '@/application/model/container-management/filter-containers/filter-containers-command';
export type { FilterContainersResult } from '@/application/model/container-management/filter-containers/filter-containers-result';
