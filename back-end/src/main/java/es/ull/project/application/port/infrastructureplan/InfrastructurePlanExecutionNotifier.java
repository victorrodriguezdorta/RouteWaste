package es.ull.project.application.port.infrastructureplan;

import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import java.util.UUID;

/**
 * Notifies clients when an infrastructure plan execution state changes.
 */
public interface InfrastructurePlanExecutionNotifier {

    /**
     * Broadcasts an execution state update for the given plan.
     *
     * @param planId         updated plan identifier
     * @param executionState new execution lifecycle state
     * @param failureReason  optional failure description when execution failed
     */
    void notifyPlanUpdated(
            UUID planId,
            InfrastructurePlanExecutionState executionState,
            InfrastructurePlanFailureReason failureReason);
}
