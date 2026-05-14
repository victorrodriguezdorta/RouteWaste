package es.ull.project.adapter.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.domain.entity.ContainerDailyState;

/**
 * In-memory repository implementation for ContainerDailyState.
 */
@Repository
@Profile("memory")
public class InMemoryContainerDailyStateRepository implements ContainerDailyStateRepository {

    private final Map<UUID, ContainerDailyState> states = new LinkedHashMap<>();

    @Override
    public ContainerDailyState save(ContainerDailyState entity) {
        if (entity == null) {
            return null;
        }
        states.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<ContainerDailyState> findById(UUID id) {
        return Optional.ofNullable(states.get(id));
    }

    @Override
    public List<ContainerDailyState> findAll() {
        return new ArrayList<>(states.values());
    }

    @Override
    public List<ContainerDailyState> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        if (infrastructurePlanId == null) {
            return List.of();
        }
        List<ContainerDailyState> matches = new ArrayList<>();
        for (ContainerDailyState state : states.values()) {
            if (infrastructurePlanId.equals(state.getInfrastructurePlanId())) {
                matches.add(state);
            }
        }
        return matches;
    }

    @Override
    public void delete(ContainerDailyState entity) {
        if (entity != null) {
            states.remove(entity.getId());
        }
    }
}
