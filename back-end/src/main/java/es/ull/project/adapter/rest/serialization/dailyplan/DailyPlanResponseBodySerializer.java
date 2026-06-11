package es.ull.project.adapter.rest.serialization.dailyplan;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.dailyplan.StopResponseBody;
import es.ull.project.domain.entity.StopAlert;
import java.io.IOException;
import java.util.List;

/**
 * Custom JSON serializer for DailyPlanResponseBody.
 */
public class DailyPlanResponseBodySerializer extends StdSerializer<DailyPlanResponseBody> {

    private static final String FIELD_ID = "id";
    private static final String FIELD_INFRASTRUCTURE_PLAN_ID = "infrastructurePlanId";
    private static final String FIELD_FACILITY_ID = "facilityId";
    private static final String FIELD_FACILITY_NAME = "facilityName";
    private static final String FIELD_SERVICE_DATE = "serviceDate";
    private static final String FIELD_PLAN_DAY = "planDay";
    private static final String FIELD_VEHICLE = "vehicle";
    private static final String FIELD_TOTAL_COLLECTED_KILOGRAMS = "totalCollectedKilograms";
    private static final String FIELD_TOTAL_COLLECTED_LITERS = "totalCollectedLiters";
    private static final String FIELD_TOTAL_DISTANCE_METERS = "totalDistanceMeters";
    private static final String FIELD_STOPS = "stops";
    private static final String FIELD_SEQUENCE = "sequence";
    private static final String FIELD_CONTAINER_ID = "containerId";
    private static final String FIELD_CONTAINER_NAME = "containerName";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_COLLECTED_KILOGRAMS = "collectedKilograms";
    private static final String FIELD_COLLECTED_LITERS = "collectedLiters";
    private static final String FIELD_DISTANCE_FROM_PREVIOUS_METERS = "distanceFromPreviousMeters";
    private static final String FIELD_CUMULATIVE_DISTANCE_METERS = "cumulativeDistanceMeters";
    private static final String FIELD_CONTAINER_ACTUAL_LITERS = "containerActualLiters";
    private static final String FIELD_COLLECTED_AT = "collectedAt";
    private static final String FIELD_ALERTS = "alerts";
    private static final String FIELD_ALERT_TYPE = "type";
    private static final String FIELD_ALERT_MESSAGE = "message";
    private static final String FIELD_ALERT_VALUE = "value";

    /**
     * Creates the serializer for DailyPlanResponseBody.
     */
    public DailyPlanResponseBodySerializer() {
        super(DailyPlanResponseBody.class);
    }

    /**
     * Serializes a daily plan response body.
     *
     * @param value response body to serialize
     * @param gen JSON generator
     * @param provider serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(DailyPlanResponseBody value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField(FIELD_ID, value.id.toString());
        gen.writeStringField(FIELD_INFRASTRUCTURE_PLAN_ID, value.infrastructurePlanId.toString());
        gen.writeStringField(FIELD_FACILITY_ID, value.facilityId.toString());
        if (value.facilityName != null) {
            gen.writeStringField(FIELD_FACILITY_NAME, value.facilityName.getValue());
        }
        if (value.serviceDate != null) {
            gen.writeStringField(FIELD_SERVICE_DATE, value.serviceDate.getDate().toString());
        }
        if (value.planDay != null) {
            gen.writeNumberField(FIELD_PLAN_DAY, value.planDay.getDay());
        }
        gen.writeObjectField(FIELD_VEHICLE, value.vehicle);
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_KILOGRAMS, value.totalCollectedKilograms.getValue());
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_LITERS, value.totalCollectedLiters.getValue());
        gen.writeNumberField(FIELD_TOTAL_DISTANCE_METERS, value.totalDistanceMeters.getValue());
        writeStops(gen, value);
        gen.writeEndObject();
    }

    /**
     * Serializes the collection of stop response bodies.
     *
     * @param gen JSON generator
     * @param value daily plan response containing the stops
     * @throws IOException if an I/O error occurs during serialization
     */
    private void writeStops(JsonGenerator gen, DailyPlanResponseBody value) throws IOException {
        gen.writeArrayFieldStart(FIELD_STOPS);
        if (value.stops != null) {
            for (StopResponseBody stop : value.stops) {
                writeStop(gen, stop);
            }
        }
        gen.writeEndArray();
    }

    /**
     * Serializes a single stop response body using scalar JSON values.
     *
     * @param gen JSON generator
     * @param stop stop response to serialize
     * @throws IOException if an I/O error occurs during serialization
     */
    private void writeStop(JsonGenerator gen, StopResponseBody stop) throws IOException {
        if (stop == null) {
            gen.writeNull();
            return;
        }
        gen.writeStartObject();
        if (stop.sequence != null) {
            gen.writeNumberField(FIELD_SEQUENCE, stop.sequence.getValue());
        }
        if (stop.containerId != null) {
            gen.writeStringField(FIELD_CONTAINER_ID, stop.containerId.toString());
        }
        if (stop.containerName != null) {
            gen.writeStringField(FIELD_CONTAINER_NAME, stop.containerName.getValue());
        }
        if (stop.type != null) {
            gen.writeStringField(FIELD_TYPE, stop.type.name());
        }
        if (stop.collectedKilograms != null) {
            gen.writeNumberField(FIELD_COLLECTED_KILOGRAMS, stop.collectedKilograms.getValue());
        }
        if (stop.collectedLiters != null) {
            gen.writeNumberField(FIELD_COLLECTED_LITERS, stop.collectedLiters.getValue());
        }
        if (stop.distanceFromPreviousMeters != null) {
            gen.writeNumberField(FIELD_DISTANCE_FROM_PREVIOUS_METERS, stop.distanceFromPreviousMeters.getValue());
        }
        if (stop.cumulativeDistanceMeters != null) {
            gen.writeNumberField(FIELD_CUMULATIVE_DISTANCE_METERS, stop.cumulativeDistanceMeters.getValue());
        }
        if (stop.containerActualLiters != null) {
            gen.writeNumberField(FIELD_CONTAINER_ACTUAL_LITERS, stop.containerActualLiters.getValue());
        }
        if (stop.collectedAt != null) {
            gen.writeStringField(FIELD_COLLECTED_AT, stop.collectedAt.toString());
        }
        writeAlerts(gen, stop.alerts);
        gen.writeEndObject();
    }

    /**
     * Serializes stop alerts using scalar JSON values.
     *
     * @param gen JSON generator
     * @param alerts alerts to serialize
     * @throws IOException if an I/O error occurs during serialization
     */
    private void writeAlerts(JsonGenerator gen, List<StopAlert> alerts) throws IOException {
        gen.writeArrayFieldStart(FIELD_ALERTS);
        if (alerts != null) {
            for (StopAlert alert : alerts) {
                writeAlert(gen, alert);
            }
        }
        gen.writeEndArray();
    }

    /**
     * Serializes a single stop alert.
     *
     * @param gen JSON generator
     * @param alert alert to serialize
     * @throws IOException if an I/O error occurs during serialization
     */
    private void writeAlert(JsonGenerator gen, StopAlert alert) throws IOException {
        if (alert == null) {
            gen.writeNull();
            return;
        }
        gen.writeStartObject();
        if (alert.getType() != null) {
            gen.writeStringField(FIELD_ALERT_TYPE, alert.getType().getValue());
        }
        if (alert.getMessage() != null) {
            gen.writeStringField(FIELD_ALERT_MESSAGE, alert.getMessage().getValue());
        }
        if (alert.getValue().isPresent()) {
            gen.writeNumberField(FIELD_ALERT_VALUE, alert.getValue().get().getValue());
        }
        gen.writeEndObject();
    }
}
