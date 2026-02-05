import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateFacilityUseCase, CreateFacilityCommand, CreateFacilityResult } from '../../usecase/FacilityManagement/CreateFacility/create-facility-use-case';
import type { FacilityRepository } from '../../repository/facility-repository';

/**
 * @brief Service implementing the CreateFacility use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `FacilityRepository`. Repository methods are assumed to
 * be available and will be provided later by the professor's infrastructure.
 */
export class CreateFacilityService implements CreateFacilityUseCase {
    private readonly facilityRepository: FacilityRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param facilityRepository Repository used to persist or forward create requests.
     */
    constructor(facilityRepository: FacilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    /**
     * @brief Execute the create facility use case.
     * @param command Input data required to create a facility.
     * @return Either a `DataError` or the created `Facility` entity.
     */
    async execute(command: CreateFacilityCommand): Promise<Either<DataError, CreateFacilityResult>> {
        return this.facilityRepository.create(command);
    }
}
