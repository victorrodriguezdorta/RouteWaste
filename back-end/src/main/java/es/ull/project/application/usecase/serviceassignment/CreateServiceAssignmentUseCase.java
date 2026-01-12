package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

public interface CreateServiceAssignmentUseCase {
    ServiceAssignment create(Container container, Facility facility, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost);
}
