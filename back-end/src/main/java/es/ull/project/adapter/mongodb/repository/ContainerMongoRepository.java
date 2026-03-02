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

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.domain.entity.Container;

/**
 * MongoDB implementation of the ContainerRepository interface.
 *
 * This class provides concrete implementations for CRUD operations
 * on Container entities using MongoDB as the data store.
 * Active when "memory" profile is NOT enabled (default).
 */
@Repository
@Profile("!memory")
public class ContainerMongoRepository implements ContainerRepository {

    public static final String COLLECTION_NAME = "containers";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Delete a container from the repository.
     *
     * @param entity Container to remove
     */
    @Override
    public void delete(Container entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Fetch all containers using the application's preferred naming.
     *
     * @return list of all containers
     */
    @Override
    public List<Container> fetchAll() {
        return this.mongoTemplate.findAll(Container.class, COLLECTION_NAME);
    }

    /**
     * Find all containers (alias commonly used by services).
     *
     * @return list of all containers
     */
    @Override
    public List<Container> findAll() {
        return this.mongoTemplate.findAll(Container.class, COLLECTION_NAME);
    }

    /**
     * Save or update a container.
     *
     * @param entity Container to persist
     * @return persisted Container instance
     */
    @Override
    public Container save(Container entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Find a container by its identifier.
     *
     * @param id container UUID
     * @return optional containing the container when found
     */
    @Override
    public Optional<Container> findById(UUID id) {
        Query query = new Query(Criteria.where("id").is(id));
        Container container = this.mongoTemplate.findOne(query, Container.class, COLLECTION_NAME);
        return Optional.ofNullable(container);
    }
}
