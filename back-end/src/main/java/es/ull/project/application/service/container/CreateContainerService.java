package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

/**
 * Service implementation for creating containers.
 * This service handles the business logic for container creation operations.
 */
public class CreateContainerService implements CreateContainerUseCase {

    private final ContainerRepository repository;

    /**
     * Constructs a new CreateContainerService with the specified repository.
     * @param repository the container repository for persistence operations
     */
    public CreateContainerService(ContainerRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new container with the specified attributes.
     * @param location the geographic location of the container
     * @param wasteType the type of waste the container handles
     * @param wasteDemand the waste demand configuration
     * @param serviceZone the service zone where the container is located
     * @return the created and persisted container
     */
    @Override
    public Container create(Location location, WasteType wasteType, WasteDemand wasteDemand, ServiceZone serviceZone) {
        Container newContainer = new Container(location, wasteType, wasteDemand, serviceZone);
        Container saved = this.repository.save(newContainer);
        return saved;
    }
}
