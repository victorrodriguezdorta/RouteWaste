package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import es.ull.project.adapter.mongo.document.entity.FacilityDocument;
import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.OpeningFixedCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
import es.ull.project.adapter.mongo.spring.FacilitySpringRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

/**
 * MongoDB implementation of {@link FacilityRepository} using Spring Data.
 */
public class MongoDbFacilityRepository implements FacilityRepository {

    private final FacilitySpringRepository springRepo;

    public MongoDbFacilityRepository(FacilitySpringRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public void delete(Facility entity) {
        if (entity == null) {
            return;
        }
        this.springRepo.deleteById(entity.getId().toString());
    }

    @Override
    public List<Facility> fetchAll() {
        return this.springRepo.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Facility> findAll() {
        return fetchAll();
    }

    @Override
    public Facility save(Facility entity) {
        if (entity == null) {
            return null;
        }
        FacilityDocument doc = toDocument(entity);
        FacilityDocument saved = this.springRepo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Facility> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return this.springRepo.findById(id.toString()).map(this::toDomain);
    }

    private Facility toDomain(FacilityDocument doc) {
        if (doc == null) {
            return null;
        }
        UUID id = UUID.fromString(doc.getId());
        FacilityType type = FacilityType.valueOf(doc.getFacilityType());
        LocationDocument loc = doc.getLocation();
        Location location = new Location(loc.getLatitude(), loc.getLongitude(), loc.getPostalAddress(), loc.getGisReference());
        CapacityDocument cap = doc.getCapacity();
        QuantityUnit qu = new QuantityUnit(cap.getQuantityUnit());
        TimeUnit timeUnit = TimeUnit.valueOf(cap.getTimeUnit());
        Capacity capacity = new Capacity(cap.getValue(), qu, timeUnit);
        OpeningFixedCostDocument ofc = doc.getOpeningFixedCost();
        OpeningFixedCost openingFixedCost = new OpeningFixedCost(ofc.getAmount(), ofc.getCurrency());
        FacilityStatus status = FacilityStatus.valueOf(doc.getStatus());
        WasteDemandDocument wd = doc.getAssignedWasteDemand();
        WasteDemand assigned = wd != null ? new WasteDemand(wd.getValue(), new QuantityUnit(wd.getQuantityUnit()), TimeUnit.valueOf(wd.getTimeUnit())) : null;
        return new Facility(id, type, location, capacity, openingFixedCost, status, assigned);
    }

    private FacilityDocument toDocument(Facility entity) {
        if (entity == null) {
            return null;
        }
        String id = entity.getId().toString();
        String type = entity.getFacilityType().name();
        Location loc = entity.getLocation();
        LocationDocument locDoc = new LocationDocument(loc.getLatitude(), loc.getLongitude(), loc.getPostalAddress(), loc.getGISReference());
        Capacity cap = entity.getCapacity();
        CapacityDocument capDoc = new CapacityDocument(cap.getValue(), cap.getQuantityUnit().getValue(), cap.getTimeUnit().name());
        OpeningFixedCost ofc = entity.getOpeningFixedCost();
        OpeningFixedCostDocument ofcDoc = new OpeningFixedCostDocument(ofc.getAmount(), ofc.getCurrency().map(c -> c.getCode()).orElse("EUR"));
        String status = entity.getStatus().name();
        WasteDemand assigned = entity.getAssignedWasteDemand();
        WasteDemandDocument wdDoc = new WasteDemandDocument(assigned.getValue(), assigned.getQuantityUnit().getValue(), assigned.getTimeUnit().name());
        return new FacilityDocument(id, type, locDoc, capDoc, ofcDoc, status, wdDoc);
    }
}
