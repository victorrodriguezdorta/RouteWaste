package es.ull.project.application.service.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ull.project.adapter.rest.mapper.AlgorithmExecutionResponseMapper;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionJobCommand;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
import es.ull.project.adapter.rest.sse.InfrastructurePlanExecutionEventPublisher;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Runs the full algorithm pipeline asynchronously for a pending infrastructure plan.
 */
@Service
public class AlgorithmExecutionJobService {

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmExecutionJobService.class);
    private static final String ERR_SERIALIZE = "Failed to serialize the processed algorithm payload";

    private final ExecuteAlgorithmUseCase executeAlgorithmUseCase;
    private final RunAlgorithmUseCase runAlgorithmUseCase;
    private final PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase;
    private final InfrastructurePlanExecutionEventPublisher executionEventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Creates the asynchronous job service.
     *
     * @param executeAlgorithmUseCase                 resolves request identifiers
     * @param runAlgorithmUseCase                     invokes the external algorithm runner
     * @param persistAlgorithmExecutionResultUseCase  completes or fails the pending plan
     * @param executionEventPublisher                 SSE broadcaster for plan state changes
     * @param objectMapper                            serializes processed payloads
     */
    public AlgorithmExecutionJobService(
            ExecuteAlgorithmUseCase executeAlgorithmUseCase,
            RunAlgorithmUseCase runAlgorithmUseCase,
            PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase,
            InfrastructurePlanExecutionEventPublisher executionEventPublisher,
            ObjectMapper objectMapper) {
        this.executeAlgorithmUseCase = executeAlgorithmUseCase;
        this.runAlgorithmUseCase = runAlgorithmUseCase;
        this.persistAlgorithmExecutionResultUseCase = persistAlgorithmExecutionResultUseCase;
        this.executionEventPublisher = executionEventPublisher;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes the algorithm pipeline and completes the pending infrastructure plan.
     *
     * @param command job input including the pending plan identifier
     */
    @Async
    public void runAsync(AlgorithmExecutionJobCommand command) {
        logger.info("=== ASYNC ALGORITHM JOB START === planId={}", command.planId());
        try {
            AlgorithmExecutionResult result = this.executeAlgorithmUseCase.execute(
                    command.facilitiesWithVehicles(),
                    command.selectedContainerIds(),
                    command.numberOfDays(),
                    command.averagePickupTimeMinutes());
            AlgorithmExecutionResponseBody processedResponseBody = AlgorithmExecutionResponseMapper.toResponseBody(result);
            processedResponseBody.maxBudget = command.maxBudget();
            String processedJson = this.serializeProcessedResponse(processedResponseBody);
            AlgorithmJsonPayload algorithmJsonPayload = this.runAlgorithmUseCase.execute(new AlgorithmJsonPayload(processedJson));
            InfrastructurePlan completedPlan = this.persistAlgorithmExecutionResultUseCase.complete(
                    command.planId(),
                    algorithmJsonPayload,
                    command.numberOfDays(),
                    command.averagePickupTimeMinutes(),
                    command.maxBudget(),
                    command.executionRequestJson());
            this.executionEventPublisher.publishPlanUpdated(
                    completedPlan.getId(),
                    completedPlan.getExecutionState(),
                    completedPlan.getFailureReason().orElse(null));
            logger.info("=== ASYNC ALGORITHM JOB END === planId={}", command.planId());
        } catch (RuntimeException e) {
            logger.error("Async algorithm execution failed for plan {}", command.planId(), e);
            this.markPlanFailed(command, e);
        }
    }

    private String serializeProcessedResponse(AlgorithmExecutionResponseBody processedResponseBody) {
        try {
            return this.objectMapper.writeValueAsString(processedResponseBody);
        } catch (JsonProcessingException e) {
            throw new AlgorithmExecutionException(ERR_SERIALIZE, e);
        }
    }

    private void markPlanFailed(AlgorithmExecutionJobCommand command, RuntimeException cause) {
        String reason = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
        try {
            InfrastructurePlan failedPlan =
                    this.persistAlgorithmExecutionResultUseCase.markExecutionFailed(command.planId(), reason);
            this.executionEventPublisher.publishPlanUpdated(
                    failedPlan.getId(),
                    failedPlan.getExecutionState(),
                    failedPlan.getFailureReason().orElse(null));
        } catch (RuntimeException markFailed) {
            logger.error("Could not mark infrastructure plan {} as FAILED", command.planId(), markFailed);
            this.executionEventPublisher.publishPlanUpdated(
                    command.planId(), InfrastructurePlanExecutionState.FAILED, reason);
        }
    }
}
