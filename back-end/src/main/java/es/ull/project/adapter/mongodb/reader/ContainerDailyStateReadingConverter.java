package es.ull.project.adapter.mongodb.reader;

/**
 * Converts MongoDB documents into ContainerDailyState entities.
 */
import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.InfrastructurePlanReferences;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class ContainerDailyStateReadingConverter implements Converter<Document, ContainerDailyState> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerDailyStateReadingConverter.class);
    private static final String ERR_CONTAINER_SNAPSHOT = "Container with id '%s' not found when loading ContainerDailyState";

    private final MongoConfiguration mongoConfiguration;

    /**
     * Creates the converter.
     *
     * @param mongoConfiguration Mongo configuration providing repositories
     */
    public ContainerDailyStateReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB document into a ContainerDailyState domain entity.
     *
     * @param document MongoDB document to convert
     * @return converted container daily state
     */
    @Override
    public ContainerDailyState convert(@NonNull Document document) {
        logger.info("Reading ContainerDailyState from document");
        UUID id = (UUID) document.get(MongoFields.ID);
        UUID infrastructurePlanId = document.get(MongoFields.INFRASTRUCTURE_PLAN_ID, UUID.class);
        InfrastructurePlan infrastructurePlan = infrastructurePlanId != null
                ? InfrastructurePlanReferences.forIdReferenceOnly(infrastructurePlanId)
                : null;
        UUID containerId = document.get(MongoFields.CONTAINER_ID, UUID.class);
        Container container = this.mongoConfiguration.containerRepository().findById(containerId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_CONTAINER_SNAPSHOT, containerId)));
        Integer planDay = document.getInteger(MongoFields.PLAN_DAY);
        Double dailyFilling = document.getDouble(MongoFields.DAILY_FILLING_LITERS);
        Double dailyFillingBeforeCollection = document.getDouble(MongoFields.DAILY_FILLING_LITERS_BEFORE_COLLECTION);
        Double capacity = document.getDouble(MongoFields.CAPACITY_LITERS);
        Double dailyDemand = document.getDouble(MongoFields.DAILY_DEMAND_LITERS_PER_DAY);
        String statusRaw = document.getString(MongoFields.STATUS);
        ContainerStatus status = ContainerStatus.fromString(statusRaw);
        LocalTime time = readLocalTime(document.getString(MongoFields.TIME));
        return new ContainerDailyState(
                id,
                infrastructurePlan,
                container,
                new PlanDay(planDay != null ? planDay : 1),
                CollectedVolumeLiters.fromLiters(dailyFilling != null ? dailyFilling : 0.0),
                dailyFillingBeforeCollection != null
                        ? CollectedVolumeLiters.fromLiters(dailyFillingBeforeCollection)
                        : null,
                new ContainerCapacityLiters(capacity != null ? capacity : 0.0),
                new DailyWasteDemandLitersPerDay(dailyDemand != null ? dailyDemand : 0.0),
                status,
                time);
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
            logger.debug("Skipping invalid container state time value: {}", value);
            return null;
        }
    }
}
