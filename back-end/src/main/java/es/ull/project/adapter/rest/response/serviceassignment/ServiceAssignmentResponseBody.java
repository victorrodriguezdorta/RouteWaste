package es.ull.project.adapter.rest.response.serviceassignment;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a ServiceAssignment response.
 * This class is used to send ServiceAssignment data in HTTP responses.
 *
 * Following DDD principles, this DTO uses domain value objects directly
 * instead of primitive types or nested response body classes. The serializer
 * handles the conversion to JSON format.
 *
 * It contains all the data of a service assignment in a structured format,
 * including the facility identifier and complete information about the containers.
 */
public class ServiceAssignmentResponseBody {

    /**
     * Unique identifier of the service assignment.
     */
    public UUID id;

    /**
     * Unique identifier of the parent infrastructure plan.
     */
    public UUID infrastructurePlanId;

    /**
     * Unique identifier of the facility associated with this service assignment.
     */
    public UUID facilityId;

    /**
     * List of containers assigned to this facility.
     */
    public List<ContainerResponseBody> assignedContainers;
}
