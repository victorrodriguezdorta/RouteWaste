package es.ull.project.adapter.mongodb.repository;

import es.ull.project.adapter.mongodb.support.MongoEnumTypeCounts;
import es.ull.project.application.query.ContainerSearchCriteria;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
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
    private static final String FIELD_SERVICE_ZONE = "serviceZone";
    private static final String FIELD_LOCATION_POSTAL_ADDRESS = "location.postalAddress";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CAPACITY_LITERS_VALUE = "capacityLiters.liters";
    private static final String FIELD_DAILY_DEMAND_VALUE = "dailyDemandLitersPerDay.litersPerDay";
    private static final String REGEX_CASE_INSENSITIVE = "i";

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
    public Page<Container> findAll(@NonNull Pageable pageable) {
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
    public Page<Container> findAll(@NonNull Pageable pageable, WasteType wasteType) {
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

    /**
     * Find containers with advanced search criteria and pagination.
     * Supports filtering by multiple attributes dynamically.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return page of matching containers
     */
    public Page<Container> findAll(@NonNull Pageable pageable, @NonNull ContainerSearchCriteria criteria) {
        Query dataQuery = new Query();
        Query countQuery = new Query();
        List<Criteria> criterias = buildSearchCriterias(criteria);
        if (!criterias.isEmpty()) {
            Criteria combinedCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[0]));
            dataQuery.addCriteria(combinedCriteria);
            countQuery.addCriteria(combinedCriteria);
        }
        dataQuery.with(pageable);
        List<Container> containers = this.mongoTemplate.find(dataQuery, Container.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(countQuery, Container.class, COLLECTION_NAME);
        return new PageImpl<>(containers, pageable, total);
    }

    /**
     * Builds a list of MongoDB Criteria from search criteria.
     * Each non-null filter is converted to a corresponding Criteria object.
     *
     * @param criteria search criteria
     * @return list of Criteria objects
     */
    private List<Criteria> buildSearchCriterias(@NonNull ContainerSearchCriteria criteria) {
        List<Criteria> criterias = new ArrayList<>();
        if (criteria.getWasteType() != null) {
            criterias.add(Criteria.where(FIELD_WASTE_TYPE).is(criteria.getWasteType()));
        }
        if (criteria.getServiceZone() != null) {
            criterias.add(Criteria.where(FIELD_SERVICE_ZONE).is(criteria.getServiceZone()));
        }
        if (criteria.getLocationPostalAddress() != null) {
            criterias.add(Criteria.where(FIELD_LOCATION_POSTAL_ADDRESS)
                    .regex(criteria.getLocationPostalAddress(), REGEX_CASE_INSENSITIVE));
        }
        if (criteria.getName() != null) {
            criterias.add(Criteria.where(FIELD_NAME)
                    .regex(criteria.getName(), REGEX_CASE_INSENSITIVE));
        }
        if (criteria.getMinCapacityLiters() != null) {
            criterias.add(Criteria.where(FIELD_CAPACITY_LITERS_VALUE).gte(criteria.getMinCapacityLiters()));
        }
        if (criteria.getMaxCapacityLiters() != null) {
            criterias.add(Criteria.where(FIELD_CAPACITY_LITERS_VALUE).lte(criteria.getMaxCapacityLiters()));
        }
        if (criteria.getMinDailyDemand() != null) {
            criterias.add(Criteria.where(FIELD_DAILY_DEMAND_VALUE)
                    .gte(criteria.getMinDailyDemand()));
        }
        if (criteria.getMaxDailyDemand() != null) {
            criterias.add(Criteria.where(FIELD_DAILY_DEMAND_VALUE)
                    .lte(criteria.getMaxDailyDemand()));
        }
        return criterias;
    }

    /**
     * Counts all containers in the collection.
     *
     * @return total container count
     */
    @Override
    public long count() {
        return MongoEnumTypeCounts.countAll(this.mongoTemplate, Container.class, COLLECTION_NAME);
    }

    /**
     * Counts containers grouped by {@link WasteType}.
     *
     * @return map with every waste type and its count
     */
    @Override
    public Map<WasteType, Long> countByWasteType() {
        return MongoEnumTypeCounts.countByEnumField(
                this.mongoTemplate,
                Container.class,
                COLLECTION_NAME,
                FIELD_WASTE_TYPE,
                WasteType.class);
    }
}
