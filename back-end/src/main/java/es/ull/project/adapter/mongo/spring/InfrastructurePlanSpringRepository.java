package es.ull.project.adapter.mongo.spring;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ull.project.adapter.mongo.document.entity.InfrastructurePlanDocument;

public interface InfrastructurePlanSpringRepository extends MongoRepository<InfrastructurePlanDocument, String> {
}
