package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import java.util.UUID;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

public interface UpdateServiceAssignmentUseCase {
    ServiceAssignment update(UUID id, Container container, Facility facility, WasteDemand newWasteDemand, Distance newDistance, ServiceTime newServiceTime, TransportationVariableCost newTransportCost);
}
