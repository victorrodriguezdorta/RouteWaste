package es.ull.project.adapter.mongodb.mapper;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.adapter.rest.mapper.InfrastructurePlanListResponseMapper;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import java.util.UUID;
import org.bson.Document;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InfrastructurePlanListDocumentMapperTests {

    private static final String CURRENCY_CODE = "EUR";
    private static final String EXECUTED_AT_VALUE = "2026-06-01T10:15:30Z";

    /**
     * Verifies that a projected MongoDB document is mapped to a list infrastructure plan summary.
     */
    @Test
    void toInfrastructurePlanMapsProjectedDocumentWithoutNestedCollections() {
        UUID planId = UUID.randomUUID();
        Document estimatedTotalCost = new Document()
                .append(MongoFields.ESTIMATED_TOTAL_COST_AMOUNT, 1250.5)
                .append(MongoFields.ESTIMATED_TOTAL_COST_CURRENCY, CURRENCY_CODE);
        Document document = new Document()
                .append(MongoFields.ID, planId)
                .append(MongoFields.ESTIMATED_TOTAL_COST, estimatedTotalCost)
                .append(MongoFields.NUMBER_OF_DAYS, 300)
                .append(MongoFields.AVERAGE_PICKUP_TIME_MINUTES, 42)
                .append(MongoFields.EXECUTED_AT, EXECUTED_AT_VALUE)
                .append(MongoFields.VALIDITY_STATE, InfrastructurePlanValidityState.VALID.name())
                .append(MongoFields.EXECUTION_STATE, InfrastructurePlanExecutionState.COMPLETED.name());
        InfrastructurePlan plan = InfrastructurePlanListDocumentMapper.toInfrastructurePlan(document);
        InfrastructurePlanListResponseBody response = InfrastructurePlanListResponseMapper.toResponseBody(plan);
        assertEquals(planId, plan.getId());
        assertEquals(300, plan.getNumberOfDays().orElseThrow().getValue());
        assertEquals(42, plan.getAveragePickupTimeMinutes().orElseThrow().getValue());
        assertEquals(1250.5, plan.getEstimatedTotalCost().getAmount());
        assertTrue(plan.getDailyPlans().isEmpty());
        assertTrue(plan.getServiceAssignments().isEmpty());
        assertTrue(plan.getSelectedFacilities().isEmpty());
        assertEquals(planId, response.id);
        assertEquals(300, response.numberOfDays.getValue());
    }

    /**
     * Verifies that a running execution is exposed as a running validity state.
     */
    @Test
    void toInfrastructurePlanMapsRunningExecutionAsRunningValidity() {
        UUID planId = UUID.randomUUID();
        Document document = new Document()
                .append(MongoFields.ID, planId)
                .append(MongoFields.VALIDITY_STATE, InfrastructurePlanValidityState.VALID.name())
                .append(MongoFields.EXECUTION_STATE, InfrastructurePlanExecutionState.RUNNING.name());
        InfrastructurePlan plan = InfrastructurePlanListDocumentMapper.toInfrastructurePlan(document);
        assertEquals(InfrastructurePlanValidityState.RUNNING, plan.getValidityState());
        assertEquals(InfrastructurePlanExecutionState.RUNNING, plan.getExecutionState());
        assertEquals(0.0, plan.getEstimatedTotalCost().getAmount());
    }
}
