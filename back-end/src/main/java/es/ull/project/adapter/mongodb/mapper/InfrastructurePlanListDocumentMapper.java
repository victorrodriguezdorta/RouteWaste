package es.ull.project.adapter.mongodb.mapper;

/**
 * Maps MongoDB infrastructure plan documents to lightweight domain aggregates for list queries.
 * Avoids loading daily plans, service assignments, facilities, and other nested collections.
 */
import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.domain.InfrastructurePlanReferences;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import java.util.UUID;
import org.bson.Document;

public final class InfrastructurePlanListDocumentMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private InfrastructurePlanListDocumentMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Builds an {@link InfrastructurePlan} containing only the fields required for list views.
     *
     * @param document projected MongoDB document
     * @return list-summary infrastructure plan
     */
    public static InfrastructurePlan toInfrastructurePlan(Document document) {
        UUID id = document.get(MongoFields.ID, UUID.class);
        TotalCost estimatedTotalCost = readEstimatedTotalCost(document);
        Integer numberOfDaysRaw = document.getInteger(MongoFields.NUMBER_OF_DAYS);
        Integer averagePickupTimeMinutesRaw = document.getInteger(MongoFields.AVERAGE_PICKUP_TIME_MINUTES);
        String executedAtRaw = document.getString(MongoFields.EXECUTED_AT);
        NumberOfDays numberOfDays = numberOfDaysRaw != null ? new NumberOfDays(numberOfDaysRaw) : null;
        AveragePickupTimeMinutes averagePickupTimeMinutes = averagePickupTimeMinutesRaw != null
                ? new AveragePickupTimeMinutes(averagePickupTimeMinutesRaw)
                : null;
        ExecutedAt executedAt = executedAtRaw != null ? new ExecutedAt(executedAtRaw) : null;
        InfrastructurePlanValidityState validityState =
                InfrastructurePlanValidityState.fromStoredString(document.getString(MongoFields.VALIDITY_STATE));
        InfrastructurePlanExecutionState executionState =
                InfrastructurePlanExecutionState.fromStoredString(document.getString(MongoFields.EXECUTION_STATE));
        if (validityState == InfrastructurePlanValidityState.VALID
                && executionState == InfrastructurePlanExecutionState.RUNNING) {
            validityState = InfrastructurePlanValidityState.RUNNING;
        }
        String failureReason = document.getString(MongoFields.FAILURE_REASON);
        return InfrastructurePlanReferences.forListSummary(
                id,
                estimatedTotalCost,
                numberOfDays,
                averagePickupTimeMinutes,
                executedAt,
                validityState,
                executionState,
                failureReason);
    }

    /**
     * Reads the estimated total cost embedded document from a list projection.
     *
     * @param document projected MongoDB document
     * @return parsed total cost, or {@code null} when absent or incomplete
     */
    private static TotalCost readEstimatedTotalCost(Document document) {
        Document totalCostDocument = document.get(MongoFields.ESTIMATED_TOTAL_COST, Document.class);
        if (totalCostDocument == null) {
            return null;
        }
        Double totalCostAmount = totalCostDocument.getDouble(MongoFields.ESTIMATED_TOTAL_COST_AMOUNT);
        if (totalCostAmount == null) {
            return null;
        }
        String totalCostCurrencyCode = totalCostDocument.getString(MongoFields.ESTIMATED_TOTAL_COST_CURRENCY);
        if (totalCostCurrencyCode != null) {
            return new TotalCost(totalCostAmount, new Currency(totalCostCurrencyCode));
        }
        return new TotalCost(totalCostAmount);
    }
}
