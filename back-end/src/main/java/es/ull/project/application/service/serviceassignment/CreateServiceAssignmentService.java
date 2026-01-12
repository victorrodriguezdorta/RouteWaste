package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.CreateServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateServiceAssignmentService implements CreateServiceAssignmentUseCase {

    @Autowired
    private ServiceAssignmentRepository repository;

    @Override
    public ServiceAssignment create(Container container, Facility facility, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost) {
        ServiceAssignment newAssignment = new ServiceAssignment(container, facility, wasteDemand, distance, serviceTime, transportCost);
        ServiceAssignment saved = this.repository.save(newAssignment);
        return saved;
    }
}
