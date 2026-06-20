package es.ull.project.application.service.algorithm;

import es.ull.project.application.repository.algorithm.AlgorithmExecutionPayloadSerializer;
import es.ull.project.application.repository.infrastructureplan.InfrastructurePlanExecutionNotifier;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionJobCommand;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs the full algorithm pipeline for a pending infrastructure plan.
 */
public class RunAlgorithmExecutionJobService implements RunAlgorithmExecutionJobUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RunAlgorithmExecutionJobService.class);

    private final ExecuteAlgorithmUseCase executeAlgorithmUseCase;
    private final RunAlgorithmUseCase runAlgorithmUseCase;
    private final PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase;
    private final InfrastructurePlanExecutionNotifier executionNotifier;
    private final AlgorithmExecutionPayloadSerializer payloadSerializer;

    /**
     * Creates the algorithm execution job service.
     *
     * @param executeAlgorithmUseCase                resolves request identifiers
     * @param runAlgorithmUseCase                    invokes the external algorithm runner
     * @param persistAlgorithmExecutionResultUseCase completes or fails the pending plan
     * @param executionNotifier                      notifies clients of plan state changes
     * @param payloadSerializer                      builds JSON for the algorithm runner
     */
    public RunAlgorithmExecutionJobService(
            ExecuteAlgorithmUseCase executeAlgorithmUseCase,
            RunAlgorithmUseCase runAlgorithmUseCase,
            PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase,
            InfrastructurePlanExecutionNotifier executionNotifier,
            AlgorithmExecutionPayloadSerializer payloadSerializer) {
        this.executeAlgorithmUseCase = executeAlgorithmUseCase;
        this.runAlgorithmUseCase = runAlgorithmUseCase;
        this.persistAlgorithmExecutionResultUseCase = persistAlgorithmExecutionResultUseCase;
        this.executionNotifier = executionNotifier;
        this.payloadSerializer = payloadSerializer;
    }

    /**
     * Runs the algorithm pipeline for the pending plan described by the command.
     *
     * @param command job input including the pending plan identifier
     */
    @Override
    public void run(AlgorithmExecutionJobCommand command) {
        logger.info("=== ASYNC ALGORITHM JOB START === planId={}", command.planId());
        try {
            AlgorithmExecutionResult result = this.executeAlgorithmUseCase.execute(
                    command.facilitiesWithVehicles(),
                    command.selectedContainerIds(),
                    command.numberOfDays(),
                    command.averagePickupTimeMinutes(),
                    command.collectionStartTime(),
                    command.averageTransferTimeMinutes(),
                    command.greedyWeights());
            AlgorithmJsonPayload processedJson = this.payloadSerializer.serialize(result, command.maxBudget());
            AlgorithmJsonPayload algorithmJsonPayload = this.runAlgorithmUseCase.execute(processedJson);
            InfrastructurePlan completedPlan = this.persistAlgorithmExecutionResultUseCase.complete(
                    command.planId(),
                    algorithmJsonPayload,
                    command.numberOfDays(),
                    command.averagePickupTimeMinutes(),
                    command.maxBudget(),
                    command.executionRequestJson());
            this.notifyPlanUpdated(completedPlan);
            logger.info("=== ASYNC ALGORITHM JOB END === planId={}", command.planId());
        } catch (RuntimeException exception) {
            logger.error("Async algorithm execution failed for plan {}", command.planId(), exception);
            this.markPlanFailed(command, exception);
        }
    }

    /**
     * Marks the pending plan as failed and notifies subscribers of the state change.
     *
     * @param command job input including the pending plan identifier
     * @param cause   failure that stopped execution
     */
    private void markPlanFailed(AlgorithmExecutionJobCommand command, RuntimeException cause) {
        String reasonText = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
        InfrastructurePlanFailureReason failureReason = InfrastructurePlanFailureReason.fromNullable(reasonText);
        try {
            InfrastructurePlan failedPlan = this.persistAlgorithmExecutionResultUseCase.markExecutionFailed(
                    command.planId(), failureReason);
            this.notifyPlanUpdated(failedPlan);
        } catch (RuntimeException markFailed) {
            logger.error("Could not mark infrastructure plan {} as FAILED", command.planId(), markFailed);
            this.executionNotifier.notifyPlanUpdated(
                    command.planId(), InfrastructurePlanExecutionState.FAILED, failureReason);
        }
    }

    /**
     * Notifies clients using the persisted plan state.
     *
     * @param plan infrastructure plan after persistence
     */
    private void notifyPlanUpdated(InfrastructurePlan plan) {
        InfrastructurePlanFailureReason failureReason = plan.getFailureReason()
                .map(InfrastructurePlanFailureReason::new)
                .orElse(null);
        this.executionNotifier.notifyPlanUpdated(
                plan.getId(),
                plan.getExecutionState(),
                failureReason);
    }
}
