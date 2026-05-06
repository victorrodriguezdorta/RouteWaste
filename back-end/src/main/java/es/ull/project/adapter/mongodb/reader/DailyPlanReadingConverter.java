package es.ull.project.adapter.mongodb.reader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

@ReadingConverter
public class DailyPlanReadingConverter implements Converter<Document, DailyPlan> {

    private static final Logger logger = LoggerFactory.getLogger(DailyPlanReadingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    public DailyPlanReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    @Override
    public DailyPlan convert(@NonNull Document document) {
        logger.info("DailyPlan to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        Document infrastructurePlanDocument = document.get("infrastructurePlan", Document.class);
        UUID planId = infrastructurePlanDocument != null ? (UUID) infrastructurePlanDocument.get(MongoFields.ID) : null;
        InfrastructurePlan infrastructurePlan = new InfrastructurePlan(
                planId,
            new PlanningPeriod(String.valueOf(LocalDate.now().getYear())),
                null,
                null,
                null,
                null,
                new MaximumBudget(1.0),
                new TotalCost(0.0),
                null,
                null,
                null,
                null,
                null,
                null);
        Facility facility = readFacilitySnapshot(document.get("facility", Document.class));
        LocalDate serviceDate = LocalDate.parse(document.getString(MongoFields.SERVICE_DATE));
        Integer planDay = document.containsKey(MongoFields.PLAN_DAY) ? document.getInteger(MongoFields.PLAN_DAY) : null;
        Vehicle vehicle = readVehicleSnapshot(document.get(MongoFields.VEHICLE, Document.class));
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

    private Facility readFacilitySnapshot(Document document) {
        if (document == null) {
            throw new IllegalStateException("Facility snapshot not found when loading DailyPlan");
        }
        UUID id = (UUID) document.get(MongoFields.ID);
        Document locationDocument = document.get(MongoFields.LOCATION, Document.class);
        Location location = new Location(
                locationDocument.getDouble(MongoFields.LATITUDE),
                locationDocument.getDouble(MongoFields.LONGITUDE),
                locationDocument.getString(MongoFields.POSTAL_ADDRESS),
                locationDocument.getString(MongoFields.GIS_REFERENCE));
        Document storageCapacityDocument = document.get(MongoFields.STORAGE_CAPACITY, Document.class);
        Document processingCapacityDocument = document.get(MongoFields.PROCESSING_CAPACITY, Document.class);
        Document unloadingTimeDocument = document.get(MongoFields.UNLOADING_TIME, Document.class);
        Document openingFixedCostDocument = document.get(MongoFields.OPENING_FIXED_COST, Document.class);
        Document currentFillingLevelDocument = document.get(MongoFields.CURRENT_FILLING_LEVEL, Document.class);
        double openingFixedCostAmount = openingFixedCostDocument.getDouble(MongoFields.OPENING_FIXED_COST_AMOUNT);
        OpeningFixedCost openingFixedCost;
        if (openingFixedCostDocument.containsKey(MongoFields.OPENING_FIXED_COST_CURRENCY)) {
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount, new Currency(openingFixedCostDocument.getString(MongoFields.OPENING_FIXED_COST_CURRENCY)));
        } else {
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount);
        }
        return new Facility(
                id,
                FacilityType.fromString(document.getString(MongoFields.FACILITY_TYPE)),
                location,
                new StorageCapacityKilograms(storageCapacityDocument.getDouble(MongoFields.CAPACITY_VALUE)),
                new ProcessingCapacityKilogramsPerDay(processingCapacityDocument.getDouble(MongoFields.CAPACITY_VALUE)),
                new UnloadingTime(unloadingTimeDocument.getInteger(MongoFields.TIME_VALUE)),
                openingFixedCost,
                FacilityStatus.fromString(document.getString(MongoFields.STATUS)),
                new DailyWasteDemandLitersPerDay(currentFillingLevelDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE)));
    }

    private Vehicle readVehicleSnapshot(Document document) {
        if (document == null) {
            throw new IllegalStateException("Vehicle snapshot not found when loading DailyPlan");
        }
        UUID id = (UUID) document.get(MongoFields.ID);
        VehicleType vehicleType = VehicleType.fromString(document.getString(MongoFields.VEHICLE_TYPE));
        Document capacityKgDocument = document.get(MongoFields.CAPACITY_Kilograms, Document.class);
        Document capacityLDocument = document.get(MongoFields.CAPACITY_liters, Document.class);
        Document costDocument = document.get(MongoFields.COST_PER_KILOMETER, Document.class);
        TransportationVariableCost costPerKilometer;
        double amount = costDocument.getDouble(MongoFields.COST_PER_KILOMETER_AMOUNT);
        if (costDocument.containsKey(MongoFields.COST_PER_KILOMETER_CURRENCY)) {
            costPerKilometer = new TransportationVariableCost(amount, new Currency(costDocument.getString(MongoFields.COST_PER_KILOMETER_CURRENCY)));
        } else {
            costPerKilometer = new TransportationVariableCost(amount);
        }
        return new Vehicle(
                id,
                vehicleType,
                new es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms(capacityKgDocument.getDouble(MongoFields.CAPACITY_Kilograms_VALUE)),
                new es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters(capacityLDocument.getDouble(MongoFields.CAPACITY_liters_VALUE)),
                costPerKilometer);
    }

    private Stop readStopSnapshot(Document document) {
        int sequence = document.getInteger(MongoFields.SEQUENCE);
        double collectedKilograms = document.getDouble(MongoFields.COLLECTED_KILOGRAMS);
        double collectedLiters = document.getDouble(MongoFields.COLLECTED_LITERS);
        double distanceFromPreviousMeters = document.getDouble(MongoFields.DISTANCE_FROM_PREVIOUS_METERS);
        double cumulativeDistanceMeters = document.getDouble(MongoFields.CUMULATIVE_DISTANCE_METERS);
        Container container = readContainerSnapshot(document.get("container", Document.class));
        return new Stop(
                RouteSequence.of(sequence),
                container,
                CollectedWeightKilograms.fromKilograms(collectedKilograms),
                CollectedVolumeLiters.fromLiters(collectedLiters),
                Distance.fromMeters(distanceFromPreviousMeters),
                Distance.fromMeters(cumulativeDistanceMeters));
    }

    private Container readContainerSnapshot(Document document) {
        if (document == null) {
            throw new IllegalStateException("Container snapshot not found when loading DailyPlan");
        }
        UUID id = (UUID) document.get(MongoFields.ID);
        Document locationDocument = document.get(MongoFields.LOCATION, Document.class);
        Location location = new Location(
                locationDocument.getDouble(MongoFields.LATITUDE),
                locationDocument.getDouble(MongoFields.LONGITUDE),
                locationDocument.getString(MongoFields.POSTAL_ADDRESS),
                locationDocument.getString(MongoFields.GIS_REFERENCE));
        ServiceZone serviceZone = null;
        if (document.containsKey(MongoFields.SERVICE_ZONE)) {
            serviceZone = ServiceZone.fromString(document.getString(MongoFields.SERVICE_ZONE));
        }
        return new Container(
                id,
                location,
                WasteType.fromString(document.getString(MongoFields.WASTE_TYPE)),
                new ContainerCapacityLiters(document.getDouble(MongoFields.CAPACITY_LITERS)),
                new DailyWasteDemandLitersPerDay(document.getDouble(MongoFields.DAILY_DEMAND_LITERS_PER_DAY)),
                serviceZone);
    }
}
