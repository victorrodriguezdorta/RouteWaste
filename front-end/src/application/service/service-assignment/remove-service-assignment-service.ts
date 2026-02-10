import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ServiceAssignmentRepository } from '../../repository/service-assignment-repository';
import type { RemoveServiceAssignmentCommand, RemoveServiceAssignmentResult, RemoveServiceAssignmentUseCase } from '../../usecase/service-assignment-management/remove-service-assignment/remove-service-assignment-use-case';

/**
 * @brief Service implementing the RemoveServiceAssignment use case.
 *
 * Delegates removal to `ServiceAssignmentRepository`.
 */
export class RemoveServiceAssignmentService implements RemoveServiceAssignmentUseCase {
    private readonly serviceAssignmentRepository: ServiceAssignmentRepository;

    /**
     * @brief Construct the service with a repository.
     * @param serviceAssignmentRepository Repository used to remove assignments.
     */
    constructor(serviceAssignmentRepository: ServiceAssignmentRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * @brief Execute the remove service assignment use case.
     * @param command Data containing the id of the assignment to remove.
     * @return Either a `DataError` or a boolean indicating success.
     */
    async execute(command: RemoveServiceAssignmentCommand): Promise<Either<DataError, RemoveServiceAssignmentResult>> {
        return this.serviceAssignmentRepository.delete(command);
    }
}
