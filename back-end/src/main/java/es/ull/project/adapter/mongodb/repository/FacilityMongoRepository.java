package es.ull.project.adapter.mongodb.repository;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.adapter.mongodb.query.FacilitySearchCriteriaBuilder;
import es.ull.project.adapter.mongodb.support.MongoEnumTypeCounts;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.query.FacilitySearchCriteria;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
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
 * MongoDB implementation of the FacilityRepository interface.
 *
 * This class provides concrete implementations for CRUD operations
 * on Facility entities using MongoDB as the data store.
 * Active when "memory" profile is NOT enabled (default).
 */
@Repository
@Profile("!memory")
public class FacilityMongoRepository implements FacilityRepository {

    public static final String COLLECTION_NAME = "facilities";
    private static final String FIELD_ID = "id";
    private static final String FIELD_LOCATION_POSTAL_ADDRESS = "location.postalAddress";
    private static final String FIELD_NAME = "name";
    private static final String CASE_INSENSITIVE_REGEX_FLAG = "i";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Delete a facility from the repository.
     *
     * @param entity Facility to remove
     */
    @Override
    public void delete(Facility entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Fetch all facilities (domain-preferred name).
     *
     * @return list of facilities
     */
    @Override
    public List<Facility> fetchAll() {
        return this.mongoTemplate.findAll(Facility.class, COLLECTION_NAME);
    }

    /**
     * Find all facilities (alias for service compatibility).
     *
     * @return list of facilities
     */
    @Override
    public List<Facility> findAll() {
        return this.mongoTemplate.findAll(Facility.class, COLLECTION_NAME);
    }

    /**
     * Save or update a facility.
     *
     * @param entity Facility to persist
     * @return persisted Facility
     */
    @Override
    public Facility save(Facility entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Find a facility by its UUID.
     *
     * @param id facility UUID
     * @return optional containing the facility when present
     */
    @Override
    public Optional<Facility> findById(UUID id) {
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        Facility facility = this.mongoTemplate.findOne(query, Facility.class, COLLECTION_NAME);
        return Optional.ofNullable(facility);
    }

    /**
     * Finds all facilities with pagination support.
     *
     * @param pageable the pagination information
     * @return a Page containing the paginated facilities
     */
    @Override
    public Page<Facility> findAll(@NonNull Pageable pageable) {
        return this.findAll(pageable, null, null);
    }

    /**
     * Finds all facilities with pagination, type filter, and status filter support.
     *
     * @param pageable the pagination information
     * @param type optional facility type filter
     * @param status optional facility status filter
     * @return a Page containing the paginated and filtered facilities
     */
    @Override
    public Page<Facility> findAll(@NonNull Pageable pageable, FacilityType type, FacilityStatus status) {
        FacilitySearchCriteria criteria = new FacilitySearchCriteriaBuilder()
                .withFacilityType(type)
                .withStatus(status)
                .build();
        return findAll(pageable, criteria);
    }

    /**
     * Find facilities with advanced search criteria and pagination.
     * Supports filtering by multiple attributes dynamically.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return page of matching facilities
     */
    public Page<Facility> findAll(@NonNull Pageable pageable, @NonNull FacilitySearchCriteria criteria) {
        Query dataQuery = new Query();
        Query countQuery = new Query();
        List<Criteria> criterias = buildSearchCriterias(criteria);
        if (!criterias.isEmpty()) {
            Criteria combinedCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[0]));
            dataQuery.addCriteria(combinedCriteria);
            countQuery.addCriteria(combinedCriteria);
        }
        dataQuery.with(pageable);
        List<Facility> facilities = this.mongoTemplate.find(dataQuery, Facility.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(countQuery, Facility.class, COLLECTION_NAME);
        return new PageImpl<>(facilities, pageable, total);
    }

    /**
     * Builds a list of MongoDB Criteria from search criteria.
     * Each non-null filter is converted to a corresponding Criteria object.
     *
     * @param criteria search criteria
     * @return list of Criteria objects
     */
    private List<Criteria> buildSearchCriterias(@NonNull FacilitySearchCriteria criteria) {
        List<Criteria> criterias = new ArrayList<>();
        if (criteria.getFacilityType() != null) {
            criterias.add(Criteria.where(MongoFields.FACILITY_TYPE).is(criteria.getFacilityType()));
        }
        if (criteria.getStatus() != null) {
            criterias.add(Criteria.where(MongoFields.STATUS).is(criteria.getStatus()));
        }
        if (criteria.getLocationPostalAddress() != null) {
            criterias.add(Criteria.where(FIELD_LOCATION_POSTAL_ADDRESS)
                    .regex(criteria.getLocationPostalAddress(), CASE_INSENSITIVE_REGEX_FLAG));
        }
        if (criteria.getName() != null) {
            criterias.add(Criteria.where(FIELD_NAME)
                    .regex(criteria.getName(), CASE_INSENSITIVE_REGEX_FLAG));
        }
        return criterias;
    }

    /**
     * Counts all facilities in the collection.
     *
     * @return total facility count
     */
    @Override
    public long count() {
        return MongoEnumTypeCounts.countAll(this.mongoTemplate, Facility.class, COLLECTION_NAME);
    }

    /**
     * Counts facilities grouped by {@link FacilityType}.
     *
     * @return map with every facility type and its count
     */
    @Override
    public Map<FacilityType, Long> countByFacilityType() {
        return MongoEnumTypeCounts.countByEnumField(
                this.mongoTemplate,
                Facility.class,
                COLLECTION_NAME,
                MongoFields.FACILITY_TYPE,
                FacilityType.class);
    }
}
