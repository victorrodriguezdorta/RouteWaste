package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.identifier.ContainerId;
import es.ull.project.domain.valueobject.identifier.InfrastructurePlanId;
import es.ull.project.domain.valueobject.time.PlanDay;
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
        UUID containerId = UUID.fromString(document.getString(MongoFields.CONTAINER_ID));
        Integer planDay = document.getInteger(MongoFields.PLAN_DAY);
        Double dailyFilling = document.getDouble(MongoFields.DAILY_FILLING_LITERS);
        Double capacity = document.getDouble(MongoFields.CAPACITY_LITERS);
        Double dailyDemand = document.getDouble(MongoFields.DAILY_DEMAND_LITERS_PER_DAY);
        String statusRaw = document.getString(MongoFields.STATUS);
        ContainerStatus status = ContainerStatus.fromString(statusRaw);
        return new ContainerDailyState(
            id,
            infrastructurePlanId != null ? new InfrastructurePlanId(infrastructurePlanId) : null,
            new ContainerId(containerId),
            new PlanDay(planDay != null ? planDay : 1),
            CollectedVolumeLiters.fromLiters(dailyFilling != null ? dailyFilling : 0.0),
            new ContainerCapacityLiters(capacity != null ? capacity : 0.0),
            new DailyWasteDemandLitersPerDay(dailyDemand != null ? dailyDemand : 0.0),
            status
        );
    }
}
