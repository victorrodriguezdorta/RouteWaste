package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.repository.query.VehicleSearchCriteria;
import es.ull.project.application.usecase.vehicle.ReadVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import es.ull.project.domain.readmodel.EntityTypeBreakdownBuilder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Service responsible for reading vehicle data from the system.
 * This service implements the {@link ReadVehicleUseCase} interface and provides
 * the business logic for vehicle retrieval operations.
 */
public class ReadVehicleService implements ReadVehicleUseCase {

    private final VehicleRepository repository;

    /**
     * Constructs a new ReadVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     */
    public ReadVehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle to retrieve
     * @return the vehicle with the specified identifier
     * @throws NoSuchElementException if no vehicle is found with the given identifier
     */
    @Override
    public Vehicle fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
    }

    /**
     * Retrieves all vehicles from the system.
     *
     * @return a list containing all vehicles
     */
    @Override
    public List<Vehicle> fetchAll() {
        return this.repository.findAll();
    }

    /**
     * Retrieves vehicles from the system using pagination.
     *
     * @param pageable pagination information
     * @return a page containing vehicles
     */
    @Override
    public Page<Vehicle> fetchAll(@NonNull Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    /**
     * Retrieves vehicles using pagination and an optional type filter.
     *
     * @param pageable    pagination and sort information
     * @param vehicleType optional vehicle type to filter by
     * @return a page containing matching vehicles
     */
    @Override
    public Page<Vehicle> fetchAll(@NonNull Pageable pageable, VehicleType vehicleType) {
        return this.repository.findAll(pageable, vehicleType);
    }

    /**
     * Retrieves vehicles using pagination and search criteria.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return a page containing matching vehicles
     */
    @Override
    public Page<Vehicle> fetchAll(@NonNull Pageable pageable, @NonNull VehicleSearchCriteria criteria) {
        return this.repository.findAll(pageable, criteria);
    }

    /**
     * Returns global vehicle statistics (total and per {@link VehicleType}).
     *
     * @return unfiltered type breakdown
     */
    @Override
    public EntityTypeBreakdown fetchStatistics() {
        return EntityTypeBreakdownBuilder.fromCounts(
                this.repository.count(),
                this.repository.countByVehicleType(),
                VehicleType.class);
    }
}
