package es.ull.project.adapter.mongodb.repository;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Facility;

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
        Query query = new Query(Criteria.where("id").is(id));
        Facility facility = this.mongoTemplate.findOne(query, Facility.class, COLLECTION_NAME);
        return Optional.ofNullable(facility);
    }
}
