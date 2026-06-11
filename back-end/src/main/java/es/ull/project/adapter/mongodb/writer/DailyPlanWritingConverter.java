package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.enumerate.StopType;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * DailyPlanWritingConverter
 *
 * Custom converter for writing DailyPlan entities to MongoDB documents.
 * Handles serialization of nested value objects and entity references.
 */
@WritingConverter
public class DailyPlanWritingConverter implements Converter<DailyPlan, Document> {

    private static final Logger logger = LoggerFactory.getLogger(DailyPlanWritingConverter.class);
    private static final String CONTAINER_FIELD = "containerId";

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new DailyPlanWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public DailyPlanWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a DailyPlan entity to a MongoDB Document.
     *
     * @param dailyPlan the DailyPlan entity to convert
     * @return the MongoDB Document representation of the DailyPlan
     */
    public Document convert(@NonNull DailyPlan dailyPlan) {
        logger.info("DailyPlan with id '{}' to be written", dailyPlan.getId());
        Document document = new Document();
        document.put(MongoFields.ID, dailyPlan.getId());
        document.put(MongoFields.INFRASTRUCTURE_PLAN_ID, dailyPlan.getInfrastructurePlan().getId());
        document.put(MongoFields.FACILITY_ID, dailyPlan.getFacility().getId());
        document.put(MongoFields.SERVICE_DATE, dailyPlan.getServiceDate().toString());
        dailyPlan.getPlanDay().ifPresent(planDay -> document.put(MongoFields.PLAN_DAY, planDay.getDay()));
        document.put(MongoFields.VEHICLE, dailyPlan.getVehicle().getId());
        document.put(MongoFields.TOTAL_COLLECTED_KILOGRAMS, dailyPlan.getTotalCollectedKilograms().getValue());
        document.put(MongoFields.TOTAL_COLLECTED_LITERS, dailyPlan.getTotalCollectedLiters().getValue());
        document.put(MongoFields.TOTAL_DISTANCE_METERS, dailyPlan.getTotalDistanceMeters().getValue());
        List<Document> stopDocuments = new ArrayList<>(dailyPlan.getStops().size());
        for (Stop stop : dailyPlan.getStops()) {
            stopDocuments.add(toStopDocument(stop));
        }
        document.put(MongoFields.STOPS, stopDocuments);
        return document;
    }

    /**
     * Converts a Stop entity to a MongoDB document with all its nested fields.
     *
     * @param stop the Stop entity to convert
     * @return a Document representing the stop
     */
    private Document toStopDocument(Stop stop) {
        Document document = new Document();
        document.put(MongoFields.SEQUENCE, stop.getSequence().getValue());
        document.put(MongoFields.STOP_TYPE, stop.getType() != null ? stop.getType().name() : StopType.CONTAINER.name());
        document.put(MongoFields.COLLECTED_KILOGRAMS, stop.getCollectedKilograms().getValue());
        document.put(MongoFields.COLLECTED_LITERS, stop.getCollectedLiters().getValue());
        document.put(MongoFields.DISTANCE_FROM_PREVIOUS_METERS, stop.getDistanceFromPreviousMeters().getValue());
        document.put(MongoFields.CUMULATIVE_DISTANCE_METERS, stop.getCumulativeDistanceMeters().getValue());
        document.put(CONTAINER_FIELD, stop.getContainer() != null ? stop.getContainer().getId() : null);
        stop.getCollectedAt().ifPresent(collectedAt -> document.put(MongoFields.COLLECTED_AT, collectedAt.toString()));
        return document;
    }
}
