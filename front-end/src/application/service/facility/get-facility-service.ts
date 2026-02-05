import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GetFacilityUseCase, GetFacilityCommand, GetFacilityResult } from '../../usecase/FacilityManagement/GetFacility/get-facility-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the GetFacility use case.
 *
 * Delegates retrieval to `FacilityRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, Facility>`.
 */
export class GetFacilityService implements GetFacilityUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository.
     * @param facilityRepository Repository used to query facilities by id.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the get facility use case.
     * @param command Data containing the id of the facility to retrieve.
     * @return Either a `DataError` or the `Facility` entity.
     */
    async execute(command: GetFacilityCommand): Promise<Either<DataError, GetFacilityResult>> {
        return this.facilityRepository.getById(command);
    }
}
