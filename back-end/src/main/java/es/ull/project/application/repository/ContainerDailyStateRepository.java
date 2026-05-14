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

    /**
     * Finds container daily state snapshots linked to an infrastructure plan.
     *
     * @param infrastructurePlanId parent plan id
     * @return matching entities, empty if id is null
     */
    List<ContainerDailyState> findByInfrastructurePlanId(UUID infrastructurePlanId);

    void delete(ContainerDailyState entity);
}
