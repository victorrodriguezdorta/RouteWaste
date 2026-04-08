package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * FacilityReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into Facility entities. This converter handles the deserialization of
 * MongoDB documents including their nested structures (Location, Capacity,
 * OpeningFixedCost, WasteDemand) into a properly constructed Facility domain entity.
 */
@ReadingConverter
public class FacilityReadingConverter implements Converter<Document, Facility> {

    private static final Logger logger = LoggerFactory.getLogger(FacilityReadingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new FacilityReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public FacilityReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a Facility entity.
     *
     * @param document MongoDB Document to convert
     * @return Facility entity reconstructed from the document
     */
    @Override
    public Facility convert(Document document) {
        logger.info("Facility to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        FacilityType facilityType = FacilityType.fromString(document.getString(MongoFields.FACILITY_TYPE));
        Document locationDocument = (Document) document.get(MongoFields.LOCATION);
        double latitude = locationDocument.getDouble(MongoFields.LATITUDE);
        double longitude = locationDocument.getDouble(MongoFields.LONGITUDE);
        String postalAddress = locationDocument.getString(MongoFields.POSTAL_ADDRESS);
        String gisReference = locationDocument.getString(MongoFields.GIS_REFERENCE);
        Location location = new Location(latitude, longitude, postalAddress, gisReference);
        Document capacityDocument = (Document) document.get(MongoFields.CAPACITY);
        double capacityValue = capacityDocument.getDouble(MongoFields.CAPACITY_VALUE);
        String capacityQuantityUnitValue = capacityDocument.getString(MongoFields.CAPACITY_QUANTITY_UNIT);
        QuantityUnit capacityQuantityUnit = new QuantityUnit(capacityQuantityUnitValue);
        String capacityTimeUnitString = capacityDocument.getString(MongoFields.CAPACITY_TIME_UNIT);
        TimeUnit capacityTimeUnit = TimeUnit.fromString(capacityTimeUnitString);
        Capacity capacity = new Capacity(capacityValue, capacityQuantityUnit, capacityTimeUnit);
        Document openingFixedCostDocument = (Document) document.get(MongoFields.OPENING_FIXED_COST);
        double openingFixedCostAmount = openingFixedCostDocument.getDouble(MongoFields.OPENING_FIXED_COST_AMOUNT);
        OpeningFixedCost openingFixedCost;
        if (openingFixedCostDocument.containsKey(MongoFields.OPENING_FIXED_COST_CURRENCY)) {
            String currencyCode = openingFixedCostDocument.getString(MongoFields.OPENING_FIXED_COST_CURRENCY);
            Currency currency = new Currency(currencyCode);
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount, currency);
        } else {
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount);
        }
        FacilityStatus status = FacilityStatus.fromString(document.getString(MongoFields.STATUS));
        Document wasteDemandDocument = (Document) document.get(MongoFields.ASSIGNED_WASTE_DEMAND);
        double wasteDemandValue = wasteDemandDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
        String wasteDemandQuantityUnitValue = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_QUANTITY_UNIT);
        QuantityUnit wasteDemandQuantityUnit = new QuantityUnit(wasteDemandQuantityUnitValue);
        String wasteDemandTimeUnitString = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_TIME_UNIT);
        TimeUnit wasteDemandTimeUnit = TimeUnit.fromString(wasteDemandTimeUnitString);
        WasteDemand assignedWasteDemand = new WasteDemand(wasteDemandValue, wasteDemandQuantityUnit, wasteDemandTimeUnit);
        Facility facility = new Facility(
                id,
                facilityType,
                location,
                capacity,
                openingFixedCost,
                status,
                assignedWasteDemand);
        return facility;
    }
}
