import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ServiceAssignmentRepository } from '../../repository/service-assignment-repository';
import type { AssignContainerToFacilityCommand, AssignContainerToFacilityResult, AssignContainerToFacilityUseCase } from '../../usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-use-case';

/**
 * @brief Service implementing the AssignContainerToFacility use case.
 *
 * Delegates creation of a `ServiceAssignment` to a `ServiceAssignmentRepository`.
 */
export class AssignContainerToFacilityService implements AssignContainerToFacilityUseCase {
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
     * @return Either a `DataError` or the created `ServiceAssignment` entity.
     */
    async execute(command: AssignContainerToFacilityCommand): Promise<Either<DataError, AssignContainerToFacilityResult>> {
        return this.serviceAssignmentRepository.assignContainerToFacility(command);
    }
}
