package es.ull.project.application.repository;

import es.ull.project.domain.entity.Container;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContainerRepository {
    Container save(Container container);
    Optional<Container> findById(UUID id);
    List<Container> findAll();
    void delete(Container container);
}
