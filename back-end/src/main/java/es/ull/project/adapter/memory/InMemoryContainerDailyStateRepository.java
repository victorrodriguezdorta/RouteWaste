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
    public void delete(ContainerDailyState entity) {
        if (entity != null) {
            states.remove(entity.getId());
        }
    }
}
