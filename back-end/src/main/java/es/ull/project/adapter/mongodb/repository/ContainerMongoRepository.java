package es.ull.project.adapter.mongodb.repository;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.domain.entity.Container;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import es.ull.project.domain.enumerate.WasteType;
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
    private static final String FIELD_ID = "id";
    private static final String FIELD_WASTE_TYPE = "wasteType";

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
     * Find all containers using pagination.
     *
     * @param pageable pagination information
     * @return page of containers
     */
    @Override
    public Page<Container> findAll(Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Container> containers = this.mongoTemplate.find(query, Container.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(new Query(), Container.class, COLLECTION_NAME);
        return new PageImpl<>(containers, pageable, total);
    }

    /**
     * Find containers with pagination and an optional waste type filter.
     *
     * @param pageable pagination and sort information
     * @param wasteType optional waste type filter
     * @return page of matching containers
     */
    @Override
    public Page<Container> findAll(Pageable pageable, WasteType wasteType) {
        Query dataQuery = new Query();
        Query countQuery = new Query();
        if (wasteType != null) {
            Criteria criteria = Criteria.where(FIELD_WASTE_TYPE).is(wasteType);
            dataQuery.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        dataQuery.with(pageable);
        List<Container> containers = this.mongoTemplate.find(dataQuery, Container.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(countQuery, Container.class, COLLECTION_NAME);
        return new PageImpl<>(containers, pageable, total);
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
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        Container container = this.mongoTemplate.findOne(query, Container.class, COLLECTION_NAME);
        return Optional.ofNullable(container);
    }
}
