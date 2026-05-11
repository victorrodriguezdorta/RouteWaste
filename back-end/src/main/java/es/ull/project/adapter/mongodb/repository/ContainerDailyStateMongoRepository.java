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

import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.domain.entity.ContainerDailyState;

@Repository
@Profile("!memory")
public class ContainerDailyStateMongoRepository implements ContainerDailyStateRepository {

    public static final String COLLECTION_NAME = "containerDailyStates";
    private static final String FIELD_ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ContainerDailyState save(ContainerDailyState entity) {
        if (entity == null) return null;
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    @Override
    public Optional<ContainerDailyState> findById(UUID id) {
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        ContainerDailyState cds = this.mongoTemplate.findOne(query, ContainerDailyState.class, COLLECTION_NAME);
        return Optional.ofNullable(cds);
    }

    @Override
    public List<ContainerDailyState> findAll() {
        return this.mongoTemplate.findAll(ContainerDailyState.class, COLLECTION_NAME);
    }

    @Override
    public void delete(ContainerDailyState entity) {
        if (entity == null) return;
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }
}
