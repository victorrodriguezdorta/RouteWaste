import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { FilterFacilitiesUseCase, FilterFacilitiesCommand, FilterFacilitiesResult } from '../../usecase/FacilityManagement/filter-facilities-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the FilterFacilities use case.
 *
 * Delegates filtering to `FacilityRepository`. The repository is expected to
 * accept the filter criteria and return matching facilities wrapped in `Either`.
 */
export class FilterFacilitiesService implements FilterFacilitiesUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository.
     * @param facilityRepository Repository used to filter facilities.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the filter facilities use case.
     * @param command Filter criteria for facilities.
     * @return Either a `DataError` or an array of matching `Facility` entities.
     */
    async execute(command: FilterFacilitiesCommand): Promise<Either<DataError, FilterFacilitiesResult>> {
        return this.facilityRepository.filter(command);
    }
}
