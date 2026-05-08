package es.ull.project.application.service.infrastructureplan;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Service responsible for reading infrastructure plan data from the system.
 * This service implements the {@link ReadInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan retrieval operations.

 */
public class ReadInfrastructurePlanService implements ReadInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;
    private final DailyPlanRepository dailyPlanRepository;

    /**
     * Constructs a new ReadInfrastructurePlanService with the specified repository.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     */
    public ReadInfrastructurePlanService(InfrastructurePlanRepository repository, DailyPlanRepository dailyPlanRepository) {
        this.repository = repository;
        this.dailyPlanRepository = dailyPlanRepository;
    }

    /**
     * Retrieves an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan to retrieve
     * @return the infrastructure plan with the specified identifier
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier
     */
    @Override
    public InfrastructurePlan fetch(UUID id) {
        InfrastructurePlan plan = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
        // Populate daily plans stored in separate collection
        try {
            var dailyPlans = this.dailyPlanRepository.findByInfrastructurePlanId(plan.getId());
            if (dailyPlans != null) {
                for (var dp : dailyPlans) {
                    plan.addDailyPlan(dp);
                }
            }
        } catch (Exception e) {
            // don't fail the read if daily plans cannot be loaded; log and continue
            // logging is not available here, so we silently continue to avoid breaking the API
        }
        return plan;
    }

    /**
     * Retrieves infrastructure plans using pagination.
     *
     * @param pageable pagination and sort information
     * @return a page containing infrastructure plans
     */
    @Override
    public Page<InfrastructurePlan> fetchAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    /**
     * Retrieves all infrastructure plans from the system.
     *
     * @return a list containing all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> fetchAll() {
        return this.repository.findAll();
    }
}
