import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ServiceAssignmentRepository } from '../../repository/service-assignment-repository';
import type { UpdateServiceAssignmentCommand, UpdateServiceAssignmentResult, UpdateServiceAssignmentUseCase } from '../../usecase/service-assignment-management/update-service-assignment/update-service-assignment-use-case';

/**
 * @brief Service implementing the UpdateServiceAssignment use case.
 *
 * Delegates update operation to a `ServiceAssignmentRepository` implementation.
 */
export class UpdateServiceAssignmentService implements UpdateServiceAssignmentUseCase {
    private readonly serviceAssignmentRepository: ServiceAssignmentRepository;

    /**
     * @brief Construct the service with a repository.
     * @param serviceAssignmentRepository Repository used to perform update operations.
     */
    constructor(serviceAssignmentRepository: ServiceAssignmentRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * @brief Execute the update service assignment use case.
     * @param command Data required to update the assignment (id + partial fields).
     * @return Either a `DataError` or the updated `ServiceAssignment` entity.
     */
    async execute(command: UpdateServiceAssignmentCommand): Promise<Either<DataError, UpdateServiceAssignmentResult>> {
        return this.serviceAssignmentRepository.update(command);
    }
}
