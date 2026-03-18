// OptimizeRoutesUseCase.ts
// Use case contract for optimizing collection routes

import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { OptimizeRoutesCommand } from './optimize-routes-command';
import type { OptimizeRoutesResult } from './optimize-routes-result';

/*
 * Use case for optimizing collection routes.
 * Input: containers, facilities, vehicles, optimization parameters
 * Output: OptimizedRoute[]
 */
export interface OptimizeRoutesUseCase {
    /**
     * Handles the optimization of collection routes
     *
     * @param command Data needed to optimize routes
     * @returns Either a DataError or the optimized routes
     */
    execute(command: OptimizeRoutesCommand): Promise<Either<DataError, OptimizeRoutesResult>>;
}

export type { OptimizeRoutesCommand } from './optimize-routes-command';
export type { OptimizeRoutesResult } from './optimize-routes-result';
