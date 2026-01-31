package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.domain.entity.Facility;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for reading facilities.
 * This service handles the business logic for facility retrieval operations.
 */
public class ReadFacilityService implements ReadFacilityUseCase {

    private final FacilityRepository repository;

    /**
     * Constructs a new ReadFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     */
    public ReadFacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches a facility by its unique identifier.
     * @param id the unique identifier of the facility to fetch
     * @return the facility with the specified identifier
     * @throws NoSuchElementException if no facility is found with the given id
     */
    @Override
    public Facility fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
    }

    /**
     * Fetches all facilities from the repository.
     * @return a list of all facilities
     */
    @Override
    public List<Facility> fetchAll() {
        return this.repository.findAll();
    }
}
