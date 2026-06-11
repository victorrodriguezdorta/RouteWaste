package es.ull.project.application.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.service.algorithm.CreatePendingInfrastructurePlanService;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePendingInfrastructurePlanServiceTests {

    @Mock
    private InfrastructurePlanRepository infrastructurePlanRepository;

    @Test
    void createPending_persistsRunningPlaceholderWithRequestMetadata() {
        when(this.infrastructurePlanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreatePendingInfrastructurePlanService service =
                new CreatePendingInfrastructurePlanService(this.infrastructurePlanRepository);

        String requestJson = "{\"numberOfDays\":3}";
        InfrastructurePlan plan = service.createPending(
                new NumberOfDays(3),
                new AveragePickupTimeMinutes(20),
                new CollectionStartTime(LocalTime.of(8, 0)),
                new AverageTransferTimeMinutes(10),
                GreedyWeights.defaultWeights(),
                new MaximumBudget(50_000.0),
                new AlgorithmJsonPayload(requestJson));

        assertNotNull(plan.getId());
        assertEquals(InfrastructurePlanExecutionState.RUNNING, plan.getExecutionState());
        assertTrue(plan.isExecutionRunning());
        assertEquals(InfrastructurePlanValidityState.RUNNING, plan.getValidityState());
        assertEquals(new NumberOfDays(3), plan.getNumberOfDays().orElseThrow());
        assertEquals(new AveragePickupTimeMinutes(20), plan.getAveragePickupTimeMinutes().orElseThrow());
        assertEquals(new CollectionStartTime(LocalTime.of(8, 0)), plan.getCollectionStartTime().orElseThrow());
        assertEquals(new AverageTransferTimeMinutes(10), plan.getAverageTransferTimeMinutes().orElseThrow());
        assertEquals(GreedyWeights.defaultWeights(), plan.getGreedyWeights().orElseThrow());
        assertEquals(requestJson, plan.getExecutionRequestJson().orElseThrow());
        assertTrue(plan.getExecutedAt().isPresent());
        assertEquals(0, plan.getSelectedFacilities().size());
        assertEquals(0, plan.getDailyPlanIds().size());

        ArgumentCaptor<InfrastructurePlan> savedPlanCaptor = ArgumentCaptor.forClass(InfrastructurePlan.class);
        verify(this.infrastructurePlanRepository).save(savedPlanCaptor.capture());
        assertEquals(plan.getId(), savedPlanCaptor.getValue().getId());
    }

    @Test
    void createPending_requiresMaxBudget() {
        CreatePendingInfrastructurePlanService service =
                new CreatePendingInfrastructurePlanService(this.infrastructurePlanRepository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createPending(
                        new NumberOfDays(1),
                        new AveragePickupTimeMinutes(10),
                        new CollectionStartTime(LocalTime.of(8, 0)),
                        new AverageTransferTimeMinutes(10),
                        GreedyWeights.defaultWeights(),
                        null,
                        null));
    }
}
