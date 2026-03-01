package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import es.ull.project.adapter.mongo.document.entity.ContainerDocument;
import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
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
        this.springRepo.deleteById(entity.getId().toString());
    }

    @Override
    public List<Container> fetchAll() {
        return this.springRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
        ContainerDocument doc = toDocument(entity);
        ContainerDocument saved = this.springRepo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Container> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return this.springRepo.findById(id.toString()).map(this::toDomain);
    }

    private Container toDomain(ContainerDocument doc) {
        if (doc == null) {
            return null;
        }
        UUID id = UUID.fromString(doc.getId());
        LocationDocument loc = doc.getLocation();
        Location location = new Location(loc.getLatitude(), loc.getLongitude(), loc.getPostalAddress(), loc.getGisReference());
        WasteDemandDocument wd = doc.getWasteDemand();
        QuantityUnit qu = new QuantityUnit(wd.getQuantityUnit());
        TimeUnit timeUnit = TimeUnit.valueOf(wd.getTimeUnit());
        WasteDemand wasteDemand = new WasteDemand(wd.getValue(), qu, timeUnit);
        WasteType wasteType = WasteType.valueOf(doc.getWasteType());
        ServiceZone serviceZone = doc.getServiceZone() != null ? ServiceZone.valueOf(doc.getServiceZone()) : null;
        return new Container(id, location, wasteType, wasteDemand, serviceZone);
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
