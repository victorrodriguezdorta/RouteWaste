package es.ull.project.adapter.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import es.ull.project.adapter.mongo.document.entity.ServiceAssignmentDocument;
import es.ull.project.adapter.mongo.document.valueobject.DistanceDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServiceTimeDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;
import es.ull.project.adapter.mongo.exception.DataCorruptionException;
import es.ull.project.adapter.mongo.exception.EntityNotFoundException;
import es.ull.project.adapter.mongo.exception.PersistenceException;
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
        try {
            ServiceAssignmentDocument doc = toDocument(assignment);
            ServiceAssignmentDocument saved = springRepository.save(doc);
            return toDomain(saved);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to save ServiceAssignment with ID: " + assignment.getId(), e);
        }
    }

    @Override
    public void delete(ServiceAssignment entity) {
        if (entity == null) {
            return;
        }
        try {
            this.springRepository.deleteById(entity.getId().toString());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to delete ServiceAssignment with ID: " + entity.getId(), e);
        }
    }

    @Override
    public List<ServiceAssignment> fetchAll() {
        try {
            List<ServiceAssignmentDocument> documents = this.springRepository.findAll();
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to fetch all ServiceAssignments", e);
        }
    }

    @Override
    public Optional<ServiceAssignment> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return springRepository.findById(id.toString()).map(this::toDomain);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find ServiceAssignment with ID: " + id, e);
        }
    }

    @Override
    public List<ServiceAssignment> findAll() {
        try {
            List<ServiceAssignmentDocument> documents = this.springRepository.findAll();
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to find all ServiceAssignments", e);
        }
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
        
        try {
            // Validate document ID
            if (d.getId() == null || d.getId().isBlank()) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "id", d.getId(),
                    "ID cannot be null or empty");
            }
            UUID id = UUID.fromString(d.getId());
            
            // Validate and fetch container reference
            if (d.getContainerId() == null || d.getContainerId().isBlank()) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "containerId", d.getContainerId(),
                    "Container ID cannot be null or empty");
            }
            UUID containerId = UUID.fromString(d.getContainerId());
            Container container = this.containerRepository.findById(containerId)
                    .orElseThrow(() -> new EntityNotFoundException("Container", containerId, 
                        "Referenced by ServiceAssignment " + id));
            
            // Validate and fetch facility reference
            if (d.getFacilityId() == null || d.getFacilityId().isBlank()) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "facilityId", d.getFacilityId(),
                    "Facility ID cannot be null or empty");
            }
            UUID facilityId = UUID.fromString(d.getFacilityId());
            Facility facility = this.facilityRepository.findById(facilityId)
                    .orElseThrow(() -> new EntityNotFoundException("Facility", facilityId,
                        "Referenced by ServiceAssignment " + id));
            
            // Validate and convert WasteDemand
            WasteDemandDocument wdDoc = d.getWasteDemand();
            if (wdDoc == null) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "wasteDemand", null,
                    "WasteDemand cannot be null");
            }
            WasteDemand wd = new WasteDemand(
                wdDoc.getValue(),
                new es.ull.project.domain.valueobject.demand.QuantityUnit(wdDoc.getQuantityUnit()),
                java.util.concurrent.TimeUnit.valueOf(wdDoc.getTimeUnit()));
            
            // Validate and convert Distance
            DistanceDocument distDoc = d.getDistance();
            if (distDoc == null) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "distance", null,
                    "Distance cannot be null");
            }
            es.ull.project.domain.valueobject.location.Distance dist = 
                es.ull.project.domain.valueobject.location.Distance.fromMeters(distDoc.getMeters());
            
            // Validate and convert ServiceTime
            ServiceTimeDocument stDoc = d.getServiceTime();
            if (stDoc == null) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "serviceTime", null,
                    "ServiceTime cannot be null");
            }
            es.ull.project.domain.valueobject.location.ServiceTime st = 
                new es.ull.project.domain.valueobject.location.ServiceTime(stDoc.getMinutes());
            
            // Validate and convert TransportCost
            TransportationVariableCostDocument tcDoc = d.getTransportCost();
            if (tcDoc == null) {
                throw new DataCorruptionException("ServiceAssignmentDocument", "transportCost", null,
                    "TransportCost cannot be null");
            }
            TransportationVariableCost tc = new TransportationVariableCost(tcDoc.getAmount(), tcDoc.getCurrency());
            
            return new ServiceAssignment(id, container, facility, wd, dist, st, tc);
            
        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in ServiceAssignmentDocument: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a list of ServiceAssignmentDocuments to domain entities using batch loading.
     * This method solves the N+1 problem by fetching all referenced Containers and Facilities
     * in just 2 database queries instead of 2*N queries.
     *
     * @param documents list of ServiceAssignmentDocuments to convert
     * @return list of ServiceAssignment domain entities
     */
    private List<ServiceAssignment> toDomainBatch(List<ServiceAssignmentDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        try {
            // Extract all unique container and facility IDs
            List<UUID> containerIds = documents.stream()
                    .map(ServiceAssignmentDocument::getContainerId)
                    .filter(id -> id != null && !id.isBlank())
                    .map(UUID::fromString)
                    .distinct()
                    .collect(Collectors.toList());

            List<UUID> facilityIds = documents.stream()
                    .map(ServiceAssignmentDocument::getFacilityId)
                    .filter(id -> id != null && !id.isBlank())
                    .map(UUID::fromString)
                    .distinct()
                    .collect(Collectors.toList());

            // Batch load all containers and facilities (only 2 queries total)
            Map<UUID, Container> containerMap = containerRepository.findAllById(containerIds).stream()
                    .collect(Collectors.toMap(Container::getId, c -> c));

            Map<UUID, Facility> facilityMap = facilityRepository.findAllById(facilityIds).stream()
                    .collect(Collectors.toMap(Facility::getId, f -> f));

            // Convert documents using pre-loaded maps
            List<ServiceAssignment> result = new ArrayList<>();
            for (ServiceAssignmentDocument d : documents) {
                if (d == null) {
                    continue;
                }

                // Validate document ID
                if (d.getId() == null || d.getId().isBlank()) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "id", d.getId(),
                        "ID cannot be null or empty");
                }
                UUID id = UUID.fromString(d.getId());

                // Validate and fetch container reference from map
                if (d.getContainerId() == null || d.getContainerId().isBlank()) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "containerId", d.getContainerId(),
                        "Container ID cannot be null or empty");
                }
                UUID containerId = UUID.fromString(d.getContainerId());
                Container container = containerMap.get(containerId);
                if (container == null) {
                    throw new EntityNotFoundException("Container", containerId,
                        "Referenced by ServiceAssignment " + id);
                }

                // Validate and fetch facility reference from map
                if (d.getFacilityId() == null || d.getFacilityId().isBlank()) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "facilityId", d.getFacilityId(),
                        "Facility ID cannot be null or empty");
                }
                UUID facilityId = UUID.fromString(d.getFacilityId());
                Facility facility = facilityMap.get(facilityId);
                if (facility == null) {
                    throw new EntityNotFoundException("Facility", facilityId,
                        "Referenced by ServiceAssignment " + id);
                }

                // Validate and convert WasteDemand
                WasteDemandDocument wdDoc = d.getWasteDemand();
                if (wdDoc == null) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "wasteDemand", null,
                        "WasteDemand cannot be null");
                }
                WasteDemand wd = new WasteDemand(
                    wdDoc.getValue(),
                    new es.ull.project.domain.valueobject.demand.QuantityUnit(wdDoc.getQuantityUnit()),
                    java.util.concurrent.TimeUnit.valueOf(wdDoc.getTimeUnit()));

                // Validate and convert Distance
                DistanceDocument distDoc = d.getDistance();
                if (distDoc == null) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "distance", null,
                        "Distance cannot be null");
                }
                es.ull.project.domain.valueobject.location.Distance dist =
                    es.ull.project.domain.valueobject.location.Distance.fromMeters(distDoc.getMeters());

                // Validate and convert ServiceTime
                ServiceTimeDocument stDoc = d.getServiceTime();
                if (stDoc == null) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "serviceTime", null,
                        "ServiceTime cannot be null");
                }
                es.ull.project.domain.valueobject.location.ServiceTime st =
                    new es.ull.project.domain.valueobject.location.ServiceTime(stDoc.getMinutes());

                // Validate and convert TransportCost
                TransportationVariableCostDocument tcDoc = d.getTransportCost();
                if (tcDoc == null) {
                    throw new DataCorruptionException("ServiceAssignmentDocument", "transportCost", null,
                        "TransportCost cannot be null");
                }
                TransportationVariableCost tc = new TransportationVariableCost(tcDoc.getAmount(), tcDoc.getCurrency());

                result.add(new ServiceAssignment(id, container, facility, wd, dist, st, tc));
            }

            return result;

        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in batch ServiceAssignment conversion: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceAssignment> findByContainerId(UUID containerId) {
        if (containerId == null) {
            return List.of();
        }
        try {
            List<ServiceAssignmentDocument> documents = springRepository.findByContainerId(containerId.toString());
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find ServiceAssignments by container ID: " + containerId, e);
        }
    }

    @Override
    public List<ServiceAssignment> findByFacilityId(UUID facilityId) {
        if (facilityId == null) {
            return List.of();
        }
        try {
            List<ServiceAssignmentDocument> documents = springRepository.findByFacilityId(facilityId.toString());
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find ServiceAssignments by facility ID: " + facilityId, e);
        }
    }

    @Override
    public List<ServiceAssignment> findAllById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        try {
            List<String> stringIds = ids.stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            List<ServiceAssignmentDocument> documents = this.springRepository.findAllById(stringIds);
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find ServiceAssignments by IDs", e);
        }
    }
}
