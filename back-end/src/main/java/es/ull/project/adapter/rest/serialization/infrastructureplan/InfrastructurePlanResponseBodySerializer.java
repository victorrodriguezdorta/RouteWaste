package es.ull.project.adapter.rest.serialization.infrastructureplan;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;

/**
 * Custom JSON serializer for InfrastructurePlanResponseBody
 * This serializer manually controls how InfrastructurePlan response data is converted to JSON format
 * Includes complete facility and container information in the response
 */
public class InfrastructurePlanResponseBodySerializer extends StdSerializer<InfrastructurePlanResponseBody> {



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
        gen.writeStringField("id", value.id.toString());
        if (value.executedAt != null) {
            gen.writeStringField("executedAt", value.executedAt);
        }
        if (value.period != null) {
            gen.writeNumberField("period", Integer.parseInt(value.period.getValue()));
        }
        if (value.numberOfDays != null) {
            gen.writeNumberField("numberOfDays", value.numberOfDays);
        }
        
        // Metrics
        gen.writeObjectFieldStart("metrics");
        gen.writeNumberField("totalCollectedKilograms", value.totalCollectedKilograms);
        gen.writeNumberField("totalCollectedLiters", value.totalCollectedLiters);
        gen.writeNumberField("totalDistanceMeters", value.totalDistanceMeters);
        if (value.averagePickupTimeMinutes != null) {
            gen.writeNumberField("averagePickupTimeMinutes", value.averagePickupTimeMinutes);
        }
        if (value.estimatedTotalCost != null) {
            gen.writeObjectFieldStart("estimatedTotalCost");
            gen.writeNumberField("amount", value.estimatedTotalCost.getAmount());
            if (value.estimatedTotalCost.getCurrency().isPresent()) {
                gen.writeStringField("currency", value.estimatedTotalCost.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        if (value.maxBudget != null) {
            gen.writeObjectFieldStart("maxBudget");
            gen.writeNumberField("amount", value.maxBudget.getAmount());
            if (value.maxBudget.getCurrency().isPresent()) {
                gen.writeStringField("currency", value.maxBudget.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();

        if (value.servicePolicies != null) {
            gen.writeObjectFieldStart("servicePolicies");
            if (value.servicePolicies.getMaxServiceDistance().isPresent()) {
                gen.writeNumberField("maxServiceDistanceMeters", value.servicePolicies.getMaxServiceDistance().get());
            }
            if (value.servicePolicies.getMaxServiceTime().isPresent()) {
                gen.writeNumberField("maxServiceTimeMinutes", value.servicePolicies.getMaxServiceTime().get());
            }
            if (value.servicePolicies.getMaxInfrastructureCount().isPresent()) {
                gen.writeNumberField("maxInfrastructureCount", value.servicePolicies.getMaxInfrastructureCount().get());
            }
            if (value.servicePolicies.getMaxEmissions().isPresent()) {
                gen.writeNumberField("maxEmissions", value.servicePolicies.getMaxEmissions().get());
            }
            gen.writeEndObject();
        }

        // Facilities
        gen.writeArrayFieldStart("facilities");
        if (value.selectedFacilities != null) {
            for (var facility : value.selectedFacilities) {
                gen.writeStartObject();
                gen.writeStringField("id", facility.id.toString());
                if (facility.name != null) {
                    gen.writeStringField("name", facility.name.getValue());
                }
                if (facility.facilityType != null) {
                    gen.writeStringField("facilityType", facility.facilityType.name());
                }
                if (facility.status != null) {
                    gen.writeStringField("status", facility.status.name());
                }
                if (facility.location != null) {
                    gen.writeObjectFieldStart("location");
                    gen.writeNumberField("latitude", facility.location.getLatitude());
                    gen.writeNumberField("longitude", facility.location.getLongitude());
                    if (facility.location.getPostalAddress() != null) {
                        gen.writeStringField("postalAddress", facility.location.getPostalAddress());
                    }
                    gen.writeEndObject();
                }
                gen.writeObjectFieldStart("capacities");
                if (facility.storageCapacity != null) {
                    gen.writeNumberField("storageCapacityKg", facility.storageCapacity.getKilograms());
                }
                if (facility.processingCapacity != null) {
                    gen.writeNumberField("processingCapacityKgPerDay", facility.processingCapacity.getKilogramsPerDay());
                }
                gen.writeEndObject();

                gen.writeArrayFieldStart("assignedContainers");
                if (value.serviceAssignments != null) {
                    var assignment = value.serviceAssignments.stream().filter(a -> a.facilityId.equals(facility.id)).findFirst().orElse(null);
                    if (assignment != null && assignment.assignedContainers != null) {
                        for (var c : assignment.assignedContainers) {
                            gen.writeObject(c);
                        }
                    }
                }
                gen.writeEndArray();

                gen.writeArrayFieldStart("dailyPlans");
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

        gen.writeArrayFieldStart("containerStateMonitoring");
        if (value.containerStateMonitoring != null) {
            for (var state : value.containerStateMonitoring) {
                gen.writeStartObject();
                if (state.id != null) {
                    gen.writeStringField("id", state.id.toString());
                }
                if (state.containerId != null) {
                    gen.writeStringField("containerId", state.containerId);
                }
                if (state.containerName != null) {
                    gen.writeStringField("containerName", state.containerName);
                }
                if (state.planDay != null) {
                    gen.writeNumberField("planDay", state.planDay);
                }
                if (state.dailyFillingLiters != null) {
                    gen.writeNumberField("dailyFillingLiters", state.dailyFillingLiters);
                }
                if (state.containerCapacityLiters != null) {
                    gen.writeNumberField("containerCapacityLiters", state.containerCapacityLiters);
                }
                if (state.dailyDemandLitersPerDay != null) {
                    gen.writeNumberField("dailyDemandLitersPerDay", state.dailyDemandLitersPerDay);
                }
                if (state.status != null) {
                    gen.writeStringField("status", state.status);
                }
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
