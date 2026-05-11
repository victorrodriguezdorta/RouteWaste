package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.domain.entity.ContainerDailyState;

/**
 * Repository abstraction for ContainerDailyState persistence.
 */
public interface ContainerDailyStateRepository {

    ContainerDailyState save(ContainerDailyState entity);

    Optional<ContainerDailyState> findById(UUID id);

    List<ContainerDailyState> findAll();

    void delete(ContainerDailyState entity);
}
