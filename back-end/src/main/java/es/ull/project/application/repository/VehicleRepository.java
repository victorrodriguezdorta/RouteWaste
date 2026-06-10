package es.ull.project.application.repository;

import es.ull.project.application.query.VehicleSearchCriteria;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

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
     * Find vehicles using pagination.
     *
     * @param pageable pagination configuration
     * @return page of vehicles
     */
    public abstract Page<Vehicle> findAll(@NonNull Pageable pageable);

    /**
     * Find vehicles using pagination and an optional type filter.
     *
     * @param pageable    pagination and sort configuration
     * @param vehicleType optional vehicle type to filter by (null means no filter)
     * @return page of matching vehicles
     */
    public abstract Page<Vehicle> findAll(@NonNull Pageable pageable, VehicleType vehicleType);

    /**
     * Find vehicles using pagination and search criteria.
     *
     * @param pageable pagination and sort configuration
     * @param criteria search criteria with optional filters
     * @return page of matching vehicles
     */
    public abstract Page<Vehicle> findAll(@NonNull Pageable pageable, @NonNull VehicleSearchCriteria criteria);

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

    /**
     * Counts all vehicles in the repository.
     *
     * @return total vehicle count
     */
    public abstract long count();

    /**
     * Counts vehicles grouped by {@link VehicleType}.
     *
     * @return map with every vehicle type and its count (zero when none)
     */
    public abstract Map<VehicleType, Long> countByVehicleType();
}
