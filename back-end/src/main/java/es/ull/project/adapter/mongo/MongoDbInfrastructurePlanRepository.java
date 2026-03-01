package es.ull.project.adapter.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import es.ull.project.adapter.mongo.document.entity.InfrastructurePlanDocument;
import es.ull.project.adapter.mongo.document.valueobject.MaximumBudgetDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServicePoliciesDocument;
import es.ull.project.adapter.mongo.document.valueobject.TotalCostDocument;
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
        this.springRepo.deleteById(entity.getId().toString());
    }

    @Override
    public List<InfrastructurePlan> fetchAll() {
        return this.springRepo.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InfrastructurePlan> findAll() {
        return fetchAll();
    }

    @Override
    public InfrastructurePlan save(InfrastructurePlan entity) {
        if (entity == null) {
            return null;
        }
        InfrastructurePlanDocument doc = toDocument(entity);
        InfrastructurePlanDocument saved = this.springRepo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<InfrastructurePlan> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return this.springRepo.findById(id.toString()).map(this::toDomain);
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
        UUID id = UUID.fromString(d.getId());
        PlanningPeriod period = new PlanningPeriod(d.getPlanningPeriod());
        List<Facility> facilities = new ArrayList<>();
        if (d.getSelectedFacilityIds() != null) {
            for (String fid : d.getSelectedFacilityIds()) {
                this.facilityRepository.findById(UUID.fromString(fid)).ifPresent(facilities::add);
            }
        }
        List<ServiceAssignment> assignments = new ArrayList<>();
        if (d.getServiceAssignmentIds() != null) {
            for (String aid : d.getServiceAssignmentIds()) {
                this.serviceAssignmentRepository.findById(UUID.fromString(aid)).ifPresent(assignments::add);
            }
        }
        ServicePoliciesDocument spd = d.getServicePolicies();
        ServicePolicies sp = spd != null ? new ServicePolicies(spd.getMaxServiceDistance(), spd.getMaxServiceTime(), spd.getMaxInfrastructureCount(), spd.getMaxEmissions()) : null;
        MaximumBudgetDocument mbd = d.getMaxBudget();
        MaximumBudget mb = mbd != null ? new MaximumBudget(mbd.getAmount(), mbd.getCurrency()) : null;
        TotalCostDocument tcd = d.getEstimatedTotalCost();
        TotalCost tc = tcd != null ? new TotalCost(tcd.getAmount(), tcd.getCurrency()) : null;
        return new InfrastructurePlan(id, period, facilities, assignments, sp, mb, tc);
    }
}
