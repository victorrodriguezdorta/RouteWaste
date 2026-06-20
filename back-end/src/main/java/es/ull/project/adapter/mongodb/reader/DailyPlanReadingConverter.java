package es.ull.project.adapter.mongodb.reader;

/**
 * DailyPlanReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into DailyPlan entities, including their nested facility, vehicle,
 * stop, and container snapshots.
 */
import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.InfrastructurePlanReferences;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class DailyPlanReadingConverter implements Converter<Document, DailyPlan> {

    private static final Logger logger = LoggerFactory.getLogger(DailyPlanReadingConverter.class);
    private static final String CONTAINER_FIELD = "containerId";
    private static final String TYPE_FIELD = "type";
    private static final String ERR_FACILITY_SNAPSHOT = "Facility with id '%s' not found when loading DailyPlan";
    private static final String ERR_VEHICLE_SNAPSHOT = "Vehicle with id '%s' not found when loading DailyPlan";
    private static final String ERR_CONTAINER_SNAPSHOT = "Container with id '%s' not found when loading DailyPlan";
    private static final String ERR_CONTAINER_ID_REQUIRED = "Container id is required for container stops";

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new DailyPlanReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public DailyPlanReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a DailyPlan entity.
     *
     * @param document MongoDB Document to convert
     * @return DailyPlan entity reconstructed from the document
     */
    @Override
    public DailyPlan convert(@NonNull Document document) {
        logger.info("DailyPlan to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        UUID planId = (UUID) document.get(MongoFields.INFRASTRUCTURE_PLAN_ID);
        InfrastructurePlan infrastructurePlan = InfrastructurePlanReferences.forIdReferenceOnly(planId);
        UUID facilityId = (UUID) document.get(MongoFields.FACILITY_ID);
        Facility facility = mongoConfiguration.facilityRepository().findById(facilityId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_FACILITY_SNAPSHOT, facilityId)));
        LocalDate serviceDate = LocalDate.parse(document.getString(MongoFields.SERVICE_DATE));
        PlanDay planDay = document.containsKey(MongoFields.PLAN_DAY) && document.getInteger(MongoFields.PLAN_DAY) != null ? new PlanDay(document.getInteger(MongoFields.PLAN_DAY)) : null;
        UUID vehicleId = (UUID) document.get(MongoFields.VEHICLE);
        Vehicle vehicle = mongoConfiguration.vehicleRepository().findById(vehicleId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_VEHICLE_SNAPSHOT, vehicleId)));
        CollectedWeightKilograms totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(document.getDouble(MongoFields.TOTAL_COLLECTED_KILOGRAMS));
        CollectedVolumeLiters totalCollectedLiters = CollectedVolumeLiters.fromLiters(document.getDouble(MongoFields.TOTAL_COLLECTED_LITERS));
        Distance totalDistanceMeters = Distance.fromMeters(document.getDouble(MongoFields.TOTAL_DISTANCE_METERS));
        List<?> stopDocuments = document.get(MongoFields.STOPS, List.class);
        List<Stop> stops = new ArrayList<>();
        if (stopDocuments != null) {
            for (Object stopDocumentObject : stopDocuments) {
                stops.add(readStopSnapshot((Document) stopDocumentObject));
            }
        }
        return new DailyPlan(
                id,
                infrastructurePlan,
                facility,
                serviceDate,
                planDay,
                vehicle,
                totalCollectedKilograms,
                totalCollectedLiters,
                totalDistanceMeters,
                stops);
    }

    /**
     * Reads a Stop snapshot from a nested MongoDB document.
     *
     * @param document the nested Document containing stop data
     * @return the reconstructed Stop entity
     */
    private Stop readStopSnapshot(Document document) {
        int sequence = document.getInteger(MongoFields.SEQUENCE);
        StopType stopType = document.containsKey(TYPE_FIELD) && document.get(TYPE_FIELD) != null
            ? StopType.fromString(document.getString(TYPE_FIELD))
            : StopType.CONTAINER;
        double collectedKilograms = document.getDouble(MongoFields.COLLECTED_KILOGRAMS);
        double collectedLiters = document.getDouble(MongoFields.COLLECTED_LITERS);
        double distanceFromPreviousMeters = document.getDouble(MongoFields.DISTANCE_FROM_PREVIOUS_METERS);
        double cumulativeDistanceMeters = document.getDouble(MongoFields.CUMULATIVE_DISTANCE_METERS);
        Container container = null;
        UUID containerId = (UUID) document.get(CONTAINER_FIELD);
        if (stopType == StopType.CONTAINER) {
            if (containerId == null) {
                throw new IllegalStateException(ERR_CONTAINER_ID_REQUIRED);
            }
            container = mongoConfiguration.containerRepository().findById(containerId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_CONTAINER_SNAPSHOT, containerId)));
        }
        LocalTime collectedAt = readLocalTime(document.getString(MongoFields.COLLECTED_AT));
        return new Stop(
                RouteSequence.of(sequence),
            stopType,
                container,
                CollectedWeightKilograms.fromKilograms(collectedKilograms),
                CollectedVolumeLiters.fromLiters(collectedLiters),
                Distance.fromMeters(distanceFromPreviousMeters),
                Distance.fromMeters(cumulativeDistanceMeters),
                null,
                null,
                collectedAt);
    }

    /**
     * Parses a persisted time-of-day value, tolerating missing or malformed data.
     *
     * @param value the raw time value
     * @return the parsed time, or {@code null} when unavailable
     */
    private LocalTime readLocalTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            logger.debug("Skipping invalid stop time value: {}", value);
            return null;
        }
    }
}
