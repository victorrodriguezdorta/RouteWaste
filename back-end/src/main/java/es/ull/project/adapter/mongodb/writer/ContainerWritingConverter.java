package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * ContainerWritingConverter
 *
 * Implements custom conversion logic for transforming Container entities
 * into MongoDB documents. This converter handles the serialization of
 * Container objects including their value objects (Location, ContainerCapacityLiters,
 * DailyWasteDemandLitersPerDay) into a MongoDB-compatible document structure.
 */
@WritingConverter
public class ContainerWritingConverter implements Converter<Container, Document> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new ContainerWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public ContainerWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a Container entity into a MongoDB Document.
     *
     * @param container Container entity to convert
     * @return MongoDB Document representation of the Container
     */
    @Override
    public Document convert(@NonNull Container container) {
        logger.info("Container with id '{}' to be written", container.getId());
        Document document = new Document();
        document.put(MongoFields.ID, container.getId());
        Document locationDocument = new Document();
        locationDocument.put(MongoFields.LATITUDE, container.getLocation().getLatitude());
        locationDocument.put(MongoFields.LONGITUDE, container.getLocation().getLongitude());
        locationDocument.put(MongoFields.POSTAL_ADDRESS, container.getLocation().getPostalAddress());
        locationDocument.put(MongoFields.GIS_REFERENCE, container.getLocation().getGISReference());
        document.put(MongoFields.LOCATION, locationDocument);
        document.put(MongoFields.WASTE_TYPE, container.getWasteType().toString());
        document.put(MongoFields.CAPACITY_LITERS, container.getCapacityLiters().getLiters());
        document.put(MongoFields.DAILY_DEMAND_LITERS_PER_DAY, container.getDailyDemandLitersPerDay().getLitersPerDay());
        container.getServiceZone().ifPresent(serviceZone -> 
            document.put(MongoFields.SERVICE_ZONE, serviceZone.toString())
        );
        return document;
    }
}
