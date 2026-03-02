package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.fields.MongoFields;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * InfrastructurePlanWritingConverter
 *
 * Custom converter for writing InfrastructurePlan entities to MongoDB documents.
 * This converter ensures proper serialization of complex value objects and entity references.
 */
@WritingConverter
public class InfrastructurePlanWritingConverter implements Converter<InfrastructurePlan, Document> {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructurePlanWritingConverter.class);

    /**
     * Converts an InfrastructurePlan entity to a MongoDB Document.
     *
     * @param plan The InfrastructurePlan entity to convert.
     * @return The MongoDB Document representation of the InfrastructurePlan.
     */
    @Override
    public Document convert(@NonNull InfrastructurePlan plan) {
        logger.info("InfrastructurePlan with id '{}' to be written", plan.getId());
        Document document = new Document();
        document.put(MongoFields.ID, plan.getId());
        document.put(MongoFields.PERIOD, plan.getPeriod().getValue());
        final List<UUID> facilityIds = new ArrayList<>(plan.getSelectedFacilities().size());
        for (Facility facility : plan.getSelectedFacilities()) {
            facilityIds.add(facility.getId());
        }
        document.put(MongoFields.SELECTED_FACILITIES, facilityIds);
        final List<UUID> assignmentIds = new ArrayList<>(plan.getServiceAssignments().size());
        for (ServiceAssignment assignment : plan.getServiceAssignments()) {
            assignmentIds.add(assignment.getId());
        }
        document.put(MongoFields.SERVICE_ASSIGNMENTS, assignmentIds);
        if (plan.getServicePolicies() != null) {
            Document policiesDocument = new Document();
            plan.getServicePolicies().getMaxServiceDistance().ifPresent(distance ->
                policiesDocument.put(MongoFields.MAX_SERVICE_DISTANCE, distance)
            );
            plan.getServicePolicies().getMaxServiceTime().ifPresent(time ->
                policiesDocument.put(MongoFields.MAX_SERVICE_TIME, time)
            );
            plan.getServicePolicies().getMaxInfrastructureCount().ifPresent(count ->
                policiesDocument.put(MongoFields.MAX_INFRASTRUCTURE_COUNT, count)
            );
            plan.getServicePolicies().getMaxEmissions().ifPresent(emissions ->
                policiesDocument.put(MongoFields.MAX_EMISSIONS, emissions)
            );
            document.put(MongoFields.SERVICE_POLICIES, policiesDocument);
        }
        Document maxBudgetDocument = new Document();
        maxBudgetDocument.put(MongoFields.MAX_BUDGET_AMOUNT, plan.getMaxBudget().getAmount());
        plan.getMaxBudget().getCurrency().ifPresent(currency ->
            maxBudgetDocument.put(MongoFields.MAX_BUDGET_CURRENCY, currency.getCode())
        );
        document.put(MongoFields.MAX_BUDGET, maxBudgetDocument);
        Document totalCostDocument = new Document();
        totalCostDocument.put(MongoFields.ESTIMATED_TOTAL_COST_AMOUNT, 
            plan.getEstimatedTotalCost().getAmount());
        plan.getEstimatedTotalCost().getCurrency().ifPresent(currency ->
            totalCostDocument.put(MongoFields.ESTIMATED_TOTAL_COST_CURRENCY, currency.getCode())
        );
        document.put(MongoFields.ESTIMATED_TOTAL_COST, totalCostDocument);
        return document;
    }
}
