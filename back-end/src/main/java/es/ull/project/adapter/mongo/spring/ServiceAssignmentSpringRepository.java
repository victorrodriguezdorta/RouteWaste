package es.ull.project.adapter.mongo.spring;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.ull.project.adapter.mongo.document.entity.ServiceAssignmentDocument;

@Repository
public interface ServiceAssignmentSpringRepository extends MongoRepository<ServiceAssignmentDocument, String> {
    /**
     * Find all service assignments by container ID.
     *
     * @param containerId the container ID
     * @return list of service assignment documents
     */
    List<ServiceAssignmentDocument> findByContainerId(String containerId);

    /**
     * Find all service assignments by facility ID.
     *
     * @param facilityId the facility ID
     * @return list of service assignment documents
     */
    List<ServiceAssignmentDocument> findByFacilityId(String facilityId);
}
