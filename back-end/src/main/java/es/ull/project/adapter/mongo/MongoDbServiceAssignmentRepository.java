package es.ull.project.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import es.ull.project.adapter.mongo.document.entity.ServiceAssignmentDocument;
import es.ull.project.adapter.mongo.document.valueobject.DistanceDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServiceTimeDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
import es.ull.project.adapter.mongo.spring.ServiceAssignmentSpringRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;

public class MongoDbServiceAssignmentRepository implements ServiceAssignmentRepository {

    private final ServiceAssignmentSpringRepository springRepository;
    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;

    public MongoDbServiceAssignmentRepository(ServiceAssignmentSpringRepository springRepository,
            ContainerRepository containerRepository,
            FacilityRepository facilityRepository) {
        this.springRepository = springRepository;
        this.containerRepository = containerRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public ServiceAssignment save(ServiceAssignment assignment) {
        ServiceAssignmentDocument doc = toDocument(assignment);
        ServiceAssignmentDocument saved = springRepository.save(doc);
        return toDomain(saved);
    }

    @Override
    public void delete(ServiceAssignment entity) {
        if (entity == null) {
            return;
        }
        this.springRepository.deleteById(entity.getId().toString());
    }

    @Override
    public List<ServiceAssignment> fetchAll() {
        return this.springRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceAssignment> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return springRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public List<ServiceAssignment> findAll() {
        return springRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private ServiceAssignmentDocument toDocument(ServiceAssignment s) {
        if (s == null) {
            return null;
        }
        String id = s.getId().toString();
        String containerId = s.getContainer().getId().toString();
        String facilityId = s.getFacility().getId().toString();
        DistanceDocument distance = new DistanceDocument(s.getDistance().toMeters());
        ServiceTimeDocument time = new ServiceTimeDocument(s.getServiceTime().getValue());
        WasteDemand wd = s.getWasteDemand();
        WasteDemandDocument wdDoc = new WasteDemandDocument(wd.getValue(), wd.getQuantityUnit().getValue(), wd.getTimeUnit().name());
        TransportationVariableCost tc = s.getTransportCost();
        TransportationVariableCostDocument tcDoc = new TransportationVariableCostDocument(tc.getAmount(), tc.getCurrency().map(c -> c.getCode()).orElse("EUR"));
        return new ServiceAssignmentDocument(id, containerId, facilityId, distance, time, wdDoc, tcDoc);
    }

    private ServiceAssignment toDomain(ServiceAssignmentDocument d) {
        if (d == null) {
            return null;
        }
        UUID id = UUID.fromString(d.getId());
        // fetch container and facility aggregates
        Container container = this.containerRepository.findById(UUID.fromString(d.getContainerId())).orElse(null);
        Facility facility = this.facilityRepository.findById(UUID.fromString(d.getFacilityId())).orElse(null);
        WasteDemandDocument wdDoc = d.getWasteDemand();
        WasteDemand wd = new WasteDemand(wdDoc.getValue(), new es.ull.project.domain.valueobject.demand.QuantityUnit(wdDoc.getQuantityUnit()), java.util.concurrent.TimeUnit.valueOf(wdDoc.getTimeUnit()));
        DistanceDocument distDoc = d.getDistance();
        es.ull.project.domain.valueobject.location.Distance dist = es.ull.project.domain.valueobject.location.Distance.fromMeters(distDoc.getMeters());
        ServiceTimeDocument stDoc = d.getServiceTime();
        es.ull.project.domain.valueobject.location.ServiceTime st = new es.ull.project.domain.valueobject.location.ServiceTime(stDoc.getMinutes());
        TransportationVariableCostDocument tcDoc = d.getTransportCost();
        TransportationVariableCost tc = new TransportationVariableCost(tcDoc.getAmount(), tcDoc.getCurrency());
        return new ServiceAssignment(id, container, facility, wd, dist, st, tc);
    }
}
