package es.ull.project.application.repository;

import es.ull.project.domain.entity.ContainerDailyState;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository abstraction for ContainerDailyState persistence.
 */
public interface ContainerDailyStateRepository {

    /**
     * Persists a container daily state snapshot.
     *
     * @param entity container daily state to persist
     * @return persisted container daily state
     */
    ContainerDailyState save(ContainerDailyState entity);

    /**
     * Finds a container daily state by identifier.
     *
     * @param id container daily state id
     * @return matching entity, or empty when not found
     */
    Optional<ContainerDailyState> findById(UUID id);

    /**
     * Finds all container daily state snapshots.
     *
     * @return all persisted container daily states
     */
    List<ContainerDailyState> findAll();

    /**
     * Finds container daily state snapshots linked to an infrastructure plan.
     *
     * @param infrastructurePlanId parent plan id
     * @return matching entities, empty if id is null
     */
    List<ContainerDailyState> findByInfrastructurePlanId(UUID infrastructurePlanId);

    /**
     * Deletes a container daily state snapshot.
     *
     * @param entity container daily state to delete
     */
    void delete(ContainerDailyState entity);
}
