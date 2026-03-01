package es.ull.project.adapter.mongo.spring;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.ull.project.adapter.mongo.document.entity.InfrastructurePlanDocument;

@Repository
public interface InfrastructurePlanSpringRepository extends MongoRepository<InfrastructurePlanDocument, String> {
    /**
     * Find all infrastructure plans that contain a specific facility ID in their selectedFacilityIds list.
     *
     * @param facilityId the facility ID
     * @return list of infrastructure plan documents
     */
    List<InfrastructurePlanDocument> findBySelectedFacilityIdsContaining(String facilityId);

    /**
     * Find all infrastructure plans that contain a specific service assignment ID in their serviceAssignmentIds list.
     *
     * @param serviceAssignmentId the service assignment ID
     * @return list of infrastructure plan documents
     */
    List<InfrastructurePlanDocument> findByServiceAssignmentIdsContaining(String serviceAssignmentId);
}
