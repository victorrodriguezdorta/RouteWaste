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
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * ServiceAssignmentReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into ServiceAssignment entities. This converter handles the deserialization of
 * MongoDB documents including their entity references (Container, Facility) by
 * loading them from their respective repositories, and their nested value objects
 * (WasteDemand, Distance, ServiceTime, TransportationVariableCost) into a properly
 * constructed ServiceAssignment domain entity.
 */
@ReadingConverter
public class ServiceAssignmentReadingConverter implements Converter<Document, ServiceAssignment> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAssignmentReadingConverter.class);

    private MongoConfiguration mongoConfiguration;

    /**
     * Constructor with MongoConfiguration dependency.
     *
     * @param mongoConfiguration MongoDB configuration for accessing repositories
     */
    public ServiceAssignmentReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a ServiceAssignment entity.
     *
     * @param document MongoDB Document to convert
     * @return ServiceAssignment entity reconstructed from the document
     */
    @Override
    public ServiceAssignment convert(@NonNull Document document) {
        logger.info("ServiceAssignment to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        
        UUID planId = (UUID) document.get(MongoFields.INFRASTRUCTURE_PLAN_ID);
        // Create a shell InfrastructurePlan to avoid infinite recursion loading from Mongo
        InfrastructurePlan infrastructurePlan = new InfrastructurePlan(
            planId,
            new PlanningPeriod(String.valueOf(LocalDate.now().getYear())),
            null, null, null, null,
            new MaximumBudget(1.0),
            new TotalCost(0.0),
            null, null, null,
            null, null, null
        );
        
        Facility facility = this.readFacilitySnapshot(document.get("facility", Document.class));
                
        List<?> containerIdsList = document.get(MongoFields.ASSIGNED_CONTAINERS, List.class);
        List<Container> assignedContainers = new ArrayList<>();
        if (containerIdsList != null) {
            for (Object idObj : containerIdsList) {
                assignedContainers.add(this.readContainerSnapshot((Document) idObj));
            }
        }
        
        return new ServiceAssignment(
                id,
                infrastructurePlan,
                facility,
                assignedContainers);
    }

    private Facility readFacilitySnapshot(Document document) {
        if (document == null) {
            throw new IllegalStateException("Facility snapshot not found when loading ServiceAssignment");
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

    private Container readContainerSnapshot(Document document) {
        if (document == null) {
            throw new IllegalStateException("Container snapshot not found when loading ServiceAssignment");
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
