package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<Vehicle> fetchAll(Pageable pageable);

    /**
     * Retrieves vehicles with pagination and an optional type filter.
     *
     * @param pageable    pagination and sort information
     * @param vehicleType optional vehicle type to filter by (null means no filter)
     * @return a page of vehicles
     */
    Page<Vehicle> fetchAll(Pageable pageable, VehicleType vehicleType);
}
