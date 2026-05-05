package es.ull.project.adapter.mongodb.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * InfrastructurePlanReadingConverter
 *
 * Custom converter for reading InfrastructurePlan entities from MongoDB documents.
 * This converter ensures proper deserialization of complex value objects and entity references.
 */
@ReadingConverter
public class InfrastructurePlanReadingConverter implements Converter<Document, InfrastructurePlan> {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructurePlanReadingConverter.class);

    private static final String ERROR_FACILITY_NOT_FOUND = "Facility with id '%s' not found when loading InfrastructurePlan";
    private static final String ERROR_SERVICE_ASSIGNMENT_NOT_FOUND = "ServiceAssignment with id '%s' not found when loading InfrastructurePlan";

    private final MongoConfiguration mongoConfiguration;

    /**
     * Creates a new InfrastructurePlanReadingConverter.
     *
     * @param mongoConfiguration MongoDB configuration with repository access.
     */
    public InfrastructurePlanReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document to an InfrastructurePlan entity.
     *
     * @param document The MongoDB document to convert.
     * @return The InfrastructurePlan entity.
     */
    @Override
    public InfrastructurePlan convert(@NonNull Document document) {
        logger.info("Reading InfrastructurePlan from MongoDB document");
        UUID id = document.get(MongoFields.ID, UUID.class);
        String periodValue = document.getString(MongoFields.PERIOD);
        PlanningPeriod period = new PlanningPeriod(periodValue);
        List<?> facilityIdsList = document.get(MongoFields.SELECTED_FACILITIES, List.class);
        List<Facility> selectedFacilities = new ArrayList<>();
        if (facilityIdsList != null) {
            for (Object idObj : facilityIdsList) {
                UUID facilityId = (UUID) idObj;
                Optional<Facility> optionalFacility = mongoConfiguration.facilityRepository().findById(facilityId);
                if (optionalFacility.isPresent()) {
                    selectedFacilities.add(optionalFacility.get());
                } else {
                    throw new IllegalStateException(String.format(ERROR_FACILITY_NOT_FOUND, facilityId));
                }
            }
        }
        List<?> assignmentIdsList = document.get(MongoFields.SERVICE_ASSIGNMENTS, List.class);
        List<ServiceAssignment> serviceAssignments = new ArrayList<>();
        if (assignmentIdsList != null) {
            for (Object idObj : assignmentIdsList) {
                UUID assignmentId = (UUID) idObj;
                Optional<ServiceAssignment> optionalAssignment = 
                    mongoConfiguration.serviceAssignmentRepository().findById(assignmentId);
                if (optionalAssignment.isPresent()) {
                    serviceAssignments.add(optionalAssignment.get());
                } else {
                    throw new IllegalStateException(String.format(ERROR_SERVICE_ASSIGNMENT_NOT_FOUND, assignmentId));
                }
            }
        }
        List<?> dailyPlanIdsList = document.get(MongoFields.DAILY_PLANS, List.class);
        // Domain model now stores DailyPlan entities; when reading from Mongo we
        // only have the stored IDs. We don't have the DailyPlan repository here
        // so populate an empty list of DailyPlan entities; callers may load them
        // via DailyPlanRepository if required.
        List<es.ull.project.domain.entity.DailyPlan> dailyPlans = new ArrayList<>();
        ServicePolicies servicePolicies = null;
        Document policiesDocument = document.get(MongoFields.SERVICE_POLICIES, Document.class);
        if (policiesDocument != null) {
            Double maxServiceDistance = policiesDocument.get(MongoFields.MAX_SERVICE_DISTANCE, Double.class);
            Integer maxServiceTime = policiesDocument.get(MongoFields.MAX_SERVICE_TIME, Integer.class);
            Integer maxInfrastructureCount = policiesDocument.get(MongoFields.MAX_INFRASTRUCTURE_COUNT, Integer.class);
            Double maxEmissions = policiesDocument.get(MongoFields.MAX_EMISSIONS, Double.class);
            servicePolicies = new ServicePolicies(
                maxServiceDistance,
                maxServiceTime,
                maxInfrastructureCount,
                maxEmissions
            );
        }
        Document maxBudgetDocument = document.get(MongoFields.MAX_BUDGET, Document.class);
        double maxBudgetAmount = maxBudgetDocument.getDouble(MongoFields.MAX_BUDGET_AMOUNT);
        String maxBudgetCurrencyCode = maxBudgetDocument.getString(MongoFields.MAX_BUDGET_CURRENCY);
        MaximumBudget maxBudget;
        if (maxBudgetCurrencyCode != null) {
            Currency maxBudgetCurrency = new Currency(maxBudgetCurrencyCode);
            maxBudget = new MaximumBudget(maxBudgetAmount, maxBudgetCurrency);
        } else {
            maxBudget = new MaximumBudget(maxBudgetAmount);
        }
        Document totalCostDocument = document.get(MongoFields.ESTIMATED_TOTAL_COST, Document.class);
        double totalCostAmount = totalCostDocument.getDouble(MongoFields.ESTIMATED_TOTAL_COST_AMOUNT);
        String totalCostCurrencyCode = totalCostDocument.getString(MongoFields.ESTIMATED_TOTAL_COST_CURRENCY);
        TotalCost estimatedTotalCost;
        if (totalCostCurrencyCode != null) {
            Currency totalCostCurrency = new Currency(totalCostCurrencyCode);
            estimatedTotalCost = new TotalCost(totalCostAmount, totalCostCurrency);
        } else {
            estimatedTotalCost = new TotalCost(totalCostAmount);
        }
        Double totalKilograms = document.getDouble(MongoFields.TOTAL_COLLECTED_KILOGRAMS);
        Double totalLiters = document.getDouble(MongoFields.TOTAL_COLLECTED_LITERS);
        Double totalDistance = document.getDouble(MongoFields.TOTAL_DISTANCE_METERS);
        Integer numberOfDays = document.getInteger(MongoFields.NUMBER_OF_DAYS);
        Integer averagePickupTimeMinutes = document.getInteger(MongoFields.AVERAGE_PICKUP_TIME_MINUTES);
        String executedAt = document.getString(MongoFields.EXECUTED_AT);
        
        CollectedWeightKilograms kg = totalKilograms != null ? CollectedWeightKilograms.fromKilograms(totalKilograms) : null;
        CollectedVolumeLiters liters = totalLiters != null ? CollectedVolumeLiters.fromLiters(totalLiters) : null;
        Distance distance = totalDistance != null ? Distance.fromMeters(totalDistance) : null;

        InfrastructurePlan plan = new InfrastructurePlan(
            id,
            period,
            selectedFacilities,
            serviceAssignments,
            dailyPlans,
            servicePolicies,
            maxBudget,
            estimatedTotalCost,
            kg,
            liters,
            distance,
            numberOfDays,
            averagePickupTimeMinutes,
            executedAt
        );
        
        if (kg != null && liters != null && distance != null) {
            plan.updateAlgorithmMetrics(kg, liters, distance);
        }
        
        return plan;
    }
}
