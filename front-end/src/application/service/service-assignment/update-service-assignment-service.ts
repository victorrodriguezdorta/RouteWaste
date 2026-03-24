import type { ServiceAssignmentRepository } from '@/application/repository/service-assignment-repository';
import type { UpdateServiceAssignmentCommand, UpdateServiceAssignmentResult, UpdateServiceAssignmentUseCase } from '@/application/usecase/service-assignment-management/update-service-assignment/update-service-assignment-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the UpdateServiceAssignment use case.
 *
 * Delegates update operation to a `ServiceAssignmentRepository` implementation.
 */
export class UpdateServiceAssignmentService implements UpdateServiceAssignmentUseCase {
    /** Repository used to perform update operations. */
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
     * @returns Either a `DataError` or the updated `ServiceAssignment` entity.
     */
    async execute(command: UpdateServiceAssignmentCommand): Promise<Either<DataError, UpdateServiceAssignmentResult>> {
        return this.serviceAssignmentRepository.update(command);
    }
}
