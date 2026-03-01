package es.ull.project.application.service.container;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.exception.ReferentialIntegrityException;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.domain.entity.Container;

/**
 * Service implementation for deleting containers.
 * This service handles the business logic for container deletion operations.
 */
public class DeleteContainerService implements DeleteContainerUseCase {

    private final ContainerRepository repository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;

    /**
     * Constructs a new DeleteContainerService with the specified repositories.
     * @param repository the container repository for persistence operations
     * @param serviceAssignmentRepository the service assignment repository for referential integrity checks
     */
    public DeleteContainerService(ContainerRepository repository, ServiceAssignmentRepository serviceAssignmentRepository) {
        this.repository = repository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * Deletes a container by its unique identifier.
     * Validates that no service assignments reference this container before deletion.
     * @param id the unique identifier of the container to delete
     * @return the deleted container
     * @throws NoSuchElementException if no container is found with the given id
     * @throws ReferentialIntegrityException if service assignments reference this container
     */
    @Override
    public Container delete(UUID id) {
        Container existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
        
        // Check referential integrity
        List<?> referencingAssignments = serviceAssignmentRepository.findByContainerId(id);
        if (!referencingAssignments.isEmpty()) {
            List<UUID> referencingIds = referencingAssignments.stream()
                .map(sa -> ((es.ull.project.domain.entity.ServiceAssignment) sa).getId())
                .toList();
            throw new ReferentialIntegrityException(
                "Container",
                id.toString(),
                "ServiceAssignment",
                referencingIds
            );
        }
        
        this.repository.delete(existing);
        return existing;
    }
}
