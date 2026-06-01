package es.ull.project.adapter.rest.sse;

import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import java.util.UUID;

/**
 * Payload broadcast to SSE clients when an infrastructure plan execution state changes.
 */
public class InfrastructurePlanExecutionEventBody {

    /**
     * Identifier of the infrastructure plan that changed.
     */
    public String planId;

    /**
     * New execution lifecycle state.
     */
    public InfrastructurePlanExecutionState executionState;

    /**
     * Optional failure description when {@link #executionState} is {@link InfrastructurePlanExecutionState#FAILED}.
     */
    public String failureReason;

    /**
     * Creates an event body for a plan state transition.
     *
     * @param planId          plan identifier
     * @param executionState  new execution state
     * @param failureReason   optional failure reason
     */
    public InfrastructurePlanExecutionEventBody(
            UUID planId,
            InfrastructurePlanExecutionState executionState,
            String failureReason) {
        this.planId = planId != null ? planId.toString() : null;
        this.executionState = executionState;
        this.failureReason = failureReason;
    }
}
