package es.ull.project.adapter.mongodb.repository;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.adapter.mongodb.mapper.InfrastructurePlanListDocumentMapper;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoDB implementation of the InfrastructurePlanRepository interface.
 *
 * This class provides concrete implementations for CRUD operations
 * on InfrastructurePlan entities using MongoDB as the data store.
 * Active when "memory" profile is NOT enabled (default).
 */
@Repository
@Profile("!memory")
public class InfrastructurePlanMongoRepository implements InfrastructurePlanRepository {

    public static final String COLLECTION_NAME = "infrastructureplans";
    private static final String SERVICE_ASSIGNMENTS_COLLECTION = "serviceassignments";
    private static final String DAILY_PLANS_COLLECTION = "daily_plans";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Delete an infrastructure plan.
     *
     * @param entity plan to delete
     */
    @Override
    public void delete(InfrastructurePlan entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Fetch all infrastructure plans.
     *
     * @return list of infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> fetchAll() {
        return this.mongoTemplate.findAll(InfrastructurePlan.class, COLLECTION_NAME);
    }

    /**
     * Find all infrastructure plans (alias for compatibility).
     *
     * @return list of all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> findAll() {
        return this.mongoTemplate.findAll(InfrastructurePlan.class, COLLECTION_NAME);
    }

    /**
     * Find all infrastructure plans using pagination and sorting.
     *
     * @param pageable pagination and sort information
     * @return page of infrastructure plans
     */
    @Override
    public Page<InfrastructurePlan> findAll(Pageable pageable) {
        Query dataQuery = listSummaryProjectionQuery().with(pageable);
        List<InfrastructurePlan> infrastructurePlans = this.mongoTemplate.find(dataQuery, Document.class, COLLECTION_NAME).stream()
                .map(InfrastructurePlanListDocumentMapper::toInfrastructurePlan)
                .toList();
        long total = this.mongoTemplate.count(new Query(), COLLECTION_NAME);
        return new PageImpl<>(infrastructurePlans, pageable, total);
    }

    /**
     * MongoDB query that projects only fields required for paginated list responses.
     *
     * @return query with list-summary field projection
     */
    private static Query listSummaryProjectionQuery() {
        Query query = new Query();
        query.fields()
                .include(MongoFields.ID)
                .include(MongoFields.ESTIMATED_TOTAL_COST)
                .include(MongoFields.NUMBER_OF_DAYS)
                .include(MongoFields.AVERAGE_PICKUP_TIME_MINUTES)
                .include(MongoFields.EXECUTED_AT)
                .include(MongoFields.VALIDITY_STATE)
                .include(MongoFields.EXECUTION_STATE)
                .include(MongoFields.FAILURE_REASON);
        return query;
    }

    /**
     * Save or update an infrastructure plan.
     *
     * @param entity plan to persist
     * @return persisted InfrastructurePlan
     */
    @Override
    public InfrastructurePlan save(InfrastructurePlan entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Find an infrastructure plan by UUID.
     *
     * @param id plan UUID
     * @return optional with the plan if present
     */
    @Override
    public Optional<InfrastructurePlan> findById(UUID id) {
        Query query = new Query(Criteria.where(MongoFields.ID).is(id));
        InfrastructurePlan infrastructurePlan = this.mongoTemplate.findOne(query, InfrastructurePlan.class, COLLECTION_NAME);
        return Optional.ofNullable(infrastructurePlan);
    }

    /**
     * Finds valid infrastructure plans that reference the given entity in their execution request.
     *
     * @param entityId facility, vehicle, or container id
     * @return valid plans linked to the entity
     */
    @Override
    public List<InfrastructurePlan> findValidPlansReferencingEntityInExecutionRequest(UUID entityId) {
        if (entityId == null) {
            return List.of();
        }
        return findPlansReferencingEntityInExecutionRequest(entityId).stream()
                .filter(p -> p.getValidityState() == InfrastructurePlanValidityState.VALID)
                .toList();
    }

    /**
     * Checks whether any infrastructure plan references the given entity in its execution request.
     *
     * @param entityId facility, vehicle, or container id
     * @return true when at least one plan references the entity
     */
    @Override
    public boolean existsAnyPlanReferencingEntityInExecutionRequest(UUID entityId) {
        if (entityId == null) {
            return false;
        }
        return !collectInfrastructurePlanIdsReferencingEntity(entityId).isEmpty();
    }

    /**
     * Finds infrastructure plans that reference the given entity in their execution request.
     *
     * @param entityId facility, vehicle, or container id
     * @return plans linked to the entity
     */
    @Override
    public List<InfrastructurePlan> findPlansReferencingEntityInExecutionRequest(UUID entityId) {
        if (entityId == null) {
            return List.of();
        }
        Set<UUID> ids = collectInfrastructurePlanIdsReferencingEntity(entityId);
        if (ids.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where(MongoFields.ID).in(ids));
        return this.mongoTemplate.find(query, InfrastructurePlan.class, COLLECTION_NAME);
    }

    /**
     * Resolves infrastructure plan ids that reference a facility, container, or vehicle anywhere
     * in the persisted snapshot (execution JSON, selected facilities, service assignments, daily plans).
     *
     * @param entityId facility, vehicle, or container id
     * @return referenced infrastructure plan ids
     */
    private Set<UUID> collectInfrastructurePlanIdsReferencingEntity(UUID entityId) {
        Set<UUID> planIds = new LinkedHashSet<>();
        String quotedToken = "\"" + entityId + "\"";
        Pattern jsonPattern = Pattern.compile(Pattern.quote(quotedToken), Pattern.CASE_INSENSITIVE);
        Criteria jsonMatch = Criteria.where(MongoFields.EXECUTION_REQUEST_JSON).regex(jsonPattern);
        Criteria selectedFacilitiesMatch = Criteria.where(MongoFields.SELECTED_FACILITIES).is(entityId);
        Query infraQuery = new Query(new Criteria().orOperator(jsonMatch, selectedFacilitiesMatch));
        infraQuery.fields().include(MongoFields.ID);
        for (Document doc : this.mongoTemplate.find(infraQuery, Document.class, COLLECTION_NAME)) {
            addIdIfPresent(doc.get(MongoFields.ID), planIds);
        }
        Query saByFacility = new Query(Criteria.where(MongoFields.FACILITY_ID).is(entityId));
        saByFacility.fields().include(MongoFields.INFRASTRUCTURE_PLAN_ID);
        for (Document doc : this.mongoTemplate.find(saByFacility, Document.class, SERVICE_ASSIGNMENTS_COLLECTION)) {
            addIdIfPresent(doc.get(MongoFields.INFRASTRUCTURE_PLAN_ID), planIds);
        }
        Query saByContainer = new Query(Criteria.where(MongoFields.ASSIGNED_CONTAINERS).is(entityId));
        saByContainer.fields().include(MongoFields.INFRASTRUCTURE_PLAN_ID);
        for (Document doc : this.mongoTemplate.find(saByContainer, Document.class, SERVICE_ASSIGNMENTS_COLLECTION)) {
            addIdIfPresent(doc.get(MongoFields.INFRASTRUCTURE_PLAN_ID), planIds);
        }
        Criteria dailyVehicleOrFacility = new Criteria().orOperator(
                Criteria.where(MongoFields.VEHICLE).is(entityId),
                Criteria.where(MongoFields.FACILITY_ID).is(entityId));
        Criteria dailyStopContainer = Criteria.where(MongoFields.STOPS)
                .elemMatch(Criteria.where(MongoFields.CONTAINER_ID).is(entityId));
        Query dailyQuery = new Query(new Criteria().orOperator(dailyVehicleOrFacility, dailyStopContainer));
        dailyQuery.fields().include(MongoFields.INFRASTRUCTURE_PLAN_ID);
        for (Document doc : this.mongoTemplate.find(dailyQuery, Document.class, DAILY_PLANS_COLLECTION)) {
            addIdIfPresent(doc.get(MongoFields.INFRASTRUCTURE_PLAN_ID), planIds);
        }
        return planIds;
    }

    /**
     * Adds the raw plan id to the result set when MongoDB returns it as a UUID.
     *
     * @param rawPlanId raw id value read from MongoDB
     * @param planIds plan ids collected so far
     */
    private static void addIdIfPresent(Object rawPlanId, Set<UUID> planIds) {
        if (rawPlanId instanceof UUID uuid) {
            planIds.add(uuid);
        }
    }
}
