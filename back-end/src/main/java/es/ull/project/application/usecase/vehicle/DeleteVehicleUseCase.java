package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import java.util.UUID;

public interface DeleteVehicleUseCase {
    Vehicle delete(UUID id);
}
