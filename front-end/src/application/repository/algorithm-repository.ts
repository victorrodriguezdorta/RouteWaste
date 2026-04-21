import type { CreateAlgorithmCommand, CreateAlgorithmResult } from '@/application/usecase/algorithm-management/create-algorithm/create-algorithm-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * AlgorithmRepository
 *
 * Repository interface for Algorithm execution.
 * Defines methods for executing the delivery planning algorithm.
 */
export interface AlgorithmRepository {
  /**
   * Create and execute an algorithm execution request.
   * @param command Data required to execute the algorithm.
   * @return Either a DataError or the algorithm execution result.
   */
  create(command: CreateAlgorithmCommand): Promise<Either<DataError, CreateAlgorithmResult>>;
}
