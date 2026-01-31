package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.domain.entity.Facility;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for deleting facilities.
 * This service handles the business logic for facility deletion operations.
 */
public class DeleteFacilityService implements DeleteFacilityUseCase {

    private final FacilityRepository repository;

    /**
     * Constructs a new DeleteFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     */
    public DeleteFacilityService(FacilityRepository repository) {
        this.repository = repository;
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
        this.repository.delete(existing);
        return existing;
    }
}
