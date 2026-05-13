package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

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
    private static final String MISSING_CAPACITY_OR_DEMAND_FIELD = "Container document must have either capacityLiters or wasteDemand field";

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
        Name name = new Name(document.getString(MongoFields.NAME));
        Document locationDocument = (Document) document.get(MongoFields.LOCATION);
        double latitude = locationDocument.getDouble(MongoFields.LATITUDE);
        double longitude = locationDocument.getDouble(MongoFields.LONGITUDE);
        String postalAddress = locationDocument.getString(MongoFields.POSTAL_ADDRESS);
        String gisReference = locationDocument.getString(MongoFields.GIS_REFERENCE);
        Location location = new Location(latitude, longitude, postalAddress, gisReference);
        WasteType wasteType = WasteType.fromString(document.getString(MongoFields.WASTE_TYPE));
        ContainerCapacityLiters capacityLiters;
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;
        if (document.containsKey(MongoFields.CAPACITY_LITERS) && document.get(MongoFields.CAPACITY_LITERS) != null) {
            double capacityLitersValue = document.getDouble(MongoFields.CAPACITY_LITERS);
            capacityLiters = new ContainerCapacityLiters(capacityLitersValue);
            double dailyDemandLitersValue = document.getDouble(MongoFields.DAILY_DEMAND_LITERS_PER_DAY);
            dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(dailyDemandLitersValue);
        } else if (document.containsKey(MongoFields.WASTE_DEMAND)) {
            Document wasteDemandDocument = (Document) document.get(MongoFields.WASTE_DEMAND);
            double wasteDemandValue = wasteDemandDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
            capacityLiters = new ContainerCapacityLiters(100.0);
            dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(wasteDemandValue);
        } else {
            throw new IllegalArgumentException(MISSING_CAPACITY_OR_DEMAND_FIELD);
        }
        ServiceZone serviceZone = null;
        if (document.containsKey(MongoFields.SERVICE_ZONE)) {
            serviceZone = ServiceZone.fromString(document.getString(MongoFields.SERVICE_ZONE));
        }
        Container container = new Container(id, name, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
        return container;
    }
}
