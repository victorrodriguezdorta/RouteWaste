package es.ull.project.adapter.mongo.spring;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.ull.project.adapter.mongo.document.entity.FacilityDocument;

@Repository
public interface FacilitySpringRepository extends MongoRepository<FacilityDocument, String> {

}
