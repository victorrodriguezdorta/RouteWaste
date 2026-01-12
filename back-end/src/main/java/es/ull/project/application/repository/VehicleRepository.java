package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.domain.entity.Vehicle;

/**
 * Repository interface for Vehicle aggregate roots.
 *
 * Declares persistence operations used by application services
 * to manage vehicles without coupling to a concrete datastore.
 */
public interface VehicleRepository {

    /**
     * Delete a vehicle from the repository.
     *
     * @param entity Vehicle to delete
     */
    public abstract void delete(Vehicle entity);

    /**
     * Fetch all vehicles (domain naming).
     *
     * @return list of vehicles
     */
    public abstract List<Vehicle> fetchAll();

    /**
     * Find all vehicles (alias expected by some services).
     *
     * @return list of vehicles
     */
    public abstract List<Vehicle> findAll();

    /**
     * Save or update a vehicle.
     *
     * @param entity Vehicle to persist
     * @return persisted Vehicle
     */
    public abstract Vehicle save(Vehicle entity);

    /**
     * Find a vehicle by UUID.
     *
     * @param id vehicle UUID
     * @return optional containing the vehicle if found
     */
    public abstract Optional<Vehicle> findById(UUID id);
}
