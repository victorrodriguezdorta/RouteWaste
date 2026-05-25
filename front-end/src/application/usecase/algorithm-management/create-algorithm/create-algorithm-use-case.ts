import type { CreateAlgorithmCommand } from '@/application/model/algorithm-management/create-algorithm/create-algorithm-command';
import type { CreateAlgorithmResult } from '@/application/model/algorithm-management/create-algorithm/create-algorithm-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
/**
 * Use case for executing the delivery planning algorithm.
 */
export interface CreateAlgorithmUseCase {
  /**
   * Handles the creation and execution of the delivery planning algorithm
   * @param command Data needed to execute the algorithm
   * @returns Either a DataError or the algorithm execution result
   */
  execute(command: CreateAlgorithmCommand): Promise<Either<DataError, CreateAlgorithmResult>>;
}

export type { CreateAlgorithmCommand } from '@/application/model/algorithm-management/create-algorithm/create-algorithm-command';
export type { FacilityVehicleCommand } from '@/adapter/http/dto/algorithm/facility-vehicle-command';
export type { CreateAlgorithmResult } from '@/application/model/algorithm-management/create-algorithm/create-algorithm-result';
