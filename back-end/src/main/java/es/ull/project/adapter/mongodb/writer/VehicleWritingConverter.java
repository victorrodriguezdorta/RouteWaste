package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Vehicle;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

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
    public Document convert(Vehicle vehicle) {
        logger.info("Vehicle with id '{}' to be written", vehicle.getId());
        Document document = new Document();
        document.put(MongoFields.ID, vehicle.getId());
        document.put(MongoFields.VEHICLE_TYPE, vehicle.getVehicleType().toString());
        Document capacityDocument = new Document();
        capacityDocument.put(MongoFields.CAPACITY_VALUE, vehicle.getTransportCapacity().getValue());
        capacityDocument.put(MongoFields.CAPACITY_QUANTITY_UNIT, 
            vehicle.getTransportCapacity().getQuantityUnit().getValue());
        capacityDocument.put(MongoFields.CAPACITY_TIME_UNIT, 
            vehicle.getTransportCapacity().getTimeUnit().toString());
        document.put(MongoFields.TRANSPORT_CAPACITY, capacityDocument);
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
