import type { ServiceAssignmentRepository } from '@/application/repository/service-assignment-repository';
import type { ListServiceAssignmentsCommand, ListServiceAssignmentsResult, ListServiceAssignmentsUseCase } from '@/application/usecase/service-assignment-management/list-service-assignments/list-service-assignments-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ListServiceAssignments use case.
 *
 * Delegates listing to `ServiceAssignmentRepository`.
 */
export class ListServiceAssignmentsService implements ListServiceAssignmentsUseCase {
    /** The repository used to persist and retrieve service assignments. */
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
     * @returns Either a `DataError` or an array of `ServiceAssignment` entities.
     */
    async execute(command?: ListServiceAssignmentsCommand): Promise<Either<DataError, ListServiceAssignmentsResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        const containerId = command?.containerId;
        const facilityId = command?.facilityId;
        return this.serviceAssignmentRepository.list({ page, pageSize, containerId, facilityId });
    }
}
