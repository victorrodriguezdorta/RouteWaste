package es.ull.project.adapter.memory;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;
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

/**
 * In-memory InfrastructurePlanRepository for tests and local runs.
 */
public class InMemoryInfrastructurePlanRepository implements InfrastructurePlanRepository {

    private static final String FIELD_ID = "id";
    private static final String FIELD_ID_MONGO = "_id";
    private static final String FIELD_EXECUTED_AT = "executedAt";
    private static final String FIELD_ESTIMATED_TOTAL_COST = "estimatedTotalCost.amount";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP_TIME = "averagePickupTimeMinutes";

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

    /**
     * Sorts the given list of infrastructure plans according to the provided sort specification.
     *
     * @param plans list of infrastructure plans to sort (modified in-place)
     * @param sort  sort specification containing ordered sort orders
     */
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

    /**
     * Builds a comparator for {@link InfrastructurePlan} instances based on the given property name.
     *
     * @param property the field name to compare by (e.g. "id", "executedAt")
     * @return a comparator for the given property, or {@code null} if the property is not supported
     */
    private java.util.Comparator<InfrastructurePlan> buildComparator(String property) {
        return switch (property) {
            case FIELD_ID_MONGO, FIELD_ID -> java.util.Comparator.comparing(InfrastructurePlan::getId, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case FIELD_EXECUTED_AT -> java.util.Comparator.comparing(
                    plan -> plan.getExecutedAt() != null ? plan.getExecutedAt().getTimestamp() : null,
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case FIELD_ESTIMATED_TOTAL_COST -> java.util.Comparator.comparing(
                    plan -> plan.getEstimatedTotalCost() != null ? plan.getEstimatedTotalCost().getAmount() : null,
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case FIELD_NUMBER_OF_DAYS -> java.util.Comparator.comparing(
                    plan -> plan.getNumberOfDays() != null ? plan.getNumberOfDays().getValue() : null,
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case FIELD_AVERAGE_PICKUP_TIME -> java.util.Comparator.comparing(
                    plan -> plan.getAveragePickupTimeMinutes() != null ? plan.getAveragePickupTimeMinutes().getValue() : null,
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
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
