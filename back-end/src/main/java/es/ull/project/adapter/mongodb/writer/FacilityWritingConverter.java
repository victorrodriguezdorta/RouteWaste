package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Facility;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

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
    public Document convert(Facility facility) {
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
        Document capacityDocument = new Document();
        capacityDocument.put(MongoFields.CAPACITY_VALUE, facility.getCapacity().getValue());
        capacityDocument.put(MongoFields.CAPACITY_QUANTITY_UNIT, 
            facility.getCapacity().getQuantityUnit().getValue());
        capacityDocument.put(MongoFields.CAPACITY_TIME_UNIT, 
            facility.getCapacity().getTimeUnit().toString());
        document.put(MongoFields.CAPACITY, capacityDocument);
        Document openingFixedCostDocument = new Document();
        openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_AMOUNT, 
            facility.getOpeningFixedCost().getAmount());
        facility.getOpeningFixedCost().getCurrency().ifPresent(currency ->
            openingFixedCostDocument.put(MongoFields.OPENING_FIXED_COST_CURRENCY, 
                currency.getCode())
        );
        document.put(MongoFields.OPENING_FIXED_COST, openingFixedCostDocument);
        document.put(MongoFields.STATUS, facility.getStatus().toString());
        Document wasteDemandDocument = new Document();
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_VALUE, 
            facility.getAssignedWasteDemand().getValue());
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_QUANTITY_UNIT, 
            facility.getAssignedWasteDemand().getQuantityUnit().getValue());
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_TIME_UNIT, 
            facility.getAssignedWasteDemand().getTimeUnit().toString());
        document.put(MongoFields.ASSIGNED_WASTE_DEMAND, wasteDemandDocument);
        return document;
    }
}
