package es.ull.project.adapter.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import es.ull.project.adapter.mongo.document.entity.InfrastructurePlanDocument;
import es.ull.project.adapter.mongo.document.valueobject.MaximumBudgetDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServicePoliciesDocument;
import es.ull.project.adapter.mongo.document.valueobject.TotalCostDocument;
import es.ull.project.adapter.mongo.exception.DataCorruptionException;
import es.ull.project.adapter.mongo.exception.EntityNotFoundException;
import es.ull.project.adapter.mongo.exception.PersistenceException;
import es.ull.project.adapter.mongo.spring.InfrastructurePlanSpringRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

public class MongoDbInfrastructurePlanRepository implements InfrastructurePlanRepository {

    private final InfrastructurePlanSpringRepository springRepo;
    private final FacilityRepository facilityRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;

    public MongoDbInfrastructurePlanRepository(InfrastructurePlanSpringRepository springRepo,
            FacilityRepository facilityRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        this.springRepo = springRepo;
        this.facilityRepository = facilityRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    @Override
    public void delete(InfrastructurePlan entity) {
        if (entity == null) {
            return;
        }
        try {
            this.springRepo.deleteById(entity.getId().toString());
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to delete InfrastructurePlan with ID: " + entity.getId(), e);
        }
    }

    @Override
    public List<InfrastructurePlan> fetchAll() {
        try {
            List<InfrastructurePlanDocument> documents = this.springRepo.findAll();
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to fetch all InfrastructurePlans", e);
        }
    }

    @Override
    public List<InfrastructurePlan> findAll() {
        try {
            List<InfrastructurePlanDocument> documents = this.springRepo.findAll();
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException("Failed to find all InfrastructurePlans", e);
        }
    }

    @Override
    public InfrastructurePlan save(InfrastructurePlan entity) {
        if (entity == null) {
            return null;
        }
        try {
            InfrastructurePlanDocument doc = toDocument(entity);
            InfrastructurePlanDocument saved = this.springRepo.save(doc);
            return toDomain(saved);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to save InfrastructurePlan with ID: " + entity.getId(), e);
        }
    }

    @Override
    public Optional<InfrastructurePlan> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return this.springRepo.findById(id.toString()).map(this::toDomain);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find InfrastructurePlan with ID: " + id, e);
        }
    }

    private InfrastructurePlanDocument toDocument(InfrastructurePlan plan) {
        String id = plan.getId().toString();
        String period = plan.getPeriod().getValue();
        List<String> facilityIds = plan.getSelectedFacilities().stream().map(f -> f.getId().toString()).collect(Collectors.toList());
        List<String> assignmentIds = plan.getServiceAssignments().stream().map(a -> a.getId().toString()).collect(Collectors.toList());
        ServicePolicies policies = plan.getServicePolicies();
        ServicePoliciesDocument policiesDoc = policies != null ? new ServicePoliciesDocument(policies.getMaxServiceDistance().orElse(null), policies.getMaxServiceTime().orElse(null), policies.getMaxInfrastructureCount().orElse(null), policies.getMaxEmissions().orElse(null)) : null;
        MaximumBudget max = plan.getMaxBudget();
        MaximumBudgetDocument maxDoc = max != null ? new MaximumBudgetDocument(max.getAmount(), max.getCurrency().map(c -> c.getCode()).orElse("EUR")) : null;
        TotalCost total = plan.getEstimatedTotalCost();
        TotalCostDocument totalDoc = total != null ? new TotalCostDocument(total.getAmount(), total.getCurrency().map(c -> c.getCode()).orElse("EUR")) : null;
        return new InfrastructurePlanDocument(id, period, facilityIds, assignmentIds, policiesDoc, maxDoc, totalDoc);
    }

    private InfrastructurePlan toDomain(InfrastructurePlanDocument d) {
        if (d == null) {
            return null;
        }
        
        try {
            // Validate document ID
            if (d.getId() == null || d.getId().isBlank()) {
                throw new DataCorruptionException("InfrastructurePlanDocument", "id", d.getId(),
                    "ID cannot be null or empty");
            }
            UUID id = UUID.fromString(d.getId());
            
            // Validate and convert PlanningPeriod
            if (d.getPlanningPeriod() == null || d.getPlanningPeriod().isBlank()) {
                throw new DataCorruptionException("InfrastructurePlanDocument", "planningPeriod", 
                    d.getPlanningPeriod(), "Planning period cannot be null or empty");
            }
            PlanningPeriod period = new PlanningPeriod(d.getPlanningPeriod());
            
            // Fetch and validate facilities
            List<Facility> facilities = new ArrayList<>();
            if (d.getSelectedFacilityIds() != null) {
                for (String fid : d.getSelectedFacilityIds()) {
                    if (fid == null || fid.isBlank()) {
                        throw new DataCorruptionException("InfrastructurePlanDocument", 
                            "selectedFacilityIds", fid, "Facility ID in list cannot be null or empty");
                    }
                    UUID facilityId = UUID.fromString(fid);
                    Facility facility = this.facilityRepository.findById(facilityId)
                            .orElseThrow(() -> new EntityNotFoundException("Facility", facilityId,
                                "Referenced by InfrastructurePlan " + id));
                    facilities.add(facility);
                }
            }
            
            // Fetch and validate service assignments
            List<ServiceAssignment> assignments = new ArrayList<>();
            if (d.getServiceAssignmentIds() != null) {
                for (String aid : d.getServiceAssignmentIds()) {
                    if (aid == null || aid.isBlank()) {
                        throw new DataCorruptionException("InfrastructurePlanDocument",
                            "serviceAssignmentIds", aid, "ServiceAssignment ID in list cannot be null or empty");
                    }
                    UUID assignmentId = UUID.fromString(aid);
                    ServiceAssignment assignment = this.serviceAssignmentRepository.findById(assignmentId)
                            .orElseThrow(() -> new EntityNotFoundException("ServiceAssignment", assignmentId,
                                "Referenced by InfrastructurePlan " + id));
                    assignments.add(assignment);
                }
            }
            
            // Convert optional ServicePolicies
            ServicePoliciesDocument spd = d.getServicePolicies();
            ServicePolicies sp = spd != null ? 
                new ServicePolicies(spd.getMaxServiceDistance(), spd.getMaxServiceTime(), 
                    spd.getMaxInfrastructureCount(), spd.getMaxEmissions()) : null;
            
            // Convert optional MaximumBudget
            MaximumBudgetDocument mbd = d.getMaxBudget();
            MaximumBudget mb = mbd != null ? 
                new MaximumBudget(mbd.getAmount(), mbd.getCurrency()) : null;
            
            // Convert optional TotalCost
            TotalCostDocument tcd = d.getEstimatedTotalCost();
            TotalCost tc = tcd != null ? 
                new TotalCost(tcd.getAmount(), tcd.getCurrency()) : null;
            
            return new InfrastructurePlan(id, period, facilities, assignments, sp, mb, tc);
            
        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in InfrastructurePlanDocument: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a list of InfrastructurePlanDocuments to domain entities using batch loading.
     * This method solves the N+1 problem by fetching all referenced Facilities and ServiceAssignments
     * in just 2 database queries instead of 2*N queries.
     *
     * @param documents list of InfrastructurePlanDocuments to convert
     * @return list of InfrastructurePlan domain entities
     */
    private List<InfrastructurePlan> toDomainBatch(List<InfrastructurePlanDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        try {
            // Extract all unique facility and service assignment IDs
            List<UUID> facilityIds = new ArrayList<>();
            List<UUID> assignmentIds = new ArrayList<>();

            for (InfrastructurePlanDocument d : documents) {
                if (d.getSelectedFacilityIds() != null) {
                    for (String fid : d.getSelectedFacilityIds()) {
                        if (fid != null && !fid.isBlank()) {
                            facilityIds.add(UUID.fromString(fid));
                        }
                    }
                }
                if (d.getServiceAssignmentIds() != null) {
                    for (String aid : d.getServiceAssignmentIds()) {
                        if (aid != null && !aid.isBlank()) {
                            assignmentIds.add(UUID.fromString(aid));
                        }
                    }
                }
            }

            // Remove duplicates
            facilityIds = facilityIds.stream().distinct().collect(Collectors.toList());
            assignmentIds = assignmentIds.stream().distinct().collect(Collectors.toList());

            // Batch load all facilities and service assignments (only 2 queries total)
            Map<UUID, Facility> facilityMap = facilityRepository.findAllById(facilityIds).stream()
                    .collect(Collectors.toMap(Facility::getId, f -> f));

            Map<UUID, ServiceAssignment> assignmentMap = serviceAssignmentRepository.findAllById(assignmentIds).stream()
                    .collect(Collectors.toMap(ServiceAssignment::getId, sa -> sa));

            // Convert documents using pre-loaded maps
            List<InfrastructurePlan> result = new ArrayList<>();
            for (InfrastructurePlanDocument d : documents) {
                if (d == null) {
                    continue;
                }

                // Validate document ID
                if (d.getId() == null || d.getId().isBlank()) {
                    throw new DataCorruptionException("InfrastructurePlanDocument", "id", d.getId(),
                        "ID cannot be null or empty");
                }
                UUID id = UUID.fromString(d.getId());

                // Validate and convert PlanningPeriod
                if (d.getPlanningPeriod() == null || d.getPlanningPeriod().isBlank()) {
                    throw new DataCorruptionException("InfrastructurePlanDocument", "planningPeriod",
                        d.getPlanningPeriod(), "Planning period cannot be null or empty");
                }
                PlanningPeriod period = new PlanningPeriod(d.getPlanningPeriod());

                // Fetch facilities from pre-loaded map
                List<Facility> facilities = new ArrayList<>();
                if (d.getSelectedFacilityIds() != null) {
                    for (String fid : d.getSelectedFacilityIds()) {
                        if (fid == null || fid.isBlank()) {
                            throw new DataCorruptionException("InfrastructurePlanDocument",
                                "selectedFacilityIds", fid, "Facility ID in list cannot be null or empty");
                        }
                        UUID facilityId = UUID.fromString(fid);
                        Facility facility = facilityMap.get(facilityId);
                        if (facility == null) {
                            throw new EntityNotFoundException("Facility", facilityId,
                                "Referenced by InfrastructurePlan " + id);
                        }
                        facilities.add(facility);
                    }
                }

                // Fetch service assignments from pre-loaded map
                List<ServiceAssignment> assignments = new ArrayList<>();
                if (d.getServiceAssignmentIds() != null) {
                    for (String aid : d.getServiceAssignmentIds()) {
                        if (aid == null || aid.isBlank()) {
                            throw new DataCorruptionException("InfrastructurePlanDocument",
                                "serviceAssignmentIds", aid, "ServiceAssignment ID in list cannot be null or empty");
                        }
                        UUID assignmentId = UUID.fromString(aid);
                        ServiceAssignment assignment = assignmentMap.get(assignmentId);
                        if (assignment == null) {
                            throw new EntityNotFoundException("ServiceAssignment", assignmentId,
                                "Referenced by InfrastructurePlan " + id);
                        }
                        assignments.add(assignment);
                    }
                }

                // Convert optional ServicePolicies
                ServicePoliciesDocument spd = d.getServicePolicies();
                ServicePolicies sp = spd != null ?
                    new ServicePolicies(spd.getMaxServiceDistance(), spd.getMaxServiceTime(),
                        spd.getMaxInfrastructureCount(), spd.getMaxEmissions()) : null;

                // Convert optional MaximumBudget
                MaximumBudgetDocument mbd = d.getMaxBudget();
                MaximumBudget mb = mbd != null ?
                    new MaximumBudget(mbd.getAmount(), mbd.getCurrency()) : null;

                // Convert optional TotalCost
                TotalCostDocument tcd = d.getEstimatedTotalCost();
                TotalCost tc = tcd != null ?
                    new TotalCost(tcd.getAmount(), tcd.getCurrency()) : null;

                result.add(new InfrastructurePlan(id, period, facilities, assignments, sp, mb, tc));
            }

            return result;

        } catch (IllegalArgumentException e) {
            throw new DataCorruptionException(
                "Invalid data format in batch InfrastructurePlan conversion: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InfrastructurePlan> findByFacilityId(UUID facilityId) {
        if (facilityId == null) {
            return List.of();
        }
        try {
            List<InfrastructurePlanDocument> documents = springRepo.findBySelectedFacilityIdsContaining(facilityId.toString());
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find InfrastructurePlans by facility ID: " + facilityId, e);
        }
    }

    @Override
    public List<InfrastructurePlan> findByServiceAssignmentId(UUID serviceAssignmentId) {
        if (serviceAssignmentId == null) {
            return List.of();
        }
        try {
            List<InfrastructurePlanDocument> documents = springRepo.findByServiceAssignmentIdsContaining(serviceAssignmentId.toString());
            return toDomainBatch(documents);
        } catch (DataAccessException e) {
            throw new PersistenceException(
                "Failed to find InfrastructurePlans by service assignment ID: " + serviceAssignmentId, e);
        }
    }
}
