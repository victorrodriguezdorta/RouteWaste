package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateContainerService implements CreateContainerUseCase {

    @Autowired
    private ContainerRepository repository;

    @Override
    public Container create(Location location, WasteType wasteType, WasteDemand wasteDemand, ServiceZone serviceZone) {
        Container newContainer = new Container(location, wasteType, wasteDemand, serviceZone);
        Container saved = this.repository.save(newContainer);
        return saved;
    }
}
