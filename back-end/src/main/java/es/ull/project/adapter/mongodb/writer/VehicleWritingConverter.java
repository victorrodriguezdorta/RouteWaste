package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * VehicleWritingConverter
 *
 * Custom converter for writing Vehicle entities to MongoDB documents.
 * This converter ensures proper serialization of complex value objects.
 */
@WritingConverter
public class VehicleWritingConverter implements Converter<Vehicle, Document> {

    private static final Logger logger = LoggerFactory.getLogger(VehicleWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new VehicleWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public VehicleWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a Vehicle entity to a MongoDB Document.
     *
     * @param vehicle The Vehicle entity to convert.
     * @return The MongoDB Document representation of the Vehicle.
     */
    @Override
    public Document convert(@NonNull Vehicle vehicle) {
        logger.info("Vehicle with id '{}' to be written", vehicle.getId());
        Document document = new Document();
        document.put(MongoFields.ID, vehicle.getId());
        document.put(MongoFields.VEHICLE_TYPE, vehicle.getVehicleType().toString());
        VehicleCapacityKilograms capacityKg = vehicle.getCapacityKilograms();
        Document capacityKgDocument = new Document();
        capacityKgDocument.put(MongoFields.CAPACITY_Kilograms_VALUE, capacityKg.getKilograms());
        document.put(MongoFields.CAPACITY_Kilograms, capacityKgDocument);
        VehicleCapacityLiters capacityL = vehicle.getCapacityLiters();
        Document capacityLDocument = new Document();
        capacityLDocument.put(MongoFields.CAPACITY_liters_VALUE, capacityL.getLiters());
        document.put(MongoFields.CAPACITY_liters, capacityLDocument);
        Document costDocument = new Document();
        costDocument.put(MongoFields.COST_PER_KILOMETER_AMOUNT,
            vehicle.getCostPerKilometer().getAmount());
        vehicle.getCostPerKilometer().getCurrency().ifPresent(currency ->
            costDocument.put(MongoFields.COST_PER_KILOMETER_CURRENCY,
                currency.getCode())
        );
        document.put(MongoFields.COST_PER_KILOMETER, costDocument);
        return document;
    }
}
