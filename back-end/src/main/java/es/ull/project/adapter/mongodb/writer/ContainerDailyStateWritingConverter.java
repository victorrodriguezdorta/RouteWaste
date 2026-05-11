package es.ull.project.adapter.mongodb.writer;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.domain.entity.ContainerDailyState;

@WritingConverter
public class ContainerDailyStateWritingConverter implements Converter<ContainerDailyState, Document> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerDailyStateWritingConverter.class);

    @Override
    public Document convert(@NonNull ContainerDailyState state) {
        logger.info("Writing ContainerDailyState {} to document", state.getId());
        Document document = new Document();
        document.put(MongoFields.ID, state.getId());
        document.put(MongoFields.CONTAINER_ID, state.getContainerId());
        document.put(MongoFields.PLAN_DAY, state.getPlanDay());
        document.put(MongoFields.DAILY_FILLING_LITERS, state.getDailyFillingLiters());
        document.put(MongoFields.CAPACITY_LITERS, state.getContainerCapacityLiters());
        document.put(MongoFields.DAILY_DEMAND_LITERS_PER_DAY, state.getDailyDemandLitersPerDay());
        document.put(MongoFields.STATUS, state.getStatus().name());
        return document;
    }
}
