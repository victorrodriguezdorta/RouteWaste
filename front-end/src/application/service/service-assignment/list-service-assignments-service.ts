import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ServiceAssignmentRepository } from '../../repository/service-assignment-repository';
import type { ListServiceAssignmentsCommand, ListServiceAssignmentsResult, ListServiceAssignmentsUseCase } from '../../usecase/service-assignment-management/list-service-assignments/list-service-assignments-use-case';

/**
 * @brief Service implementing the ListServiceAssignments use case.
 *
 * Delegates listing to `ServiceAssignmentRepository`.
 */
export class ListServiceAssignmentsService implements ListServiceAssignmentsUseCase {
    private readonly serviceAssignmentRepository: ServiceAssignmentRepository;

    /**
     * @brief Construct the service with a repository.
     * @param serviceAssignmentRepository Repository used to list assignments.
     */
    constructor(serviceAssignmentRepository: ServiceAssignmentRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * @brief Execute the list service assignments use case.
     * @param command Optional pagination and filters.
     * @return Either a `DataError` or an array of `ServiceAssignment` entities.
     */
    async execute(command?: ListServiceAssignmentsCommand): Promise<Either<DataError, ListServiceAssignmentsResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        const containerId = command?.containerId;
        const facilityId = command?.facilityId;
        return this.serviceAssignmentRepository.list({ page, pageSize, containerId, facilityId });
    }
}
