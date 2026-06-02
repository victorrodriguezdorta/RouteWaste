package es.ull.project.application.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.ull.project.application.port.algorithm.AlgorithmExecutionPayloadSerializer;
import es.ull.project.application.service.algorithm.RunAlgorithmExecutionJobService;
import es.ull.project.application.port.infrastructureplan.InfrastructurePlanExecutionNotifier;
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
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunAlgorithmExecutionJobServiceTests {

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

    @BeforeEach
    void setUp() {
        this.runAlgorithmExecutionJobService = new RunAlgorithmExecutionJobService(
                this.executeAlgorithmUseCase,
                this.runAlgorithmUseCase,
                this.persistAlgorithmExecutionResultUseCase,
                this.executionNotifier,
                this.payloadSerializer);
    }

    @Test
    void run_completesPendingPlanWhenPipelineSucceeds() {
        UUID planId = UUID.randomUUID();
        AlgorithmExecutionJobCommand command = new AlgorithmExecutionJobCommand(
                planId,
                List.of(new AlgorithmExecutionSelection(UUID.randomUUID(), List.of(UUID.randomUUID()))),
                List.of(UUID.randomUUID()),
                new NumberOfDays(3),
                new AveragePickupTimeMinutes(10),
                new MaximumBudget(1000.0),
                new AlgorithmJsonPayload("{\"numberOfDays\":3}"));

        when(this.executeAlgorithmUseCase.execute(any(), any(), any(), any()))
                .thenReturn(new AlgorithmExecutionResult(List.of(), List.of(), new NumberOfDays(3), new AveragePickupTimeMinutes(10)));
        when(this.payloadSerializer.serialize(any(), any())).thenReturn(new AlgorithmJsonPayload("{\"processed\":true}"));
        when(this.runAlgorithmUseCase.execute(any())).thenReturn(new AlgorithmJsonPayload("{\"clusters\":[],\"dailyPlans\":[]}"));
        InfrastructurePlan completedPlan = new InfrastructurePlan(
                new PlanningPeriod("2026"),
                new MaximumBudget(1000.0),
                null,
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

    @Test
    void run_marksPlanFailedWhenRunnerThrows() {
        UUID planId = UUID.randomUUID();
        AlgorithmExecutionJobCommand command = new AlgorithmExecutionJobCommand(
                planId,
                List.of(new AlgorithmExecutionSelection(UUID.randomUUID(), List.of(UUID.randomUUID()))),
                List.of(UUID.randomUUID()),
                new NumberOfDays(1),
                new AveragePickupTimeMinutes(5),
                new MaximumBudget(500.0),
                null);

        when(this.executeAlgorithmUseCase.execute(any(), any(), any(), any()))
                .thenReturn(new AlgorithmExecutionResult(List.of(), List.of(), new NumberOfDays(1), new AveragePickupTimeMinutes(5)));
        when(this.payloadSerializer.serialize(any(), any())).thenReturn(new AlgorithmJsonPayload("{\"processed\":true}"));
        when(this.runAlgorithmUseCase.execute(any())).thenThrow(new RuntimeException("Docker failed"));
        InfrastructurePlan failedPlan = new InfrastructurePlan(
                new PlanningPeriod("2026"),
                new MaximumBudget(500.0),
                null,
                new NumberOfDays(1),
                new AveragePickupTimeMinutes(5),
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
        failedPlan.markExecutionFailed("Docker failed");
        InfrastructurePlanFailureReason failureReason = new InfrastructurePlanFailureReason("Docker failed");
        when(this.persistAlgorithmExecutionResultUseCase.markExecutionFailed(planId, failureReason))
                .thenReturn(failedPlan);

        this.runAlgorithmExecutionJobService.run(command);

        verify(this.persistAlgorithmExecutionResultUseCase).markExecutionFailed(planId, failureReason);
        verify(this.executionNotifier).notifyPlanUpdated(
                eq(failedPlan.getId()), eq(InfrastructurePlanExecutionState.FAILED), eq(failureReason));
    }
}
