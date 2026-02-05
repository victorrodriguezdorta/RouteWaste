import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListFacilitiesUseCase, ListFacilitiesCommand, ListFacilitiesResult } from '../../usecase/FacilityManagement/ListFacilities/list-facilities-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the ListFacilities use case.
 *
 * Delegates listing to `FacilityRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, Facility[]>`.
 */
export class ListFacilitiesService implements ListFacilitiesUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository.
     * @param facilityRepository Repository used to list facilities.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the list facilities use case.
     * @param command Optional pagination parameters.
     * @return Either a `DataError` or an array of `Facility` entities.
     */
    async execute(command?: ListFacilitiesCommand): Promise<Either<DataError, ListFacilitiesResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        return this.facilityRepository.list({ page, pageSize });
    }
}
