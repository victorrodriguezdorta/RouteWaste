import type { FacilityRepository } from '@/application/repository/facility-repository';
import type { GetFacilityCommand, GetFacilityResult, GetFacilityUseCase } from '@/application/usecase/facility-management/get-facility/get-facility-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the GetFacility use case.
 *
 * Delegates retrieval to `FacilityRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, Facility>`.
 */
export class GetFacilityService implements GetFacilityUseCase {
    /**
     * Repository used to query facilities by id.
     */
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
     * @returns Either a `DataError` or the `Facility` entity.
     */
    async execute(command: GetFacilityCommand): Promise<Either<DataError, GetFacilityResult>> {
        return this.facilityRepository.getById(command);
    }
}
