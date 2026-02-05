import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { RemoveServiceAssignmentUseCase, RemoveServiceAssignmentCommand, RemoveServiceAssignmentResult } from '../../usecase/ServiceAssignmentManagement/RemoveServiceAssignment/remove-service-assignment-use-case';
import type{ ServiceAssignmentRepository } from '../../repository/service-assignment-repository';

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
