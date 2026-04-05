import type { FacilityRepository } from '@/application/repository/facility-repository';
import type { ListFacilitiesCommand, ListFacilitiesResult, ListFacilitiesUseCase } from '@/application/usecase/facility-management/list-facilities/list-facilities-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ListFacilities use case.
 *
 * Delegates listing to `FacilityRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, Facility[]>`.
 */
export class ListFacilitiesService implements ListFacilitiesUseCase {
    /**
     * Repositorio utilizado para listar las entidades Facility.
     * Proporciona acceso a las operaciones de persistencia relacionadas con Facility.
     */
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
     * @param command Optional pagination, sort and filter parameters.
     * @returns Either a `DataError` or a paginated set of `Facility` entities.
     */
    async execute(command?: ListFacilitiesCommand): Promise<Either<DataError, ListFacilitiesResult>> {
        return this.facilityRepository.list(command);
    }
}
