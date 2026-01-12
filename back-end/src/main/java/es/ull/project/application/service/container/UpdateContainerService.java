package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.UpdateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateContainerService implements UpdateContainerUseCase {

    @Autowired
    private ContainerRepository repository;

    @Override
    public Container update(UUID id, Location newLocation, WasteType newWasteType, WasteDemand newWasteDemand, ServiceZone newServiceZone) {
        Container existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));

        if (newLocation != null) {
            existing.updateLocation(newLocation);
        }
        if (newWasteType != null) {
            existing.updateWasteType(newWasteType);
        }
        if (newWasteDemand != null) {
            existing.updateWasteDemand(newWasteDemand);
        }
        existing.updateServiceZone(newServiceZone);

        Container saved = this.repository.save(existing);
        return saved;
    }
}
