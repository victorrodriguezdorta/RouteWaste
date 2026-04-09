package es.ull.project.adapter.mongodb.repository;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/**
 * MongoDB implementation of the VehicleRepository interface.
 *
 * This class provides concrete implementations for CRUD operations
 * on Vehicle entities using MongoDB as the data store.
 * Active when "memory" profile is NOT enabled (default).
 */
@Repository
@Profile("!memory")
public class VehicleMongoRepository implements VehicleRepository {

    public static final String COLLECTION_NAME = "vehicles";
    private static final String FIELD_VEHICLE_TYPE = "vehicleType";
    private static final String FIELD_ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Delete a vehicle from the repository.
     *
     * @param entity Vehicle to delete
     */
    @Override
    public void delete(Vehicle entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Fetch all vehicles (domain naming).
     *
     * @return list of vehicles
     */
    @Override
    public List<Vehicle> fetchAll() {
        return this.mongoTemplate.findAll(Vehicle.class, COLLECTION_NAME);
    }

    /**
     * Find all vehicles (alias expected by some services).
     *
     * @return list of vehicles
     */
    @Override
    public List<Vehicle> findAll() {
        return this.mongoTemplate.findAll(Vehicle.class, COLLECTION_NAME);
    }

    /**
     * Find all vehicles using pagination.
     *
     * @param pageable pagination information
     * @return page of vehicles
     */
    @Override
    public Page<Vehicle> findAll(@NonNull Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Vehicle> vehicles = this.mongoTemplate.find(query, Vehicle.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(new Query(), Vehicle.class, COLLECTION_NAME);
        return new PageImpl<>(vehicles, pageable, total);
    }

    /**
     * Find vehicles with pagination and an optional type filter.
     *
     * @param pageable    pagination and sort configuration
     * @param vehicleType optional vehicle type filter (null means no filter)
     * @return page of matching vehicles
     */
    @Override
    public Page<Vehicle> findAll(@NonNull Pageable pageable, VehicleType vehicleType) {
        Query dataQuery = new Query();
        Query countQuery = new Query();
        if (vehicleType != null) {
            Criteria criteria = Criteria.where(FIELD_VEHICLE_TYPE).is(vehicleType);
            dataQuery.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        dataQuery.with(pageable);
        List<Vehicle> vehicles = this.mongoTemplate.find(dataQuery, Vehicle.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(countQuery, Vehicle.class, COLLECTION_NAME);
        return new PageImpl<>(vehicles, pageable, total);
    }

    /**
     * Save or update a vehicle.
     *
     * @param entity Vehicle to persist
     * @return persisted Vehicle
     */
    @Override
    public Vehicle save(Vehicle entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Find a vehicle by UUID.
     *
     * @param id vehicle UUID
     * @return optional containing the vehicle if found
     */
    @Override
    public Optional<Vehicle> findById(UUID id) {
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        Vehicle vehicle = this.mongoTemplate.findOne(query, Vehicle.class, COLLECTION_NAME);
        return Optional.ofNullable(vehicle);
    }
}
