import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { DeleteFacilityUseCase, DeleteFacilityCommand, DeleteFacilityResult } from '../../usecase/FacilityManagement/delete-facility-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the DeleteFacility use case.
 *
 * Delegates deletion to `FacilityRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteFacilityService implements DeleteFacilityUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository.
     * @param facilityRepository Repository used to perform delete operations.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the delete facility use case.
     * @param command Data containing the id of the facility to delete.
     * @return Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteFacilityCommand): Promise<Either<DataError, DeleteFacilityResult>> {
        return this.facilityRepository.delete(command);
    }
}
