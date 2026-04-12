package es.ull.project.adapter.mongodb.reader;

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
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;

/**
 * ContainerReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into Container entities. This converter handles the deserialization of
 * MongoDB documents including their nested structures (Location, ContainerCapacityLiters,
 * DailyWasteDemandLitersPerDay) into a properly constructed Container domain entity.
 */
@ReadingConverter
public class ContainerReadingConverter implements Converter<Document, Container> {
    private static final Logger logger = LoggerFactory.getLogger(ContainerReadingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new ContainerReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public ContainerReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a Container entity.
     *
     * @param document MongoDB Document to convert
     * @return Container entity reconstructed from the document
     */
    @Override
    public Container convert(@NonNull Document document) {
        logger.info("Container to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        Document locationDocument = (Document) document.get(MongoFields.LOCATION);
        double latitude = locationDocument.getDouble(MongoFields.LATITUDE);
        double longitude = locationDocument.getDouble(MongoFields.LONGITUDE);
        String postalAddress = locationDocument.getString(MongoFields.POSTAL_ADDRESS);
        String gisReference = locationDocument.getString(MongoFields.GIS_REFERENCE);
        Location location = new Location(latitude, longitude, postalAddress, gisReference);
        WasteType wasteType = WasteType.fromString(document.getString(MongoFields.WASTE_TYPE));
        
        ContainerCapacityLiters capacityLiters;
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;
        
        // Handle both old format (with wasteDemand) and new format (with separate fields)
        if (document.containsKey(MongoFields.CAPACITY_LITERS) && document.get(MongoFields.CAPACITY_LITERS) != null) {
            // New format: flat fields
            double capacityLitersValue = document.getDouble(MongoFields.CAPACITY_LITERS);
            capacityLiters = new ContainerCapacityLiters(capacityLitersValue);
            
            double dailyDemandLitersValue = document.getDouble(MongoFields.DAILY_DEMAND_LITERS_PER_DAY);
            dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(dailyDemandLitersValue);
        } else if (document.containsKey(MongoFields.WASTE_DEMAND)) {
            // Old format: nested wasteDemand document
            Document wasteDemandDocument = (Document) document.get(MongoFields.WASTE_DEMAND);
            double wasteDemandValue = wasteDemandDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
            
            // Map old format to new: assume 1 unit of wasteDemand = ~1 liter per day as approximation
            capacityLiters = new ContainerCapacityLiters(100.0); // Default capacity for migrated records
            dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(wasteDemandValue);
        } else {
            throw new IllegalArgumentException("Container document must have either capacityLiters or wasteDemand field");
        }
        
        ServiceZone serviceZone = null;
        if (document.containsKey(MongoFields.SERVICE_ZONE)) {
            serviceZone = ServiceZone.fromString(document.getString(MongoFields.SERVICE_ZONE));
        }
        Container container = new Container(id, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
        return container;
    }
}
