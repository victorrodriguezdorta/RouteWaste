package es.ull.project.adapter.mongo.spring;

import es.ull.project.adapter.mongo.document.entity.ContainerDocument;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerSpringRepository extends MongoRepository<ContainerDocument, String> {

}
