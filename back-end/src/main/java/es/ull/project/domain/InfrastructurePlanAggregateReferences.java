package es.ull.project.domain;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Stop;
import java.util.UUID;

/**
 * Detects whether an infrastructure plan aggregate references a master entity id
 * (facility, container, or vehicle) anywhere in the persisted snapshot graph.
 */
public final class InfrastructurePlanAggregateReferences {

    private InfrastructurePlanAggregateReferences() {
    }

    /**
     * @param plan     loaded plan (selected facilities, assignments, daily plans may be populated)
     * @param entityId facility, container, or vehicle id
     * @return true if the plan references that id
     */
    public static boolean referencesEntity(InfrastructurePlan plan, UUID entityId) {
        if (plan == null || entityId == null) {
            return false;
        }
        String json = plan.getExecutionRequestJson().orElse(null);
        if (InfrastructurePlanExecutionRequestReferences.containsQuotedEntityId(json, entityId)) {
            return true;
        }
        for (Facility facility : plan.getSelectedFacilities()) {
            if (facility != null && entityId.equals(facility.getId())) {
                return true;
            }
        }
        for (ServiceAssignment assignment : plan.getServiceAssignments()) {
            if (assignment.getFacility() != null && entityId.equals(assignment.getFacility().getId())) {
                return true;
            }
            for (Container container : assignment.getAssignedContainers()) {
                if (container != null && entityId.equals(container.getId())) {
                    return true;
                }
            }
        }
        for (DailyPlan dailyPlan : plan.getDailyPlans()) {
            if (dailyPlan.getFacility() != null && entityId.equals(dailyPlan.getFacility().getId())) {
                return true;
            }
            if (dailyPlan.getVehicle() != null && entityId.equals(dailyPlan.getVehicle().getId())) {
                return true;
            }
            for (Stop stop : dailyPlan.getStops()) {
                if (stop.getContainer() != null && entityId.equals(stop.getContainer().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
