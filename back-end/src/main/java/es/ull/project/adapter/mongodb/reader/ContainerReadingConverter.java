package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;
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
 * MongoDB documents including their nested structures (Location, WasteDemand)
 * into a properly constructed Container domain entity.
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
        Document wasteDemandDocument = (Document) document.get(MongoFields.WASTE_DEMAND);
        double wasteDemandValue = wasteDemandDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
        String quantityUnitValue = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_QUANTITY_UNIT);
        QuantityUnit quantityUnit = new QuantityUnit(quantityUnitValue);
        String timeUnitString = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_TIME_UNIT);
        TimeUnit timeUnit = TimeUnit.fromString(timeUnitString);
        WasteDemand wasteDemand = new WasteDemand(wasteDemandValue, quantityUnit, timeUnit);
        ServiceZone serviceZone = null;
        if (document.containsKey(MongoFields.SERVICE_ZONE)) {
            serviceZone = ServiceZone.fromString(document.getString(MongoFields.SERVICE_ZONE));
        }
        Container container = new Container(id, location, wasteType, wasteDemand, serviceZone);
        return container;
    }
}
