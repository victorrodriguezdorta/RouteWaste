package es.ull.project.adapter.mongodb.repository;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.domain.entity.ContainerDailyState;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!memory")
public class ContainerDailyStateMongoRepository implements ContainerDailyStateRepository {

    public static final String COLLECTION_NAME = "containerDailyStates";
    private static final String FIELD_ID = "id";
    private static final String FIELD_INFRASTRUCTURE_PLAN_ID = MongoFields.INFRASTRUCTURE_PLAN_ID;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Persists a container daily state snapshot.
     *
     * @param entity container daily state to persist
     * @return persisted entity, or null when the input is null
     */
    @Override
    public ContainerDailyState save(ContainerDailyState entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Finds a container daily state by identifier.
     *
     * @param id container daily state id
     * @return matching entity, or empty when not found
     */
    @Override
    public Optional<ContainerDailyState> findById(UUID id) {
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        ContainerDailyState cds = this.mongoTemplate.findOne(query, ContainerDailyState.class, COLLECTION_NAME);
        return Optional.ofNullable(cds);
    }

    /**
     * Finds all persisted container daily states.
     *
     * @return all container daily states
     */
    @Override
    public List<ContainerDailyState> findAll() {
        return this.mongoTemplate.findAll(ContainerDailyState.class, COLLECTION_NAME);
    }

    /**
     * Finds container daily states linked to an infrastructure plan.
     *
     * @param infrastructurePlanId parent infrastructure plan id
     * @return matching container daily states
     */
    @Override
    public List<ContainerDailyState> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        if (infrastructurePlanId == null) {
            return List.of();
        }
        Query query = new Query(Criteria.where(FIELD_INFRASTRUCTURE_PLAN_ID).is(infrastructurePlanId));
        return this.mongoTemplate.find(query, ContainerDailyState.class, COLLECTION_NAME);
    }

    /**
     * Deletes a container daily state snapshot.
     *
     * @param entity container daily state to delete
     */
    @Override
    public void delete(ContainerDailyState entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }
}
