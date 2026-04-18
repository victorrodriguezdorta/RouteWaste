package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Facility;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * FacilityWritingConverter
 *
 * Custom converter for writing Facility entities to MongoDB documents.
 * This converter ensures proper serialization of complex value objects.
 */
@WritingConverter
public class FacilityWritingConverter implements Converter<Facility, Document> {

    private static final Logger logger = LoggerFactory.getLogger(FacilityWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new FacilityWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public FacilityWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a Facility entity to a MongoDB Document.
     *
     * @param facility The Facility entity to convert.
     * @return The MongoDB Document representation of the Facility.
     */
    @Override
    public Document convert(@NonNull Facility facility) {
        logger.info("Facility with id '{}' to be written", facility.getId());
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
        openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_AMOUNT, 
            facility.getOpeningFixedCost().getAmount());
        facility.getOpeningFixedCost().getCurrency().ifPresent(currency ->
            openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_CURRENCY, 
                currency.getCode())
        );
        document.put(MongoFields.OPENING_FIXED_COST, openingFixedCostDocument);
        document.put(MongoFields.STATUS, facility.getStatus().toString());
        Document currentFillingLevelDocument = new Document();
        currentFillingLevelDocument.put(MongoFields.WASTE_DEMAND_VALUE, 
            facility.getCurrentFillingLevel().getLitersPerDay());
        document.put(MongoFields.CURRENT_FILLING_LEVEL, currentFillingLevelDocument);
        return document;
    }
}
