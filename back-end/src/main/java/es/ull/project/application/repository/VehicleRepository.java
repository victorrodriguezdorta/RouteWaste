package es.ull.project.application.repository;

import es.ull.project.domain.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    List<Vehicle> findAll();
    void delete(Vehicle vehicle);
}
