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
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * ServiceAssignmentWritingConverter
 *
 * Custom converter for writing ServiceAssignment entities to MongoDB documents.
 * This converter ensures proper serialization of complex value objects and entity references.
 */
@WritingConverter
public class ServiceAssignmentWritingConverter implements Converter<ServiceAssignment, Document> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAssignmentWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new ServiceAssignmentWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public ServiceAssignmentWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a ServiceAssignment entity to a MongoDB Document.
     *
     * @param serviceAssignment The ServiceAssignment entity to convert.
     * @return The MongoDB Document representation of the ServiceAssignment.
     */
    @Override
    public Document convert(@NonNull ServiceAssignment serviceAssignment) {
        logger.info("ServiceAssignment with id '{}' to be written", serviceAssignment.getId());
        Document document = new Document();
        document.put(MongoFields.ID, serviceAssignment.getId());
        document.put(MongoFields.INFRASTRUCTURE_PLAN_ID, serviceAssignment.getInfrastructurePlan().getId());
        document.put("infrastructurePlan", toInfrastructurePlanReferenceDocument(serviceAssignment.getInfrastructurePlan()));
        document.put("facility", toFacilityDocument(serviceAssignment.getFacility()));
        
        List<Document> containerDocuments = new ArrayList<>(serviceAssignment.getAssignedContainers().size());
        for (Container container : serviceAssignment.getAssignedContainers()) {
            containerDocuments.add(toContainerDocument(container));
        }
        document.put(MongoFields.ASSIGNED_CONTAINERS, containerDocuments);
        
        return document;
    }

    private Document toInfrastructurePlanReferenceDocument(InfrastructurePlan infrastructurePlan) {
        Document planDocument = new Document();
        planDocument.put(MongoFields.ID, infrastructurePlan.getId());
        return planDocument;
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
