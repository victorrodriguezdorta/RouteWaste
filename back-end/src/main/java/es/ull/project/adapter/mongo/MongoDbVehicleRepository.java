package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import es.ull.project.adapter.mongo.document.entity.VehicleDocument;
import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;
import es.ull.project.adapter.mongo.exception.DataCorruptionException;
import es.ull.project.adapter.mongo.exception.PersistenceException;
import es.ull.project.adapter.mongo.spring.VehicleSpringRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;

/**
 * MongoDB implementation of {@link VehicleRepository} using Spring Data.
 */
public class MongoDbVehicleRepository implements VehicleRepository {

    private final VehicleSpringRepository springRepo;

    public MongoDbVehicleRepository(VehicleSpringRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public void delete(Vehicle entity) {
        if (entity == null) {
            return;
        }
        try {
            this.springRepo.deleteById(entity.getId().toString());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to delete Vehicle with ID: " + entity.getId(), e);
        }
    }

    @Override
    public List<Vehicle> fetchAll() {
        try {
            return this.springRepo.findAll().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to fetch all Vehicles", e);
        }
    }

    @Override
    public List<Vehicle> findAll() {
        return fetchAll();
    }

    @Override
    public Vehicle save(Vehicle entity) {
        if (entity == null) {
            return null;
        }
        try {
            VehicleDocument doc = toDocument(entity);
            VehicleDocument saved = this.springRepo.save(doc);
            return toDomain(saved);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to save Vehicle with ID: " + entity.getId(), e);
        }
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return this.springRepo.findById(id.toString()).map(this::toDomain);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find Vehicle with ID: " + id, e);
        }
    }

    private Vehicle toDomain(VehicleDocument doc) {
        if (doc == null) {
            return null;
        }
        
        try {
            // Validate document ID
            if (doc.getId() == null || doc.getId().isBlank()) {
                throw new DataCorruptionException("VehicleDocument", "id", doc.getId(),
                    "ID cannot be null or empty");
            }
            UUID id = UUID.fromString(doc.getId());
            
            // Validate and convert VehicleType
            if (doc.getVehicleType() == null || doc.getVehicleType().isBlank()) {
                throw new DataCorruptionException("VehicleDocument", "vehicleType", doc.getVehicleType(),
                    "VehicleType cannot be null or empty");
            }
            VehicleType type = VehicleType.valueOf(doc.getVehicleType());
            
            // Validate and convert Capacity
            CapacityDocument cap = doc.getTransportCapacity();
            if (cap == null) {
                throw new DataCorruptionException("VehicleDocument", "transportCapacity", null,
                    "TransportCapacity cannot be null");
            }
            QuantityUnit qu = new QuantityUnit(cap.getQuantityUnit());
            TimeUnit timeUnit = TimeUnit.valueOf(cap.getTimeUnit());
            Capacity capacity = new Capacity(cap.getValue(), qu, timeUnit);
            
            // Validate and convert TransportationVariableCost
            TransportationVariableCostDocument costDoc = doc.getCostPerKilometer();
            if (costDoc == null) {
                throw new DataCorruptionException("VehicleDocument", "costPerKilometer", null,
                    "CostPerKilometer cannot be null");
            }
            TransportationVariableCost cost = new TransportationVariableCost(
                costDoc.getAmount(), costDoc.getCurrency());
            
            return new Vehicle(id, type, capacity, cost);
            
        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in VehicleDocument: " + e.getMessage(), e);
        }
    }

    private VehicleDocument toDocument(Vehicle entity) {
        if (entity == null) {
            return null;
        }
        String id = entity.getId().toString();
        String vehicleType = entity.getVehicleType().name();
        Capacity cap = entity.getTransportCapacity();
        CapacityDocument capDoc = new CapacityDocument(cap.getValue(), cap.getQuantityUnit().getValue(), cap.getTimeUnit().name());
        TransportationVariableCost cost = entity.getCostPerKilometer();
        TransportationVariableCostDocument costDoc = new TransportationVariableCostDocument(cost.getAmount(), cost.getCurrency().map(c -> c.getCode()).orElse("EUR"));
        return new VehicleDocument(id, vehicleType, capDoc, costDoc);
    }
}
