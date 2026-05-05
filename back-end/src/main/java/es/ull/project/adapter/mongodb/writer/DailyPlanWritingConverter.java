package es.ull.project.adapter.mongodb.writer;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;

@WritingConverter
public class DailyPlanWritingConverter implements Converter<DailyPlan, Document> {

    private static final Logger logger = LoggerFactory.getLogger(DailyPlanWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    public DailyPlanWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    @Override
    public Document convert(@NonNull DailyPlan dailyPlan) {
        logger.info("DailyPlan with id '{}' to be written", dailyPlan.getId());
        Document document = new Document();
        document.put(MongoFields.ID, dailyPlan.getId());
        document.put("infrastructurePlan", toInfrastructurePlanReferenceDocument(dailyPlan.getInfrastructurePlan()));
        document.put("facility", toFacilityDocument(dailyPlan.getFacility()));
        document.put(MongoFields.SERVICE_DATE, dailyPlan.getServiceDate().toString());
        if (dailyPlan.getPlanDay() != null) {
            document.put(MongoFields.PLAN_DAY, dailyPlan.getPlanDay());
        }
        document.put(MongoFields.VEHICLE, toVehicleDocument(dailyPlan.getVehicle()));
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

    private Document toInfrastructurePlanReferenceDocument(InfrastructurePlan infrastructurePlan) {
        Document document = new Document();
        document.put(MongoFields.ID, infrastructurePlan.getId());
        return document;
    }

    private Document toFacilityDocument(Facility facility) {
        Document document = new Document();
        document.put(MongoFields.ID, facility.getId());
        document.put(MongoFields.FACILITY_TYPE, facility.getFacilityType().toString());
        Document locationDocument = new Document();
        locationDocument.put(MongoFields.LATITUDE, facility.getLocation().getLatitude());
        locationDocument.put(MongoFields.LONGITUDE, facility.getLocation().getLongitude());
        locationDocument.put(MongoFields.POSTAL_ADDRESS, facility.getLocation().getPostalAddress());
        locationDocument.put(MongoFields.GIS_REFERENCE, facility.getLocation().getGISReference());
        document.put(MongoFields.LOCATION, locationDocument);
        Document storageCapacityDocument = new Document();
        storageCapacityDocument.put(MongoFields.CAPACITY_VALUE, facility.getStorageCapacity().getKilograms());
        document.put(MongoFields.STORAGE_CAPACITY, storageCapacityDocument);
        Document processingCapacityDocument = new Document();
        processingCapacityDocument.put(MongoFields.CAPACITY_VALUE, facility.getProcessingCapacity().getKilogramsPerDay());
        document.put(MongoFields.PROCESSING_CAPACITY, processingCapacityDocument);
        Document unloadingTimeDocument = new Document();
        unloadingTimeDocument.put(MongoFields.TIME_VALUE, facility.getUnloadingTime().getMinutes());
        document.put(MongoFields.UNLOADING_TIME, unloadingTimeDocument);
        Document openingFixedCostDocument = new Document();
        openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_AMOUNT, facility.getOpeningFixedCost().getAmount());
        facility.getOpeningFixedCost().getCurrency().ifPresent(currency ->
            openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_CURRENCY, currency.getCode())
        );
        document.put(MongoFields.OPENING_FIXED_COST, openingFixedCostDocument);
        document.put(MongoFields.STATUS, facility.getStatus().toString());
        Document currentFillingLevelDocument = new Document();
        currentFillingLevelDocument.put(MongoFields.WASTE_DEMAND_VALUE, facility.getCurrentFillingLevel().getLitersPerDay());
        document.put(MongoFields.CURRENT_FILLING_LEVEL, currentFillingLevelDocument);
        return document;
    }

    private Document toVehicleDocument(Vehicle vehicle) {
        Document document = new Document();
        document.put(MongoFields.ID, vehicle.getId());
        document.put(MongoFields.VEHICLE_TYPE, vehicle.getVehicleType().toString());
        Document capacityKgDocument = new Document();
        capacityKgDocument.put(MongoFields.CAPACITY_Kilograms_VALUE, vehicle.getCapacityKilograms().getKilograms());
        document.put(MongoFields.CAPACITY_Kilograms, capacityKgDocument);
        Document capacityLDocument = new Document();
        capacityLDocument.put(MongoFields.CAPACITY_liters_VALUE, vehicle.getCapacityLiters().getLiters());
        document.put(MongoFields.CAPACITY_liters, capacityLDocument);
        Document costDocument = new Document();
        costDocument.put(MongoFields.COST_PER_KILOMETER_AMOUNT, vehicle.getCostPerKilometer().getAmount());
        vehicle.getCostPerKilometer().getCurrency().ifPresent(currency ->
            costDocument.put(MongoFields.COST_PER_KILOMETER_CURRENCY, currency.getCode())
        );
        document.put(MongoFields.COST_PER_KILOMETER, costDocument);
        return document;
    }

    private Document toStopDocument(Stop stop) {
        Document document = new Document();
        document.put(MongoFields.SEQUENCE, stop.getSequence().getValue());
        document.put(MongoFields.COLLECTED_KILOGRAMS, stop.getCollectedKilograms().getValue());
        document.put(MongoFields.COLLECTED_LITERS, stop.getCollectedLiters().getValue());
        document.put(MongoFields.DISTANCE_FROM_PREVIOUS_METERS, stop.getDistanceFromPreviousMeters().getValue());
        document.put(MongoFields.CUMULATIVE_DISTANCE_METERS, stop.getCumulativeDistanceMeters().getValue());
        document.put("container", toContainerDocument(stop.getContainer()));
        return document;
    }

    private Document toContainerDocument(Container container) {
        Document document = new Document();
        document.put(MongoFields.ID, container.getId());
        Document locationDocument = new Document();
        locationDocument.put(MongoFields.LATITUDE, container.getLocation().getLatitude());
        locationDocument.put(MongoFields.LONGITUDE, container.getLocation().getLongitude());
        locationDocument.put(MongoFields.POSTAL_ADDRESS, container.getLocation().getPostalAddress());
        locationDocument.put(MongoFields.GIS_REFERENCE, container.getLocation().getGISReference());
        document.put(MongoFields.LOCATION, locationDocument);
        document.put(MongoFields.WASTE_TYPE, container.getWasteType().toString());
        document.put(MongoFields.CAPACITY_LITERS, container.getCapacityLiters().getLiters());
        document.put(MongoFields.DAILY_DEMAND_LITERS_PER_DAY, container.getDailyDemandLitersPerDay().getLitersPerDay());
        container.getServiceZone().ifPresent(serviceZone -> document.put(MongoFields.SERVICE_ZONE, serviceZone.toString()));
        return document;
    }
}
