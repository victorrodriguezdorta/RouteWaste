package es.ull.project.adapter.mongo.spring;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ull.project.adapter.mongo.document.entity.ServiceAssignmentDocument;

public interface ServiceAssignmentSpringRepository extends MongoRepository<ServiceAssignmentDocument, String> {
}
