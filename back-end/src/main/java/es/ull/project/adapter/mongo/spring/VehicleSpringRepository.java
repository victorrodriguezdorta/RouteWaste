package es.ull.project.adapter.mongo.spring;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.ull.project.adapter.mongo.document.entity.VehicleDocument;

@Repository
public interface VehicleSpringRepository extends MongoRepository<VehicleDocument, String> {

}
