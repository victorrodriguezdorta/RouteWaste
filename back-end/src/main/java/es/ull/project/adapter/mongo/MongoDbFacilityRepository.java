package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import es.ull.project.adapter.mongo.document.entity.FacilityDocument;
import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.OpeningFixedCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
import es.ull.project.adapter.mongo.exception.DataCorruptionException;
import es.ull.project.adapter.mongo.exception.PersistenceException;
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
        try {
            this.springRepo.deleteById(entity.getId().toString());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to delete Facility with ID: " + entity.getId(), e);
        }
    }

    @Override
    public List<Facility> fetchAll() {
        try {
            return this.springRepo.findAll().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to fetch all Facilities", e);
        }
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
        try {
            FacilityDocument doc = toDocument(entity);
            FacilityDocument saved = this.springRepo.save(doc);
            return toDomain(saved);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to save Facility with ID: " + entity.getId(), e);
        }
    }

    @Override
    public Optional<Facility> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return this.springRepo.findById(id.toString()).map(this::toDomain);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find Facility with ID: " + id, e);
        }
    }

    @Override
    public List<Facility> findAllById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        try {
            List<String> stringIds = ids.stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            return this.springRepo.findAllById(stringIds).stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find Facilities by IDs", e);
        }
    }

    private Facility toDomain(FacilityDocument doc) {
        if (doc == null) {
            return null;
        }
        
        try {
            // Validate document ID
            if (doc.getId() == null || doc.getId().isBlank()) {
                throw new DataCorruptionException("FacilityDocument", "id", doc.getId(),
                    "ID cannot be null or empty");
            }
            UUID id = UUID.fromString(doc.getId());
            
            // Validate and convert FacilityType
            if (doc.getFacilityType() == null || doc.getFacilityType().isBlank()) {
                throw new DataCorruptionException("FacilityDocument", "facilityType", doc.getFacilityType(),
                    "FacilityType cannot be null or empty");
            }
            FacilityType type = FacilityType.valueOf(doc.getFacilityType());
            
            // Validate and convert Location
            LocationDocument loc = doc.getLocation();
            if (loc == null) {
                throw new DataCorruptionException("FacilityDocument", "location", null,
                    "Location cannot be null");
            }
            Location location = new Location(loc.getLatitude(), loc.getLongitude(), 
                loc.getPostalAddress(), loc.getGisReference());
            
            // Validate and convert Capacity
            CapacityDocument cap = doc.getCapacity();
            if (cap == null) {
                throw new DataCorruptionException("FacilityDocument", "capacity", null,
                    "Capacity cannot be null");
            }
            QuantityUnit qu = new QuantityUnit(cap.getQuantityUnit());
            TimeUnit timeUnit = TimeUnit.valueOf(cap.getTimeUnit());
            Capacity capacity = new Capacity(cap.getValue(), qu, timeUnit);
            
            // Validate and convert OpeningFixedCost
            OpeningFixedCostDocument ofc = doc.getOpeningFixedCost();
            if (ofc == null) {
                throw new DataCorruptionException("FacilityDocument", "openingFixedCost", null,
                    "OpeningFixedCost cannot be null");
            }
            OpeningFixedCost openingFixedCost = new OpeningFixedCost(ofc.getAmount(), ofc.getCurrency());
            
            // Validate and convert FacilityStatus
            if (doc.getStatus() == null || doc.getStatus().isBlank()) {
                throw new DataCorruptionException("FacilityDocument", "status", doc.getStatus(),
                    "Status cannot be null or empty");
            }
            FacilityStatus status = FacilityStatus.valueOf(doc.getStatus());
            
            // Convert optional WasteDemand
            WasteDemandDocument wd = doc.getAssignedWasteDemand();
            WasteDemand assigned = wd != null 
                ? new WasteDemand(wd.getValue(), new QuantityUnit(wd.getQuantityUnit()), 
                    TimeUnit.valueOf(wd.getTimeUnit())) 
                : null;
            
            return new Facility(id, type, location, capacity, openingFixedCost, status, assigned);
            
        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in FacilityDocument: " + e.getMessage(), e);
        }
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
