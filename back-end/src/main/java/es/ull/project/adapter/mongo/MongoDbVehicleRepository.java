package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import es.ull.project.adapter.mongo.document.entity.VehicleDocument;
import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;
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
        this.springRepo.deleteById(entity.getId().toString());
    }

    @Override
    public List<Vehicle> fetchAll() {
        return this.springRepo.findAll().stream().map(this::toDomain).collect(Collectors.toList());
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
        VehicleDocument doc = toDocument(entity);
        VehicleDocument saved = this.springRepo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return this.springRepo.findById(id.toString()).map(this::toDomain);
    }

    private Vehicle toDomain(VehicleDocument doc) {
        if (doc == null) {
            return null;
        }
        UUID id = UUID.fromString(doc.getId());
        VehicleType type = VehicleType.valueOf(doc.getVehicleType());
        CapacityDocument cap = doc.getTransportCapacity();
        QuantityUnit qu = new QuantityUnit(cap.getQuantityUnit());
        TimeUnit timeUnit = TimeUnit.valueOf(cap.getTimeUnit());
        Capacity capacity = new Capacity(cap.getValue(), qu, timeUnit);
        TransportationVariableCostDocument costDoc = doc.getCostPerKilometer();
        TransportationVariableCost cost = new TransportationVariableCost(costDoc.getAmount(), costDoc.getCurrency());
        return new Vehicle(id, type, capacity, cost);
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
