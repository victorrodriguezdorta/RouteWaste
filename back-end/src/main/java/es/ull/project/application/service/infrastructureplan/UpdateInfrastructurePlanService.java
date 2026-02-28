package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.infrastructureplan.UpdateInfrastructurePlanUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for updating existing infrastructure plans in the system.
 * This service implements the {@link UpdateInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan modification operations.
 */
public class UpdateInfrastructurePlanService implements UpdateInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;
    private final FacilityRepository facilityRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;

    /**
     * Constructs a new UpdateInfrastructurePlanService with the specified repositories.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     * @param facilityRepository the facility repository used for fetching facilities
     * @param serviceAssignmentRepository the service assignment repository used for fetching assignments
     */
    public UpdateInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            FacilityRepository facilityRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        this.repository = repository;
        this.facilityRepository = facilityRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * Updates an existing infrastructure plan with the specified parameters.
     * <p>
     * Only non-null parameters will be updated. Null values are ignored,
     * allowing for partial updates.
     * </p>
     *
     * @param id the unique identifier of the infrastructure plan to update
     * @param newPeriod the new planning period, or null to keep the current value
     * @param newMaxBudget the new maximum budget, or null to keep the current value
     * @param newServicePolicies the new service policies, or null to keep the current value
     * @param selectedFacilityIds list of facility IDs to include in the plan (replaces existing), or null to keep current
     * @param serviceAssignmentIds list of service assignment IDs to include in the plan (replaces existing), or null to keep current
     * @return the updated infrastructure plan
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier, or if any facility or service assignment ID does not exist
     */
    @Override
    public InfrastructurePlan update(
            UUID id,
            PlanningPeriod newPeriod,
            MaximumBudget newMaxBudget,
            ServicePolicies newServicePolicies,
            List<UUID> selectedFacilityIds,
            List<UUID> serviceAssignmentIds) {
        InfrastructurePlan existing = this.repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
        if (newPeriod != null) {
            existing.updatePeriod(newPeriod);
        }
        if (newMaxBudget != null) {
            existing.updateMaxBudget(newMaxBudget);
        }
        if (newServicePolicies != null) {
            existing.updateServicePolicies(newServicePolicies);
        }
        if (selectedFacilityIds != null) {
            existing.clearFacilities();
            for (UUID facilityId : selectedFacilityIds) {
                Facility facility = this.facilityRepository.findById(facilityId)
                        .orElseThrow(() -> new NoSuchElementException("Facility not found with ID: " + facilityId));
                existing.addFacility(facility);
            }
        }
        if (serviceAssignmentIds != null) {
            existing.clearServiceAssignments();
            for (UUID assignmentId : serviceAssignmentIds) {
                ServiceAssignment assignment = this.serviceAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found with ID: " + assignmentId));
                existing.addServiceAssignment(assignment);
            }
        }
        InfrastructurePlan saved = this.repository.save(existing);
        return saved;
    }
}