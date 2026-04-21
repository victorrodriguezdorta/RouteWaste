import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { CreateAlgorithmCommand } from './create-algorithm-command';
import type { CreateAlgorithmResult } from './create-algorithm-result';

export type { CreateAlgorithmCommand } from './create-algorithm-command';
export type { CreateAlgorithmResult } from './create-algorithm-result';

/**
 * CreateAlgorithmUseCase
 *
 * Use case for executing the delivery planning algorithm.
 * Input: facilities with vehicles, containers, number of days, pickup time
 * Output: Algorithm execution result with ID and status
 */
export interface CreateAlgorithmUseCase {
  /**
   * Handles the creation and execution of the delivery planning algorithm
   * @param command Data needed to execute the algorithm
   * @returns Either a DataError or the algorithm execution result
   */
  execute(command: CreateAlgorithmCommand): Promise<Either<DataError, CreateAlgorithmResult>>;
}
