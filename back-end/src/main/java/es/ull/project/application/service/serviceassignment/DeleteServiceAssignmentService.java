package es.ull.project.application.service.serviceassignment;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.exception.ReferentialIntegrityException;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.DeleteServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * Service responsible for deleting service assignments from the system.
 * <p>
 * This service implements the {@link DeleteServiceAssignmentUseCase} interface and provides
 * the business logic for service assignment deletion operations.
 * </p>
 *
 * @see DeleteServiceAssignmentUseCase
 * @see ServiceAssignment
 */
public class DeleteServiceAssignmentService implements DeleteServiceAssignmentUseCase {

    private final ServiceAssignmentRepository repository;
    private final InfrastructurePlanRepository infrastructurePlanRepository;

    /**
     * Constructs a new DeleteServiceAssignmentService with the specified repositories.
     *
     * @param repository the service assignment repository used for persistence operations
     * @param infrastructurePlanRepository the infrastructure plan repository for referential integrity checks
     */
    public DeleteServiceAssignmentService(ServiceAssignmentRepository repository,
                                           InfrastructurePlanRepository infrastructurePlanRepository) {
        this.repository = repository;
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * Deletes a service assignment by its unique identifier.
     * Validates that no infrastructure plans reference this service assignment before deletion.
     *
     * @param id the unique identifier of the service assignment to delete
     * @return the deleted service assignment
     * @throws NoSuchElementException if no service assignment is found with the given identifier
     * @throws ReferentialIntegrityException if infrastructure plans reference this service assignment
     */
    @Override
    public ServiceAssignment delete(UUID id) {
        ServiceAssignment existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
        
        // Check referential integrity
        List<?> referencingPlans = infrastructurePlanRepository.findByServiceAssignmentId(id);
        if (!referencingPlans.isEmpty()) {
            List<UUID> referencingIds = referencingPlans.stream()
                .map(plan -> ((es.ull.project.domain.entity.InfrastructurePlan) plan).getId())
                .toList();
            throw new ReferentialIntegrityException(
                "ServiceAssignment",
                id.toString(),
                "InfrastructurePlan",
                referencingIds
            );
        }
        
        this.repository.delete(existing);
        return existing;
    }
}
