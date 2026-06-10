package es.ull.project.application.usecase.vehicle;

import es.ull.project.application.query.VehicleSearchCriteria;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Use case interface for reading vehicles.
 *
 * @see Vehicle
 */
public interface ReadVehicleUseCase {

    /**
     * Retrieves a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle
     * @return the vehicle with the specified identifier
     */
    Vehicle fetch(UUID id);

    /**
     * Retrieves all vehicles from the system.
     *
     * @return a list containing all vehicles
     */
    List<Vehicle> fetchAll();

    /**
     * Retrieves vehicles using pagination parameters.
     *
     * @param pageable pagination information
     * @return a page of vehicles
     */
    Page<Vehicle> fetchAll(@NonNull Pageable pageable);

    /**
     * Retrieves vehicles with pagination and an optional type filter.
     *
     * @param pageable    pagination and sort information
     * @param vehicleType optional vehicle type to filter by (null means no filter)
     * @return a page of vehicles
     */
    Page<Vehicle> fetchAll(@NonNull Pageable pageable, VehicleType vehicleType);

    /**
     * Retrieves vehicles with pagination and search criteria.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return a page of matching vehicles
     */
    Page<Vehicle> fetchAll(@NonNull Pageable pageable, @NonNull VehicleSearchCriteria criteria);

    /**
     * Returns global vehicle statistics: total count and count per {@link VehicleType}.
     *
     * @return unfiltered type breakdown
     */
    EntityTypeBreakdown fetchStatistics();
}
