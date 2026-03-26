package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.ServiceAssignment;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * ServiceAssignmentWritingConverter
 *
 * Custom converter for writing ServiceAssignment entities to MongoDB documents.
 * This converter ensures proper serialization of complex value objects and entity references.
 */
@WritingConverter
public class ServiceAssignmentWritingConverter implements Converter<ServiceAssignment, Document> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAssignmentWritingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    public ServiceAssignmentWritingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a ServiceAssignment entity to a MongoDB Document.
     *
     * @param serviceAssignment The ServiceAssignment entity to convert.
     * @return The MongoDB Document representation of the ServiceAssignment.
     */
    @Override
    public Document convert(ServiceAssignment serviceAssignment) {
        logger.info("ServiceAssignment with id '{}' to be written", serviceAssignment.getId());
        Document document = new Document();
        document.put(MongoFields.ID, serviceAssignment.getId());
        document.put(MongoFields.CONTAINER_ID, serviceAssignment.getContainer().getId());
        document.put(MongoFields.FACILITY_ID, serviceAssignment.getFacility().getId());
        Document wasteDemandDocument = new Document();
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_VALUE, 
            serviceAssignment.getWasteDemand().getValue());
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_QUANTITY_UNIT, 
            serviceAssignment.getWasteDemand().getQuantityUnit().getValue());
        wasteDemandDocument.put(MongoFields.WASTE_DEMAND_TIME_UNIT, 
            serviceAssignment.getWasteDemand().getTimeUnit().toString());
        document.put(MongoFields.WASTE_DEMAND, wasteDemandDocument);
        document.put(MongoFields.DISTANCE, serviceAssignment.getDistance().getValue());
        document.put(MongoFields.SERVICE_TIME, serviceAssignment.getServiceTime().getValue());
        Document transportCostDocument = new Document();
        transportCostDocument.put(MongoFields.TRANSPORT_COST_AMOUNT, 
            serviceAssignment.getTransportCost().getAmount());
        serviceAssignment.getTransportCost().getCurrency().ifPresent(currency ->
            transportCostDocument.put(MongoFields.TRANSPORT_COST_CURRENCY, 
                currency.getCode())
        );
        document.put(MongoFields.TRANSPORT_COST, transportCostDocument);
        return document;
    }
}
