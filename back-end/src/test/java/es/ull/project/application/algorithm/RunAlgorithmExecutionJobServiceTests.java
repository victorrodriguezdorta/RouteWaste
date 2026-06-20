package es.ull.project.application.algorithm;

import es.ull.project.application.repository.algorithm.AlgorithmExecutionPayloadSerializer;
import es.ull.project.application.repository.infrastructureplan.InfrastructurePlanExecutionNotifier;
import es.ull.project.application.service.algorithm.RunAlgorithmExecutionJobService;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionJobCommand;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionSelection;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
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
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunAlgorithmExecutionJobServiceTests {

    private static final String DOCKER_FAILED_MESSAGE = "Docker failed";

    @Mock
    private ExecuteAlgorithmUseCase executeAlgorithmUseCase;

    @Mock
    private RunAlgorithmUseCase runAlgorithmUseCase;

    @Mock
    private PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase;

    @Mock
    private InfrastructurePlanExecutionNotifier executionNotifier;

    @Mock
    private AlgorithmExecutionPayloadSerializer payloadSerializer;

    private RunAlgorithmExecutionJobService runAlgorithmExecutionJobService;

    /**
     * Should create the service under test with mocked collaborators.
     */
    @BeforeEach
    void setUp() {
        this.runAlgorithmExecutionJobService = new RunAlgorithmExecutionJobService(
                this.executeAlgorithmUseCase,
                this.runAlgorithmUseCase,
                this.persistAlgorithmExecutionResultUseCase,
                this.executionNotifier,
                this.payloadSerializer);
    }

    /**
     * Should complete the pending infrastructure plan when the algorithm pipeline succeeds.
     */
    @Test
    void runCompletesPendingPlanWhenPipelineSucceeds() {
        UUID planId = UUID.randomUUID();
        AlgorithmExecutionJobCommand command = new AlgorithmExecutionJobCommand(
                planId,
                List.of(new AlgorithmExecutionSelection(UUID.randomUUID(), List.of(UUID.randomUUID()))),
                List.of(UUID.randomUUID()),
                new NumberOfDays(3),
                new AveragePickupTimeMinutes(10),
                new CollectionStartTime(LocalTime.of(8, 0)),
                new AverageTransferTimeMinutes(10),
                GreedyWeights.defaultWeights(),
                new MaximumBudget(1000.0),
                new AlgorithmJsonPayload("{\"numberOfDays\":3}"));
        when(this.executeAlgorithmUseCase.execute(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new AlgorithmExecutionResult(List.of(), List.of(), new NumberOfDays(3), new AveragePickupTimeMinutes(10),
                        new CollectionStartTime(LocalTime.of(8, 0)), new AverageTransferTimeMinutes(10), GreedyWeights.defaultWeights()));
        when(this.payloadSerializer.serialize(any(), any())).thenReturn(new AlgorithmJsonPayload("{\"processed\":true}"));
        when(this.runAlgorithmUseCase.execute(any())).thenReturn(new AlgorithmJsonPayload("{\"clusters\":[],\"dailyPlans\":[]}"));
        InfrastructurePlan completedPlan = new InfrastructurePlan(
                new PlanningPeriod("2026"),
                null,
                new MaximumBudget(1000.0),
                new NumberOfDays(3),
                new AveragePickupTimeMinutes(10),
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
        when(this.persistAlgorithmExecutionResultUseCase.complete(
                any(), any(), any(), any(), any(), any())).thenReturn(completedPlan);
        this.runAlgorithmExecutionJobService.run(command);
        ArgumentCaptor<UUID> planIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(this.persistAlgorithmExecutionResultUseCase).complete(
                planIdCaptor.capture(),
                any(AlgorithmJsonPayload.class),
                eq(new NumberOfDays(3)),
                eq(new AveragePickupTimeMinutes(10)),
                eq(new MaximumBudget(1000.0)),
                eq(command.executionRequestJson()));
        assertEquals(planId, planIdCaptor.getValue());
    }

    /**
     * Should mark the pending infrastructure plan as failed when the runner throws.
     */
    @Test
    void runMarksPlanFailedWhenRunnerThrows() {
        UUID planId = UUID.randomUUID();
        AlgorithmExecutionJobCommand command = new AlgorithmExecutionJobCommand(
                planId,
                List.of(new AlgorithmExecutionSelection(UUID.randomUUID(), List.of(UUID.randomUUID()))),
                List.of(UUID.randomUUID()),
                new NumberOfDays(1),
                new AveragePickupTimeMinutes(5),
                new CollectionStartTime(LocalTime.of(8, 0)),
                new AverageTransferTimeMinutes(10),
                GreedyWeights.defaultWeights(),
                new MaximumBudget(500.0),
                null);
        when(this.executeAlgorithmUseCase.execute(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new AlgorithmExecutionResult(List.of(), List.of(), new NumberOfDays(1), new AveragePickupTimeMinutes(5),
                        new CollectionStartTime(LocalTime.of(8, 0)), new AverageTransferTimeMinutes(10), GreedyWeights.defaultWeights()));
        when(this.payloadSerializer.serialize(any(), any())).thenReturn(new AlgorithmJsonPayload("{\"processed\":true}"));
        when(this.runAlgorithmUseCase.execute(any())).thenThrow(new RuntimeException(DOCKER_FAILED_MESSAGE));
        InfrastructurePlan failedPlan = new InfrastructurePlan(
                new PlanningPeriod("2026"),
                null,
                new MaximumBudget(500.0),
                new NumberOfDays(1),
                new AveragePickupTimeMinutes(5),
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
        failedPlan.markExecutionFailed(DOCKER_FAILED_MESSAGE);
        InfrastructurePlanFailureReason failureReason = new InfrastructurePlanFailureReason(DOCKER_FAILED_MESSAGE);
        when(this.persistAlgorithmExecutionResultUseCase.markExecutionFailed(planId, failureReason))
                .thenReturn(failedPlan);
        this.runAlgorithmExecutionJobService.run(command);
        verify(this.persistAlgorithmExecutionResultUseCase).markExecutionFailed(planId, failureReason);
        verify(this.executionNotifier).notifyPlanUpdated(
                eq(failedPlan.getId()), eq(InfrastructurePlanExecutionState.FAILED), eq(failureReason));
    }
}
