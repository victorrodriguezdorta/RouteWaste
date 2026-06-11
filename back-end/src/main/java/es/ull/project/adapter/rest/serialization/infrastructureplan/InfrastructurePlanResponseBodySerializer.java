package es.ull.project.adapter.rest.serialization.infrastructureplan;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import java.io.IOException;

/**
 * Custom JSON serializer for InfrastructurePlanResponseBody
 * This serializer manually controls how InfrastructurePlan response data is converted to JSON format
 * Includes complete facility and container information in the response
 */
public class InfrastructurePlanResponseBodySerializer extends StdSerializer<InfrastructurePlanResponseBody> {

    private static final String FIELD_ID = "id";
    private static final String FIELD_VALIDITY_STATE = "validityState";
    private static final String FIELD_EXECUTION_STATE = "executionState";
    private static final String FIELD_FAILURE_REASON = "failureReason";
    private static final String FIELD_EXECUTION_REQUEST_JSON = "executionRequestJson";
    private static final String FIELD_EXECUTED_AT = "executedAt";
    private static final String FIELD_PERIOD = "period";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_COLLECTION_START_TIME = "collectionStartTime";
    private static final String FIELD_AVERAGE_TRANSFER_TIME_MINUTES = "averageTransferTimeMinutes";
    private static final String FIELD_GREEDY_WEIGHTS = "greedyWeights";
    private static final String FIELD_DISTANCE_WEIGHT = "distanceWeight";
    private static final String FIELD_FILL_WEIGHT = "fillWeight";
    private static final String FIELD_METRICS = "metrics";
    private static final String FIELD_TOTAL_COLLECTED_KILOGRAMS = "totalCollectedKilograms";
    private static final String FIELD_TOTAL_COLLECTED_LITERS = "totalCollectedLiters";
    private static final String FIELD_TOTAL_DISTANCE_METERS = "totalDistanceMeters";
    private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
    private static final String FIELD_ESTIMATED_TOTAL_COST = "estimatedTotalCost";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_MAX_BUDGET = "maxBudget";
    private static final String FIELD_SERVICE_POLICIES = "servicePolicies";
    private static final String FIELD_MAX_SERVICE_DISTANCE_METERS = "maxServiceDistanceMeters";
    private static final String FIELD_MAX_SERVICE_TIME_MINUTES = "maxServiceTimeMinutes";
    private static final String FIELD_MAX_INFRASTRUCTURE_COUNT = "maxInfrastructureCount";
    private static final String FIELD_MAX_EMISSIONS = "maxEmissions";
    private static final String FIELD_FACILITIES = "facilities";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_FACILITY_TYPE = "facilityType";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_LATITUDE = "latitude";
    private static final String FIELD_LONGITUDE = "longitude";
    private static final String FIELD_POSTAL_ADDRESS = "postalAddress";
    private static final String FIELD_CAPACITIES = "capacities";
    private static final String FIELD_STORAGE_CAPACITY_KG = "storageCapacityKg";
    private static final String FIELD_PROCESSING_CAPACITY_KG_PER_DAY = "processingCapacityKgPerDay";
    private static final String FIELD_ASSIGNED_CONTAINERS = "assignedContainers";
    private static final String FIELD_DAILY_PLANS = "dailyPlans";
    private static final String FIELD_CONTAINER_STATE_MONITORING = "containerStateMonitoring";
    private static final String FIELD_CONTAINER_ID = "containerId";
    private static final String FIELD_CONTAINER_NAME = "containerName";
    private static final String FIELD_PLAN_DAY = "planDay";
    private static final String FIELD_DAILY_FILLING_LITERS = "dailyFillingLiters";
    private static final String FIELD_DAILY_FILLING_LITERS_BEFORE_COLLECTION = "dailyFillingLitersBeforeCollection";
    private static final String FIELD_CONTAINER_CAPACITY_LITERS = "containerCapacityLiters";
    private static final String FIELD_DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public InfrastructurePlanResponseBodySerializer() {
        super(InfrastructurePlanResponseBody.class);
    }

    /**
     * Serializes an InfrastructurePlanResponseBody object into JSON format
     * Extracts primitives from domain value objects during serialization
     *
     * @param value    The InfrastructurePlanResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(InfrastructurePlanResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(FIELD_ID, value.id.toString());
        if (value.validityState != null) {
            gen.writeStringField(FIELD_VALIDITY_STATE, value.validityState.name());
        }
        if (value.executionState != null) {
            gen.writeStringField(FIELD_EXECUTION_STATE, value.executionState.name());
        }
        if (value.failureReason != null) {
            gen.writeStringField(FIELD_FAILURE_REASON, value.failureReason.getValue());
        }
        if (value.executionRequestJson != null) {
            gen.writeStringField(FIELD_EXECUTION_REQUEST_JSON, value.executionRequestJson.getJson());
        }
        if (value.executedAt != null) {
            gen.writeStringField(FIELD_EXECUTED_AT, value.executedAt.getTimestamp());
        }
        if (value.period != null) {
            gen.writeNumberField(FIELD_PERIOD, Integer.parseInt(value.period.getValue()));
        }
        if (value.numberOfDays != null) {
            gen.writeNumberField(FIELD_NUMBER_OF_DAYS, value.numberOfDays.getValue());
        }
        if (value.collectionStartTime != null) {
            gen.writeStringField(FIELD_COLLECTION_START_TIME, value.collectionStartTime.getFormatted());
        }
        if (value.averageTransferTimeMinutes != null) {
            gen.writeNumberField(FIELD_AVERAGE_TRANSFER_TIME_MINUTES, value.averageTransferTimeMinutes.getValue());
        }
        if (value.greedyWeights != null) {
            gen.writeObjectFieldStart(FIELD_GREEDY_WEIGHTS);
            gen.writeNumberField(FIELD_DISTANCE_WEIGHT, value.greedyWeights.getDistanceWeight());
            gen.writeNumberField(FIELD_FILL_WEIGHT, value.greedyWeights.getFillWeight());
            gen.writeEndObject();
        }
        gen.writeObjectFieldStart(FIELD_METRICS);
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_KILOGRAMS, value.totalCollectedKilograms.getValue());
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_LITERS, value.totalCollectedLiters.getValue());
        gen.writeNumberField(FIELD_TOTAL_DISTANCE_METERS, value.totalDistanceMeters.getValue());
        if (value.averagePickupTimeMinutes != null) {
            gen.writeNumberField(FIELD_AVERAGE_PICKUP_TIME_MINUTES, value.averagePickupTimeMinutes.getValue());
        }
        if (value.estimatedTotalCost != null) {
            gen.writeObjectFieldStart(FIELD_ESTIMATED_TOTAL_COST);
            gen.writeNumberField(FIELD_AMOUNT, value.estimatedTotalCost.getAmount());
            if (value.estimatedTotalCost.getCurrency().isPresent()) {
                gen.writeStringField(FIELD_CURRENCY, value.estimatedTotalCost.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        if (value.maxBudget != null) {
            gen.writeObjectFieldStart(FIELD_MAX_BUDGET);
            gen.writeNumberField(FIELD_AMOUNT, value.maxBudget.getAmount());
            if (value.maxBudget.getCurrency().isPresent()) {
                gen.writeStringField(FIELD_CURRENCY, value.maxBudget.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();
        if (value.servicePolicies != null) {
            gen.writeObjectFieldStart(FIELD_SERVICE_POLICIES);
            if (value.servicePolicies.getMaxServiceDistance().isPresent()) {
                gen.writeNumberField(FIELD_MAX_SERVICE_DISTANCE_METERS, value.servicePolicies.getMaxServiceDistance().get());
            }
            if (value.servicePolicies.getMaxServiceTime().isPresent()) {
                gen.writeNumberField(FIELD_MAX_SERVICE_TIME_MINUTES, value.servicePolicies.getMaxServiceTime().get());
            }
            if (value.servicePolicies.getMaxInfrastructureCount().isPresent()) {
                gen.writeNumberField(FIELD_MAX_INFRASTRUCTURE_COUNT, value.servicePolicies.getMaxInfrastructureCount().get());
            }
            if (value.servicePolicies.getMaxEmissions().isPresent()) {
                gen.writeNumberField(FIELD_MAX_EMISSIONS, value.servicePolicies.getMaxEmissions().get());
            }
            gen.writeEndObject();
        }
        gen.writeArrayFieldStart(FIELD_FACILITIES);
        if (value.selectedFacilities != null) {
            for (var facility : value.selectedFacilities) {
                gen.writeStartObject();
                gen.writeStringField(FIELD_ID, facility.id.toString());
                if (facility.name != null) {
                    gen.writeStringField(FIELD_NAME, facility.name.getValue());
                }
                if (facility.facilityType != null) {
                    gen.writeStringField(FIELD_FACILITY_TYPE, facility.facilityType.name());
                }
                if (facility.status != null) {
                    gen.writeStringField(FIELD_STATUS, facility.status.name());
                }
                if (facility.location != null) {
                    gen.writeObjectFieldStart(FIELD_LOCATION);
                    gen.writeNumberField(FIELD_LATITUDE, facility.location.getLatitude());
                    gen.writeNumberField(FIELD_LONGITUDE, facility.location.getLongitude());
                    if (facility.location.getPostalAddress() != null) {
                        gen.writeStringField(FIELD_POSTAL_ADDRESS, facility.location.getPostalAddress());
                    }
                    gen.writeEndObject();
                }
                gen.writeObjectFieldStart(FIELD_CAPACITIES);
                if (facility.storageCapacity != null) {
                    gen.writeNumberField(FIELD_STORAGE_CAPACITY_KG, facility.storageCapacity.getKilograms());
                }
                if (facility.processingCapacity != null) {
                    gen.writeNumberField(FIELD_PROCESSING_CAPACITY_KG_PER_DAY, facility.processingCapacity.getKilogramsPerDay());
                }
                gen.writeEndObject();
                gen.writeArrayFieldStart(FIELD_ASSIGNED_CONTAINERS);
                if (value.serviceAssignments != null) {
                    var assignment = value.serviceAssignments.stream().filter(a -> a.facilityId.equals(facility.id)).findFirst().orElse(null);
                    if (assignment != null && assignment.assignedContainers != null) {
                        for (var c : assignment.assignedContainers) {
                            gen.writeObject(c);
                        }
                    }
                }
                gen.writeEndArray();
                gen.writeArrayFieldStart(FIELD_DAILY_PLANS);
                if (value.dailyPlans != null) {
                    var plans = value.dailyPlans.stream().filter(dp -> dp.facilityId.equals(facility.id)).toList();
                    for (var dp : plans) {
                        gen.writeObject(dp);
                    }
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart(FIELD_CONTAINER_STATE_MONITORING);
        if (value.containerStateMonitoring != null) {
            for (var state : value.containerStateMonitoring) {
                gen.writeStartObject();
                if (state.id != null) {
                    gen.writeStringField(FIELD_ID, state.id.toString());
                }
                if (state.containerId != null) {
                    gen.writeStringField(FIELD_CONTAINER_ID, state.containerId.toString());
                }
                if (state.containerName != null) {
                    gen.writeStringField(FIELD_CONTAINER_NAME, state.containerName.getValue());
                }
                if (state.planDay != null) {
                    gen.writeNumberField(FIELD_PLAN_DAY, state.planDay.getDay());
                }
                if (state.dailyFillingLiters != null) {
                    gen.writeNumberField(FIELD_DAILY_FILLING_LITERS, state.dailyFillingLiters.getValue());
                }
                if (state.dailyFillingLitersBeforeCollection != null) {
                    gen.writeNumberField(
                            FIELD_DAILY_FILLING_LITERS_BEFORE_COLLECTION,
                            state.dailyFillingLitersBeforeCollection.getValue());
                }
                if (state.containerCapacityLiters != null) {
                    gen.writeNumberField(FIELD_CONTAINER_CAPACITY_LITERS, state.containerCapacityLiters.getLiters());
                }
                if (state.dailyDemandLitersPerDay != null) {
                    gen.writeNumberField(FIELD_DAILY_DEMAND_LITERS_PER_DAY, state.dailyDemandLitersPerDay.getLitersPerDay());
                }
                if (state.status != null) {
                    gen.writeStringField(FIELD_STATUS, state.status.name());
                }
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
