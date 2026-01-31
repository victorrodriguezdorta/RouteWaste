package es.ull.project.application.service.serviceassignment;

import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.UpdateServiceAssignmentUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * Service responsible for updating existing service assignments in the system.
 * This service implements the {@link UpdateServiceAssignmentUseCase} interface and provides
 * the business logic for service assignment modification operations. Since ServiceAssignment
 * is immutable, updates are performed by creating a new instance with the updated values.
 */
public class UpdateServiceAssignmentService implements UpdateServiceAssignmentUseCase {

    private final ServiceAssignmentRepository repository;
    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;

    /**
     * Constructs a new UpdateServiceAssignmentService with the specified repositories.
     *
     * @param repository the service assignment repository used for persistence operations
     * @param containerRepository the container repository used for fetching containers
     * @param facilityRepository the facility repository used for fetching facilities
     */
    public UpdateServiceAssignmentService(ServiceAssignmentRepository repository, ContainerRepository containerRepository, FacilityRepository facilityRepository) {
        this.repository = repository;
        this.containerRepository = containerRepository;
        this.facilityRepository = facilityRepository;
    }

    /**
     * Updates an existing service assignment with the specified parameters.
     * <p>
     * Only non-null parameters will be updated. Null values are ignored,
     * allowing for partial updates. Since ServiceAssignment is immutable,
     * a new instance is created with the updated values.
     * </p>
     *
     * @param id the unique identifier of the service assignment to update
     * @param containerId the new container ID, or null to keep the current value
     * @param facilityId the new facility ID, or null to keep the current value
     * @param newWasteDemand the new waste demand, or null to keep the current value
     * @param newDistance the new distance, or null to keep the current value
     * @param newServiceTime the new service time, or null to keep the current value
     * @param newTransportCost the new transport cost, or null to keep the current value
     * @return the updated service assignment
     * @throws NoSuchElementException if no service assignment is found with the given identifier
     */
    @Override
    public ServiceAssignment update(UUID id, UUID containerId, UUID facilityId, WasteDemand newWasteDemand, Distance newDistance, ServiceTime newServiceTime, TransportationVariableCost newTransportCost) {
        ServiceAssignment existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
        Container updatedContainer = containerId != null 
            ? this.containerRepository.findById(containerId).orElseThrow(() -> new NoSuchElementException("Container not found"))
            : existing.getContainer();
        Facility updatedFacility = facilityId != null 
            ? this.facilityRepository.findById(facilityId).orElseThrow(() -> new NoSuchElementException("Facility not found"))
            : existing.getFacility();
        WasteDemand updatedDemand = newWasteDemand != null ? newWasteDemand : existing.getWasteDemand();
        Distance updatedDistance = newDistance != null ? newDistance : existing.getDistance();
        ServiceTime updatedServiceTime = newServiceTime != null ? newServiceTime : existing.getServiceTime();
        TransportationVariableCost updatedTransportCost = newTransportCost != null ? newTransportCost : existing.getTransportCost();
        ServiceAssignment updated = new ServiceAssignment(updatedContainer, updatedFacility, updatedDemand, updatedDistance, updatedServiceTime, updatedTransportCost);
        ServiceAssignment saved = this.repository.save(updated);
        return saved;
    }
}
