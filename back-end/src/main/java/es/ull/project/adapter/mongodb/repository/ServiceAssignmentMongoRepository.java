package es.ull.project.adapter.mongodb.repository;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.ServiceAssignment;

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
 * MongoDB implementation of the ServiceAssignmentRepository interface.
 *
 * This class provides concrete implementations for CRUD operations
 * on ServiceAssignment entities using MongoDB as the data store.
 * Active when "memory" profile is NOT enabled (default).
 */
@Repository
@Profile("!memory")
public class ServiceAssignmentMongoRepository implements ServiceAssignmentRepository {

    public static final String COLLECTION_NAME = "serviceassignments";
    private static final String FIELD_ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Delete a service assignment.
     *
     * @param entity ServiceAssignment to delete
     */
    @Override
    public void delete(ServiceAssignment entity) {
        if (entity == null) {
            return;
        }
        this.mongoTemplate.remove(entity, COLLECTION_NAME);
    }

    /**
     * Fetch all service assignments.
     *
     * @return list of service assignments
     */
    @Override
    public List<ServiceAssignment> fetchAll() {
        return this.mongoTemplate.findAll(ServiceAssignment.class, COLLECTION_NAME);
    }

    /**
     * Find all service assignments (alias used by some services).
     *
     * @return list of service assignments
     */
    @Override
    public List<ServiceAssignment> findAll() {
        return this.mongoTemplate.findAll(ServiceAssignment.class, COLLECTION_NAME);
    }

    /**
     * Save or update a service assignment.
     *
     * @param entity ServiceAssignment to persist
     * @return persisted ServiceAssignment
     */
    @Override
    public ServiceAssignment save(ServiceAssignment entity) {
        if (entity == null) {
            return null;
        }
        return this.mongoTemplate.save(entity, COLLECTION_NAME);
    }

    /**
     * Find a service assignment by UUID.
     *
     * @param id assignment UUID
     * @return optional with the assignment if found
     */
    @Override
    public Optional<ServiceAssignment> findById(UUID id) {
        Query query = new Query(Criteria.where(FIELD_ID).is(id));
        ServiceAssignment serviceAssignment = this.mongoTemplate.findOne(query, ServiceAssignment.class, COLLECTION_NAME);
        return Optional.ofNullable(serviceAssignment);
    }
}
