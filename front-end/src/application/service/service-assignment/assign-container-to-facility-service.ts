import type { ServiceAssignmentRepository } from '@/application/repository/service-assignment-repository';
import type { AssignContainerToFacilityCommand, AssignContainerToFacilityResult, AssignContainerToFacilityUseCase } from '@/application/usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the AssignContainerToFacility use case.
 *
 * Delegates creation of a `ServiceAssignment` to a `ServiceAssignmentRepository`.
 */
export class AssignContainerToFacilityService implements AssignContainerToFacilityUseCase {
    /** Service assignment repository for persisting assignments */
    private readonly serviceAssignmentRepository: ServiceAssignmentRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param serviceAssignmentRepository Repository used to persist assignments.
     */
    constructor(serviceAssignmentRepository: ServiceAssignmentRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * @brief Execute the assign container to facility use case.
     * @param command Data required to create the service assignment.
     * @returns Either a `DataError` or the created `ServiceAssignment` entity.
     */
    async execute(command: AssignContainerToFacilityCommand): Promise<Either<DataError, AssignContainerToFacilityResult>> {
        return this.serviceAssignmentRepository.assignContainerToFacility(command);
    }
}
