package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlansReferencingEntityUseCase;
import es.ull.project.domain.entity.Facility;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for deleting facilities.
 * This service handles the business logic for facility deletion operations.
 */
public class DeleteFacilityService implements DeleteFacilityUseCase {

    private final FacilityRepository repository;
    private final DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityUseCase;

    /**
     * Constructs a new DeleteFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     * @param deleteInfrastructurePlansReferencingEntityUseCase removes plans that referenced this facility in their execution snapshot
     */
    public DeleteFacilityService(
            FacilityRepository repository,
            DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityUseCase) {
        this.repository = repository;
        this.deleteInfrastructurePlansReferencingEntityUseCase = deleteInfrastructurePlansReferencingEntityUseCase;
    }

    /**
     * Deletes a facility by its unique identifier.
     * @param id the unique identifier of the facility to delete
     * @return the deleted facility
     * @throws NoSuchElementException if no facility is found with the given id
     */
    @Override
    public Facility delete(UUID id) {
        Facility existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
        this.deleteInfrastructurePlansReferencingEntityUseCase.deletePlansWhoseExecutionRequestReferencesEntity(id);
        this.repository.delete(existing);
        return existing;
    }
}
