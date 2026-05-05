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

    @Override
    public DailyPlan save(DailyPlan entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    @Override
    public void delete(DailyPlan entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    @Override
    public Optional<DailyPlan> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        DailyPlan plan = this.mongoTemplate.findOne(query, DailyPlan.class, COLLECTION_NAME);
        return Optional.ofNullable(plan);
    }

    @Override
    public List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        if (infrastructurePlanId == null) {
            return List.of();
        }
        // In MongoDB, the embedded entity 'infrastructurePlan' is stored.
        // We can query its nested id field: 'infrastructurePlan._id'
        Query query = new Query(Criteria.where("infrastructurePlan._id").is(infrastructurePlanId));
        return this.mongoTemplate.find(query, DailyPlan.class, COLLECTION_NAME);
    }
}
