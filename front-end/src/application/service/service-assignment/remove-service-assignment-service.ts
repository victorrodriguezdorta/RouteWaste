import type { ServiceAssignmentRepository } from '@/application/repository/service-assignment-repository';
import type { RemoveServiceAssignmentCommand, RemoveServiceAssignmentResult, RemoveServiceAssignmentUseCase } from '@/application/usecase/service-assignment-management/remove-service-assignment/remove-service-assignment-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the RemoveServiceAssignment use case.
 *
 * Delegates removal to `ServiceAssignmentRepository`.
 */
export class RemoveServiceAssignmentService implements RemoveServiceAssignmentUseCase {
    /**
     * Repository used to remove assignments.
     * @remarks Required for delegating removal operations.
     */
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
     * @returns Either a `DataError` or a boolean indicating success.
     */
    async execute(command: RemoveServiceAssignmentCommand): Promise<Either<DataError, RemoveServiceAssignmentResult>> {
        return this.serviceAssignmentRepository.delete(command);
    }
}
