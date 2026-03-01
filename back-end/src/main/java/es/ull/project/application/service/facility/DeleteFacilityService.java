package es.ull.project.application.service.facility;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.exception.ReferentialIntegrityException;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.domain.entity.Facility;

/**
 * Service implementation for deleting facilities.
 * This service handles the business logic for facility deletion operations.
 */
public class DeleteFacilityService implements DeleteFacilityUseCase {

    private final FacilityRepository repository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final InfrastructurePlanRepository infrastructurePlanRepository;

    /**
     * Constructs a new DeleteFacilityService with the specified repositories.
     * @param repository the facility repository for persistence operations
     * @param serviceAssignmentRepository the service assignment repository for referential integrity checks
     * @param infrastructurePlanRepository the infrastructure plan repository for referential integrity checks
     */
    public DeleteFacilityService(FacilityRepository repository,
                                  ServiceAssignmentRepository serviceAssignmentRepository,
                                  InfrastructurePlanRepository infrastructurePlanRepository) {
        this.repository = repository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * Deletes a facility by its unique identifier.
     * Validates that no service assignments or infrastructure plans reference this facility before deletion.
     * @param id the unique identifier of the facility to delete
     * @return the deleted facility
     * @throws NoSuchElementException if no facility is found with the given id
     * @throws ReferentialIntegrityException if service assignments or infrastructure plans reference this facility
     */
    @Override
    public Facility delete(UUID id) {
        Facility existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
        
        // Check referential integrity - ServiceAssignments
        List<?> referencingAssignments = serviceAssignmentRepository.findByFacilityId(id);
        if (!referencingAssignments.isEmpty()) {
            List<UUID> referencingIds = referencingAssignments.stream()
                .map(sa -> ((es.ull.project.domain.entity.ServiceAssignment) sa).getId())
                .toList();
            throw new ReferentialIntegrityException(
                "Facility",
                id.toString(),
                "ServiceAssignment",
                referencingIds
            );
        }
        
        // Check referential integrity - InfrastructurePlans
        List<?> referencingPlans = infrastructurePlanRepository.findByFacilityId(id);
        if (!referencingPlans.isEmpty()) {
            List<UUID> referencingIds = referencingPlans.stream()
                .map(plan -> ((es.ull.project.domain.entity.InfrastructurePlan) plan).getId())
                .toList();
            throw new ReferentialIntegrityException(
                "Facility",
                id.toString(),
                "InfrastructurePlan",
                referencingIds
            );
        }
        
        this.repository.delete(existing);
        return existing;
    }
}
