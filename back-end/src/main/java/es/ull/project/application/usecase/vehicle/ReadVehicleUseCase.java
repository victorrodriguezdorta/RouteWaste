package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import java.util.List;
import java.util.UUID;

public interface ReadVehicleUseCase {
    Vehicle fetch(UUID id);
    List<Vehicle> fetchAll();
}
