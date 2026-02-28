package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.infrastructureplan.CreateInfrastructurePlanUseCase;
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
 * Service responsible for creating new infrastructure plans in the system.
 * This service implements the {@link CreateInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan creation operations.
 */
public class CreateInfrastructurePlanService implements CreateInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;
    private final FacilityRepository facilityRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;

    /**
     * Constructs a new CreateInfrastructurePlanService with the specified repositories.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     * @param facilityRepository the facility repository used for fetching facilities
     * @param serviceAssignmentRepository the service assignment repository used for fetching assignments
     */
    public CreateInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            FacilityRepository facilityRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        this.repository = repository;
        this.facilityRepository = facilityRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * Creates a new infrastructure plan with the specified parameters.
     *
     * @param period the planning period for the infrastructure plan
     * @param maxBudget the maximum budget allocated for the plan
     * @param servicePolicies the service policies to be applied
     * @param selectedFacilityIds list of facility IDs to include in the plan
     * @param serviceAssignmentIds list of service assignment IDs to include in the plan
     * @return the newly created and persisted infrastructure plan
     * @throws NoSuchElementException if any facility or service assignment ID does not exist
     */
    @Override
    public InfrastructurePlan create(
            PlanningPeriod period, 
            MaximumBudget maxBudget, 
            ServicePolicies servicePolicies,
            List<UUID> selectedFacilityIds,
            List<UUID> serviceAssignmentIds) {
        InfrastructurePlan plan = new InfrastructurePlan(period, maxBudget, servicePolicies);
        if (selectedFacilityIds != null && !selectedFacilityIds.isEmpty()) {
            for (UUID facilityId : selectedFacilityIds) {
                Facility facility = this.facilityRepository.findById(facilityId)
                    .orElseThrow(() -> new NoSuchElementException("Facility not found with ID: " + facilityId));
                plan.addFacility(facility);
            }
        }
        if (serviceAssignmentIds != null && !serviceAssignmentIds.isEmpty()) {
            for (UUID assignmentId : serviceAssignmentIds) {
                ServiceAssignment assignment = this.serviceAssignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found with ID: " + assignmentId));
                plan.addServiceAssignment(assignment);
            }
        }
        InfrastructurePlan saved = this.repository.save(plan);
        return saved;
    }
}
