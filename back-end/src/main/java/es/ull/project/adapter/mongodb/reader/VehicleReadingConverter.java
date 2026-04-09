package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

/**
 * VehicleReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into Vehicle entities. This converter handles the deserialization of
 * MongoDB documents including their nested structures (Capacity, TransportationVariableCost)
 * into a properly constructed Vehicle domain entity.
 */
@ReadingConverter
public class VehicleReadingConverter implements Converter<Document, Vehicle> {
    private static final Logger logger = LoggerFactory.getLogger(VehicleReadingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new VehicleReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public VehicleReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a Vehicle entity.
     *
     * @param document MongoDB Document to convert
     * @return Vehicle entity reconstructed from the document
     */
    @Override
    public Vehicle convert(@NonNull Document document) {
        logger.info("Vehicle to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        VehicleType vehicleType = VehicleType.fromString(document.getString(MongoFields.VEHICLE_TYPE));
        Document capacityDocument = (Document) document.get(MongoFields.TRANSPORT_CAPACITY);
        double capacityValue = capacityDocument.getDouble(MongoFields.CAPACITY_VALUE);
        String capacityQuantityUnitValue = capacityDocument.getString(MongoFields.CAPACITY_QUANTITY_UNIT);
        QuantityUnit capacityQuantityUnit = new QuantityUnit(capacityQuantityUnitValue);
        String capacityTimeUnitString = capacityDocument.getString(MongoFields.CAPACITY_TIME_UNIT);
        TimeUnit capacityTimeUnit = TimeUnit.fromString(capacityTimeUnitString);
        Capacity transportCapacity = new Capacity(capacityValue, capacityQuantityUnit, capacityTimeUnit);
        Document costDocument = (Document) document.get(MongoFields.COST_PER_KILOMETER);
        double costAmount = costDocument.getDouble(MongoFields.COST_PER_KILOMETER_AMOUNT);
        TransportationVariableCost costPerKilometer;
        if (costDocument.containsKey(MongoFields.COST_PER_KILOMETER_CURRENCY)) {
            String currencyCode = costDocument.getString(MongoFields.COST_PER_KILOMETER_CURRENCY);
            Currency currency = new Currency(currencyCode);
            costPerKilometer = new TransportationVariableCost(costAmount, currency);
        } else {
            costPerKilometer = new TransportationVariableCost(costAmount);
        }
        Vehicle vehicle = new Vehicle(id, vehicleType, transportCapacity, costPerKilometer);
        return vehicle;
    }
}
