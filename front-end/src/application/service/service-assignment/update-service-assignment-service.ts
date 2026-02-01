import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateServiceAssignmentUseCase, UpdateServiceAssignmentCommand, UpdateServiceAssignmentResult } from '../../usecase/ServiceAssignmentManagement/update-service-assignment-use-case';
import { ServiceAssignmentRepository } from '../../repository/service-assignment/service-assignment-repository';

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
        return this.serviceAssignmentRepository.update(command.assignmentId, command.updatedFields);
    }
}
