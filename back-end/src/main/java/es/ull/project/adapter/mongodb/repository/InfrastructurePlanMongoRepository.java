package es.ull.project.adapter.mongodb.repository;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
     * @return list of infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> findAll() {
        return this.mongoTemplate.findAll(InfrastructurePlan.class, COLLECTION_NAME);
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
        Query query = new Query(Criteria.where("id").is(id));
        InfrastructurePlan infrastructurePlan = this.mongoTemplate.findOne(query, InfrastructurePlan.class, COLLECTION_NAME);
        return Optional.ofNullable(infrastructurePlan);
    }
}
