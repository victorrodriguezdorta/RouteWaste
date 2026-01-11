package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.UUID;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

public interface CreateServiceAssignmentUseCase {
    ServiceAssignment create(UUID containerId, UUID facilityId, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost);
}
