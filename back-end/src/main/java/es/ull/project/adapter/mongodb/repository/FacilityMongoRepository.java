package es.ull.project.adapter.mongodb.repository;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import java.util.List;
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
        Query dataQuery = new Query();
        Query countQuery = new Query();
        if (type != null) {
            Criteria criteria = Criteria.where(MongoFields.FACILITY_TYPE).is(type);
            dataQuery.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        if (status != null) {
            Criteria criteria = Criteria.where(MongoFields.STATUS).is(status);
            dataQuery.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        dataQuery.with(pageable);
        List<Facility> content = this.mongoTemplate.find(dataQuery, Facility.class, COLLECTION_NAME);
        long total = this.mongoTemplate.count(countQuery, COLLECTION_NAME);
        return new PageImpl<>(content, pageable, total);
    }
}
