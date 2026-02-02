import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateFacilityUseCase, UpdateFacilityCommand, UpdateFacilityResult } from '../../usecase/FacilityManagement/update-facility-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the UpdateFacility use case.
 *
 * Delegates update operation to a `FacilityRepository` implementation. The repository
 * is responsible for performing the actual API call or persistence operation.
 */
export class UpdateFacilityService implements UpdateFacilityUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository.
     * @param facilityRepository Repository used to perform update operations.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the update facility use case.
     * @param command Data required to update the facility (id + partial fields).
     * @return Either a `DataError` or the updated `Facility` entity.
     */
    async execute(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>> {
        return this.facilityRepository.update(command);
    }
}
