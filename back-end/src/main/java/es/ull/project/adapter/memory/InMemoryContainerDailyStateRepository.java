package es.ull.project.adapter.memory;

import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.domain.entity.ContainerDailyState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository implementation for ContainerDailyState.
 */
@Repository
@Profile("memory")
public class InMemoryContainerDailyStateRepository implements ContainerDailyStateRepository {

    private final Map<UUID, ContainerDailyState> states = new LinkedHashMap<>();

    /**
     * Persists a container daily state in memory.
     *
     * @param entity container daily state to persist
     * @return persisted entity, or null when input is null
     */
    @Override
    public ContainerDailyState save(ContainerDailyState entity) {
        if (entity == null) {
            return null;
        }
        states.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a container daily state by identifier.
     *
     * @param id container daily state id
     * @return matching entity, or empty when not found
     */
    @Override
    public Optional<ContainerDailyState> findById(UUID id) {
        return Optional.ofNullable(states.get(id));
    }

    /**
     * Finds all in-memory container daily states.
     *
     * @return all container daily states
     */
    @Override
    public List<ContainerDailyState> findAll() {
        return new ArrayList<>(states.values());
    }

    /**
     * Finds container daily states linked to an infrastructure plan.
     *
     * @param infrastructurePlanId parent infrastructure plan id
     * @return matching container daily states
     */
    @Override
    public List<ContainerDailyState> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        if (infrastructurePlanId == null) {
            return List.of();
        }
        List<ContainerDailyState> matches = new ArrayList<>();
        for (ContainerDailyState state : states.values()) {
            if (state.getInfrastructurePlanId().filter(infrastructurePlanId::equals).isPresent()) {
                matches.add(state);
            }
        }
        return matches;
    }

    /**
     * Deletes a container daily state from memory.
     *
     * @param entity container daily state to delete
     */
    @Override
    public void delete(ContainerDailyState entity) {
        if (entity != null) {
            states.remove(entity.getId());
        }
    }
}
