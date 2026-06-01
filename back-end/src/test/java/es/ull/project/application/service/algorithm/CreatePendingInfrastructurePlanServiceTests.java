package es.ull.project.application.service.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.ull.project.adapter.memory.InMemoryInfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import org.junit.jupiter.api.Test;

class CreatePendingInfrastructurePlanServiceTests {

    @Test
    void createPending_persistsRunningPlaceholderWithRequestMetadata() {
        InMemoryInfrastructurePlanRepository repository = new InMemoryInfrastructurePlanRepository();
        CreatePendingInfrastructurePlanService service = new CreatePendingInfrastructurePlanService(repository);

        String requestJson = "{\"numberOfDays\":3}";
        InfrastructurePlan plan = service.createPending(
                new NumberOfDays(3),
                new AveragePickupTimeMinutes(20),
                new MaximumBudget(50_000.0),
                new AlgorithmJsonPayload(requestJson));

        assertNotNull(plan.getId());
        assertEquals(InfrastructurePlanExecutionState.RUNNING, plan.getExecutionState());
        assertTrue(plan.isExecutionRunning());
        assertEquals(InfrastructurePlanValidityState.RUNNING, plan.getValidityState());
        assertEquals(new NumberOfDays(3), plan.getNumberOfDays().orElseThrow());
        assertEquals(new AveragePickupTimeMinutes(20), plan.getAveragePickupTimeMinutes().orElseThrow());
        assertEquals(requestJson, plan.getExecutionRequestJson().orElseThrow());
        assertTrue(plan.getExecutedAt().isPresent());
        assertEquals(0, plan.getSelectedFacilities().size());
        assertEquals(0, plan.getDailyPlanIds().size());
        assertEquals(1, repository.fetchAll().size());
    }

    @Test
    void createPending_requiresMaxBudget() {
        CreatePendingInfrastructurePlanService service =
                new CreatePendingInfrastructurePlanService(new InMemoryInfrastructurePlanRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createPending(
                        new NumberOfDays(1),
                        new AveragePickupTimeMinutes(10),
                        null,
                        null));
    }
}
