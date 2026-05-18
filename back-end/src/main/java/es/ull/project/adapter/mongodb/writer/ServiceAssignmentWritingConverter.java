package es.ull.project.adapter.mongodb.writer;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ServiceAssignment;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

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

    /**
     * Constructs a new ServiceAssignmentWritingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
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
    public Document convert(@NonNull ServiceAssignment serviceAssignment) {
        logger.info("ServiceAssignment with id '{}' to be written", serviceAssignment.getId());
        Document document = new Document();
        document.put(MongoFields.ID, serviceAssignment.getId());
        document.put(MongoFields.INFRASTRUCTURE_PLAN_ID, serviceAssignment.getInfrastructurePlan().getId());
        document.put(MongoFields.FACILITY_ID, serviceAssignment.getFacility().getId());
        List<UUID> containerIds = new ArrayList<>(serviceAssignment.getAssignedContainers().size());
        for (Container container : serviceAssignment.getAssignedContainers()) {
            containerIds.add(container.getId());
        }
        document.put(MongoFields.ASSIGNED_CONTAINERS, containerIds);
        return document;
    }
}
