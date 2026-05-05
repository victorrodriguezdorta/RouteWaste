package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import java.util.List;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for deleting infrastructure plans from the system.
 * This service implements the {@link DeleteInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan deletion operations.

 */
public class DeleteInfrastructurePlanService implements DeleteInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;
    private final DailyPlanRepository dailyPlanRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;

    /**
     * Constructs a new DeleteInfrastructurePlanService with the specified repositories.
     *
     * @param repository                  the infrastructure plan repository
     * @param dailyPlanRepository         the daily plan repository for cascading deletes
     * @param serviceAssignmentRepository the service assignment repository for cascading deletes
     */
    public DeleteInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            DailyPlanRepository dailyPlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        this.repository = repository;
        this.dailyPlanRepository = dailyPlanRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    /**
     * Deletes an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan to delete
     * @return the deleted infrastructure plan
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier
     */
    @Override
    public InfrastructurePlan delete(UUID id) {
        InfrastructurePlan existing = this.repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));

        // Cascade delete all daily plans associated with this infrastructure plan
        List<DailyPlan> dailyPlans = dailyPlanRepository.findByInfrastructurePlanId(id);
        if (dailyPlans != null) {
            for (DailyPlan dp : dailyPlans) {
                dailyPlanRepository.delete(dp);
            }
        }

        // Cascade delete all service assignments (clusters) associated with this plan
        List<ServiceAssignment> assignments = existing.getServiceAssignments();
        if (assignments != null) {
            for (ServiceAssignment sa : assignments) {
                serviceAssignmentRepository.delete(sa);
            }
        }

        // Delete the parent infrastructure plan
        this.repository.delete(existing);
        return existing;
    }
}
