import type { AlgorithmRepository } from '@/application/repository/algorithm-repository';
import type { CreateAlgorithmCommand, CreateAlgorithmResult, CreateAlgorithmUseCase } from '@/application/usecase/algorithm-management/create-algorithm/create-algorithm-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * CreateAlgorithmService
 *
 * Service implementing the CreateAlgorithm use case.
 * Delegates the execution request to the AlgorithmRepository for communication
 * with the backend API.
 */
export class CreateAlgorithmService implements CreateAlgorithmUseCase {
  /**
   * Repository instance for algorithm execution operations.
   */
  private readonly algorithmRepository: AlgorithmRepository;

  /**
   * Construct the service with a repository implementation.
   * @param algorithmRepository Repository used to forward algorithm execution requests.
   */
  constructor(algorithmRepository: AlgorithmRepository) {
    this.algorithmRepository = algorithmRepository;
  }

  /**
   * Execute the create algorithm use case.
   * @param command Input data required to execute the algorithm.
   * @returns Either a DataError or the algorithm execution result.
   */
  async execute(command: CreateAlgorithmCommand): Promise<Either<DataError, CreateAlgorithmResult>> {
    return this.algorithmRepository.create(command);
  }
}
