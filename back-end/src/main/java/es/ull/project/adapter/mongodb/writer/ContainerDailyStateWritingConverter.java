package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.domain.entity.ContainerDailyState;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * Converts ContainerDailyState entities to MongoDB documents.
 */
@WritingConverter
public class ContainerDailyStateWritingConverter implements Converter<ContainerDailyState, Document> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerDailyStateWritingConverter.class);

    /**
     * Converts a container daily state to its MongoDB document representation.
     *
     * @param state container daily state to convert
     * @return MongoDB document representation
     */
    @Override
    public Document convert(@NonNull ContainerDailyState state) {
        logger.info("Writing ContainerDailyState {} to document", state.getId());
        Document document = new Document();
        document.put(MongoFields.ID, state.getId());
        state.getInfrastructurePlanId().ifPresent(id -> document.put(MongoFields.INFRASTRUCTURE_PLAN_ID, id));
        document.put(MongoFields.CONTAINER_ID, state.getContainer().getId());
        document.put(MongoFields.PLAN_DAY, state.getPlanDay());
        document.put(MongoFields.DAILY_FILLING_LITERS, state.getDailyFillingLiters());
        state.getDailyFillingLitersBeforeCollection()
                .ifPresent(value -> document.put(MongoFields.DAILY_FILLING_LITERS_BEFORE_COLLECTION, value));
        document.put(MongoFields.CAPACITY_LITERS, state.getContainerCapacityLiters());
        document.put(MongoFields.DAILY_DEMAND_LITERS_PER_DAY, state.getDailyDemandLitersPerDay());
        document.put(MongoFields.STATUS, state.getStatus().name());
        return document;
    }
}
