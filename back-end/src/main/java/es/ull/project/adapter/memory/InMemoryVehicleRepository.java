package es.ull.project.adapter.memory;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.domain.entity.Vehicle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * In-memory VehicleRepository for tests and local runs.
 */
@Repository
public class InMemoryVehicleRepository implements VehicleRepository {

    private final Map<UUID, Vehicle> store = new LinkedHashMap<>();

    /**
     * Deletes a vehicle from the repository.
     *
     * @param entity the vehicle to delete
     */
    @Override
    public void delete(Vehicle entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all vehicles from the repository.
     *
     * @return a list of all vehicles
     */
    @Override
    public List<Vehicle> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all vehicles in the repository.
     *
     * @return a list of all vehicles
     */
    @Override
    public List<Vehicle> findAll() {
        return fetchAll();
    }

    /**
     * Saves a vehicle to the repository.
     *
     * @param entity the vehicle to save
     * @return the saved vehicle, or null if the entity was null
     */
    @Override
    public Vehicle save(Vehicle entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle
     * @return an Optional containing the vehicle if found, or empty if not found
     */
    @Override
    public Optional<Vehicle> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
