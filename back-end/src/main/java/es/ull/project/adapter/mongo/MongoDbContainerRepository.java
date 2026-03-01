package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import es.ull.project.adapter.mongo.document.entity.ContainerDocument;
import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
import es.ull.project.adapter.mongo.exception.DataCorruptionException;
import es.ull.project.adapter.mongo.exception.PersistenceException;
import es.ull.project.adapter.mongo.spring.ContainerSpringRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

/**
 * MongoDB implementation of {@link ContainerRepository} using Spring Data.
 */
public class MongoDbContainerRepository implements ContainerRepository {

    private final ContainerSpringRepository springRepo;

    public MongoDbContainerRepository(ContainerSpringRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public void delete(Container entity) {
        if (entity == null) {
            return;
        }
        try {
            this.springRepo.deleteById(entity.getId().toString());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to delete Container with ID: " + entity.getId(), e);
        }
    }

    @Override
    public List<Container> fetchAll() {
        try {
            return this.springRepo.findAll().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to fetch all Containers", e);
        }
    }

    @Override
    public List<Container> findAll() {
        return fetchAll();
    }

    @Override
    public Container save(Container entity) {
        if (entity == null) {
            return null;
        }
        try {
            ContainerDocument doc = toDocument(entity);
            ContainerDocument saved = this.springRepo.save(doc);
            return toDomain(saved);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to save Container with ID: " + entity.getId(), e);
        }
    }

    @Override
    public Optional<Container> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return this.springRepo.findById(id.toString()).map(this::toDomain);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find Container with ID: " + id, e);
        }
    }

    @Override
    public List<Container> findAllById(List<UUID> ids) {
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
                "Failed to find Containers by IDs", e);
        }
    }

    private Container toDomain(ContainerDocument doc) {
        if (doc == null) {
            return null;
        }
        
        try {
            // Validate document ID
            if (doc.getId() == null || doc.getId().isBlank()) {
                throw new DataCorruptionException("ContainerDocument", "id", doc.getId(),
                    "ID cannot be null or empty");
            }
            UUID id = UUID.fromString(doc.getId());
            
            // Validate and convert Location
            LocationDocument loc = doc.getLocation();
            if (loc == null) {
                throw new DataCorruptionException("ContainerDocument", "location", null,
                    "Location cannot be null");
            }
            Location location = new Location(loc.getLatitude(), loc.getLongitude(), 
                loc.getPostalAddress(), loc.getGisReference());
            
            // Validate and convert WasteDemand
            WasteDemandDocument wd = doc.getWasteDemand();
            if (wd == null) {
                throw new DataCorruptionException("ContainerDocument", "wasteDemand", null,
                    "WasteDemand cannot be null");
            }
            QuantityUnit qu = new QuantityUnit(wd.getQuantityUnit());
            TimeUnit timeUnit = TimeUnit.valueOf(wd.getTimeUnit());
            WasteDemand wasteDemand = new WasteDemand(wd.getValue(), qu, timeUnit);
            
            // Validate and convert WasteType
            if (doc.getWasteType() == null || doc.getWasteType().isBlank()) {
                throw new DataCorruptionException("ContainerDocument", "wasteType", doc.getWasteType(),
                    "WasteType cannot be null or empty");
            }
            WasteType wasteType = WasteType.valueOf(doc.getWasteType());
            
            // Convert optional ServiceZone
            ServiceZone serviceZone = doc.getServiceZone() != null && !doc.getServiceZone().isBlank() 
                ? ServiceZone.valueOf(doc.getServiceZone()) : null;
            
            return new Container(id, location, wasteType, wasteDemand, serviceZone);
            
        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in ContainerDocument: " + e.getMessage(), e);
        }
    }

    private ContainerDocument toDocument(Container entity) {
        if (entity == null) {
            return null;
        }
        String id = entity.getId().toString();
        Location loc = entity.getLocation();
        LocationDocument locDoc = new LocationDocument(loc.getLatitude(), loc.getLongitude(), loc.getPostalAddress(), loc.getGISReference());
        WasteDemand wd = entity.getWasteDemand();
        WasteDemandDocument wdDoc = new WasteDemandDocument(wd.getValue(), wd.getQuantityUnit().getValue(), wd.getTimeUnit().name());
        String wasteType = entity.getWasteType().name();
        String serviceZone = entity.getServiceZone().map(Enum::name).orElse(null);
        return new ContainerDocument(id, locDoc, wasteType, wdDoc, serviceZone);
    }
}
