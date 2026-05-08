package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
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
        VehicleCapacityKilograms capacityKilograms;
        Document capacityKgDocument = (Document) document.get(MongoFields.CAPACITY_Kilograms);
        if (capacityKgDocument != null && capacityKgDocument.containsKey(MongoFields.CAPACITY_Kilograms_VALUE)) {
            double capacityKgValue = capacityKgDocument.getDouble(MongoFields.CAPACITY_Kilograms_VALUE);
            capacityKilograms = new VehicleCapacityKilograms(capacityKgValue);
        } else {
            logger.warn("Missing or null capacityKilograms in vehicle document: {}", id);
            capacityKilograms = new VehicleCapacityKilograms(0.0);
        }
        VehicleCapacityLiters capacityLiters;
        Document capacityLDocument = (Document) document.get(MongoFields.CAPACITY_liters);
        if (capacityLDocument != null && capacityLDocument.containsKey(MongoFields.CAPACITY_liters_VALUE)) {
            double capacityLValue = capacityLDocument.getDouble(MongoFields.CAPACITY_liters_VALUE);
            capacityLiters = new VehicleCapacityLiters(capacityLValue);
        } else {
            logger.warn("Missing or null capacityLiters in vehicle document: {}", id);
            capacityLiters = new VehicleCapacityLiters(0.0);
        }
        TransportationVariableCost costPerKilometer;
        Document costDocument = (Document) document.get(MongoFields.COST_PER_KILOMETER);
        if (costDocument != null && costDocument.containsKey(MongoFields.COST_PER_KILOMETER_AMOUNT)) {
            double costAmount = costDocument.getDouble(MongoFields.COST_PER_KILOMETER_AMOUNT);
            if (costDocument.containsKey(MongoFields.COST_PER_KILOMETER_CURRENCY)) {
                String currencyCode = costDocument.getString(MongoFields.COST_PER_KILOMETER_CURRENCY);
                Currency currency = new Currency(currencyCode);
                costPerKilometer = new TransportationVariableCost(costAmount, currency);
            } else {
                costPerKilometer = new TransportationVariableCost(costAmount);
            }
        } else {
            logger.warn("Missing or null costPerKilometer in vehicle document: {}", id);
            costPerKilometer = new TransportationVariableCost(0.0);
        }
        Vehicle vehicle = new Vehicle(id, vehicleType, capacityKilograms, capacityLiters, costPerKilometer);
        return vehicle;
    }
}
