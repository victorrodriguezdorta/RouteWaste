import type { FacilityRepository } from '@/application/repository/facility-repository';
import type { UpdateFacilityCommand, UpdateFacilityResult, UpdateFacilityUseCase } from '@/application/usecase/facility-management/update-facility/update-facility-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the UpdateFacility use case.
 *
 * Delegates update operation to a `FacilityRepository` implementation. The repository
 * is responsible for performing the actual API call or persistence operation.
 */
export class UpdateFacilityService implements UpdateFacilityUseCase {
    /** Repository for performing facility update operations */
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
     * @returns Either a `DataError` or the updated `Facility` entity.
     */
    async execute(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>> {
        return this.facilityRepository.update(command);
    }
}
