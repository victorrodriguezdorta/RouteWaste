package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.UpdateServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateServiceAssignmentService implements UpdateServiceAssignmentUseCase {

    @Autowired
    private ServiceAssignmentRepository repository;

    @Override
    public ServiceAssignment update(UUID id, Container container, Facility facility, WasteDemand newWasteDemand, Distance newDistance, ServiceTime newServiceTime, TransportationVariableCost newTransportCost) {
        ServiceAssignment existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));

        // ServiceAssignment is immutable, so create a new instance with updated values.
        Container updatedContainer = container != null ? container : existing.getContainer();
        Facility updatedFacility = facility != null ? facility : existing.getFacility();
        WasteDemand updatedDemand = newWasteDemand != null ? newWasteDemand : existing.getWasteDemand();
        Distance updatedDistance = newDistance != null ? newDistance : existing.getDistance();
        ServiceTime updatedServiceTime = newServiceTime != null ? newServiceTime : existing.getServiceTime();
        TransportationVariableCost updatedTransportCost = newTransportCost != null ? newTransportCost : existing.getTransportCost();

        ServiceAssignment updated = new ServiceAssignment(updatedContainer, updatedFacility, updatedDemand, updatedDistance, updatedServiceTime, updatedTransportCost);
        ServiceAssignment saved = this.repository.save(updated);
        return saved;
    }
}
