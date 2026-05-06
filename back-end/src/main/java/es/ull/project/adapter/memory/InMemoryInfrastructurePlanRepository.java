package es.ull.project.adapter.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * In-memory InfrastructurePlanRepository for tests and local runs.
 */
public class InMemoryInfrastructurePlanRepository implements InfrastructurePlanRepository {

    private final Map<UUID, InfrastructurePlan> store = new LinkedHashMap<>();

    /**
     * Deletes an infrastructure plan from the repository.
     *
     * @param entity the infrastructure plan to delete
     */
    @Override
    public void delete(InfrastructurePlan entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all infrastructure plans from the repository.
     *
     * @return a list of all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all infrastructure plans in the repository.
     *
     * @return a list of all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> findAll() {
        return fetchAll();
    }

    /**
     * Finds all infrastructure plans using pagination and sorting.
     *
     * @param pageable pagination and sort information
     * @return a page of infrastructure plans
     */
    @Override
    public Page<InfrastructurePlan> findAll(Pageable pageable) {
        List<InfrastructurePlan> plans = new ArrayList<>(store.values());
        sortPlans(plans, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), plans.size());
        List<InfrastructurePlan> pageContent = start >= plans.size() ? List.of() : plans.subList(start, end);
        return new PageImpl<>(pageContent, pageable, plans.size());
    }

    private void sortPlans(List<InfrastructurePlan> plans, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }

        java.util.Comparator<InfrastructurePlan> comparator = null;
        for (Sort.Order order : sort) {
            java.util.Comparator<InfrastructurePlan> propertyComparator = buildComparator(order.getProperty());
            if (propertyComparator == null) {
                continue;
            }
            if (order.isDescending()) {
                propertyComparator = propertyComparator.reversed();
            }
            comparator = comparator == null ? propertyComparator : comparator.thenComparing(propertyComparator);
        }

        if (comparator != null) {
            plans.sort(comparator);
        }
    }

    private java.util.Comparator<InfrastructurePlan> buildComparator(String property) {
        return switch (property) {
            case "_id", "id" -> java.util.Comparator.comparing(InfrastructurePlan::getId, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "executedAt" -> java.util.Comparator.comparing(InfrastructurePlan::getExecutedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "estimatedTotalCost.amount" -> java.util.Comparator.comparing(
                    plan -> plan.getEstimatedTotalCost() != null ? plan.getEstimatedTotalCost().getAmount() : null,
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "numberOfDays" -> java.util.Comparator.comparing(InfrastructurePlan::getNumberOfDays, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "averagePickupTimeMinutes" -> java.util.Comparator.comparing(InfrastructurePlan::getAveragePickupTimeMinutes, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            default -> null;
        };
    }

    /**
     * Saves an infrastructure plan to the repository.
     *
     * @param entity the infrastructure plan to save
     * @return the saved infrastructure plan, or null if the entity was null
     */
    @Override
    public InfrastructurePlan save(InfrastructurePlan entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan
     * @return an Optional containing the infrastructure plan if found, or empty if not found
     */
    @Override
    public Optional<InfrastructurePlan> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
