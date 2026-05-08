package es.ull.project.adapter.mongodb.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.domain.entity.DailyPlan;

/**
 * MongoDB implementation of the DailyPlanRepository interface.
 *
 * This class provides concrete implementations for database operations
 * on DailyPlan entities using MongoDB as the data store.
 */
@Repository
@Profile("!memory")
public class DailyPlanMongoRepository implements DailyPlanRepository {

    public static final String COLLECTION_NAME = "daily_plans";
    private static final String FIELD_ID = "id";
    private static final String FIELD_INFRASTRUCTURE_PLAN_ID = "infrastructurePlanId";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Saves or updates a DailyPlan entity in MongoDB.
     *
     * @param entity the DailyPlan to persist
     * @return the persisted DailyPlan, or null if entity is null
     */
    @Override
    public DailyPlan save(DailyPlan entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Deletes a DailyPlan entity from MongoDB.
     *
     * @param entity the DailyPlan to delete; ignored if null
     */
    @Override
    public void delete(DailyPlan entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Finds a DailyPlan by its unique identifier.
     *
     * @param id the UUID of the plan to find
     * @return an Optional containing the plan if found, or empty
     */
    @Override
    public Optional<DailyPlan> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        DailyPlan plan = this.mongoTemplate.findOne(query, DailyPlan.class, COLLECTION_NAME);
        return Optional.ofNullable(plan);
    }

    /**
     * Finds all DailyPlan entities associated with a given InfrastructurePlan.
     *
     * @param infrastructurePlanId the UUID of the infrastructure plan
     * @return list of matching DailyPlan entities, or an empty list if id is null
     */
    @Override
    public List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        if (infrastructurePlanId == null) {
            return List.of();
        }
        Query query = new Query(Criteria.where(FIELD_INFRASTRUCTURE_PLAN_ID).is(infrastructurePlanId));
        return this.mongoTemplate.find(query, DailyPlan.class, COLLECTION_NAME);
    }
}
