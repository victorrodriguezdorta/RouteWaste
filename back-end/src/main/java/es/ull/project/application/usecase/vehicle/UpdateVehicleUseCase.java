package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import java.util.UUID;

public interface UpdateVehicleUseCase {
    Vehicle update(UUID id, Vehicle newVehicle);
}
