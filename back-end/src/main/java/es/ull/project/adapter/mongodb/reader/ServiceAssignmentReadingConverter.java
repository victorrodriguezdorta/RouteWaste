package es.ull.project.adapter.mongodb.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * ServiceAssignmentReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into ServiceAssignment entities. This converter handles the deserialization of
 * MongoDB documents including their entity references (Container, Facility) by
 * loading them from their respective repositories, and their nested value objects
 * (WasteDemand, Distance, ServiceTime, TransportationVariableCost) into a properly
 * constructed ServiceAssignment domain entity.
 */
@ReadingConverter
public class ServiceAssignmentReadingConverter implements Converter<Document, ServiceAssignment> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAssignmentReadingConverter.class);

    private static final String ERR_FACILITY_SNAPSHOT = "Facility with id '%s' not found when loading ServiceAssignment";
    private static final String ERR_CONTAINER_SNAPSHOT = "Container with id '%s' not found when loading ServiceAssignment";

    private MongoConfiguration mongoConfiguration;

    /**
     * Constructor with MongoConfiguration dependency.
     *
     * @param mongoConfiguration MongoDB configuration for accessing repositories
     */
    public ServiceAssignmentReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a ServiceAssignment entity.
     *
     * @param document MongoDB Document to convert
     * @return ServiceAssignment entity reconstructed from the document
     */
    @Override
    public ServiceAssignment convert(@NonNull Document document) {
        logger.info("ServiceAssignment to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        UUID planId = (UUID) document.get(MongoFields.INFRASTRUCTURE_PLAN_ID);
        InfrastructurePlan infrastructurePlan = InfrastructurePlan.forIdReferenceOnly(planId);
        UUID facilityId = (UUID) document.get(MongoFields.FACILITY_ID);
        Facility facility = mongoConfiguration.facilityRepository().findById(facilityId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_FACILITY_SNAPSHOT, facilityId)));
        List<?> containerIdsList = document.get(MongoFields.ASSIGNED_CONTAINERS, List.class);
        List<Container> assignedContainers = new ArrayList<>();
        if (containerIdsList != null) {
            for (Object idObj : containerIdsList) {
                UUID containerId = (UUID) idObj;
                Container container = mongoConfiguration.containerRepository().findById(containerId)
                        .orElseThrow(() -> new IllegalStateException(String.format(ERR_CONTAINER_SNAPSHOT, containerId)));
                assignedContainers.add(container);
            }
        }
        return new ServiceAssignment(
                id,
                infrastructurePlan,
                facility,
                assignedContainers);
    }


}
