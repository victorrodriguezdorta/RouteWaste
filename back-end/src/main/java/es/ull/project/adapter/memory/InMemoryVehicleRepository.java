package es.ull.project.adapter.memory;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.repository.query.VehicleSearchCriteria;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * In-memory VehicleRepository for tests and local runs.
 */
public class InMemoryVehicleRepository implements VehicleRepository {

    private static final String FIELD_CAPACITY_KILOGRAMS = "capacityKilograms.Kilograms";
    private static final String FIELD_CAPACITY_LITERS = "CapacityLiters.liters";
    private static final String FIELD_COST = "costPerKilometer.amount";
    private static final String FIELD_TYPE = "vehicleType";
    private static final String FIELD_NAME = "name";

    private final Map<UUID, Vehicle> store = new LinkedHashMap<>();

    /**
     * Deletes a vehicle from the repository.
     *
     * @param entity the vehicle to delete
     */
    @Override
    public void delete(Vehicle entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all vehicles from the repository.
     *
     * @return a list of all vehicles
     */
    @Override
    public List<Vehicle> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all vehicles in the repository.
     *
     * @return a list of all vehicles
     */
    @Override
    public List<Vehicle> findAll() {
        return fetchAll();
    }

    /**
     * Finds vehicles in the repository using pagination.
     *
     * @param pageable pagination configuration
     * @return a page of vehicles
     */
    @Override
    public Page<Vehicle> findAll(Pageable pageable) {
        List<Vehicle> all = fetchAll();
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= all.size()) {
            return new PageImpl<>(List.of(), pageable, all.size());
        }
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Vehicle> content = all.subList(start, end);
        return new PageImpl<>(content, pageable, all.size());
    }

    /**
     * Finds vehicles in the repository using pagination, optional type filter and sort.
     *
     * @param pageable    pagination and sort configuration
     * @param vehicleType optional vehicle type to filter by (null means no filter)
     * @return a page of matching vehicles
     */
    @Override
    public Page<Vehicle> findAll(Pageable pageable, VehicleType vehicleType) {
        Stream<Vehicle> stream = fetchAll().stream();
        if (vehicleType != null) {
            stream = stream.filter(v -> vehicleType.equals(v.getVehicleType()));
        }
        if (pageable.getSort().isSorted()) {
            Comparator<Vehicle> comparator = null;
            for (Sort.Order order : pageable.getSort()) {
                Comparator<Vehicle> fieldComparator = switch (order.getProperty()) {
                    case FIELD_CAPACITY_KILOGRAMS ->
                            Comparator.comparingDouble(v -> v.getCapacityKilograms().getKilograms());
                    case FIELD_CAPACITY_LITERS ->
                            Comparator.comparingDouble(v -> v.getCapacityLiters().getLiters());
                    case FIELD_COST ->
                            Comparator.comparingDouble(v -> v.getCostPerKilometer().getAmount());
                    case FIELD_TYPE ->
                            Comparator.comparing((Vehicle v) -> v.getVehicleType().name());
                    default -> Comparator.comparingInt(v -> 0);
                };
                if (order.isDescending()) {
                    fieldComparator = fieldComparator.reversed();
                }
                comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
            }
            if (comparator != null) {
                stream = stream.sorted(comparator);
            }
        }
        List<Vehicle> filtered = stream.collect(Collectors.toList());
        long total = filtered.size();
        int start = (int) pageable.getOffset();
        if (start >= filtered.size()) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, total);
    }

    /**
     * Finds vehicles in the repository using pagination, search criteria and sort.
     *
     * @param pageable pagination and sort configuration
     * @param criteria search criteria with optional filters
     * @return a page of matching vehicles
     */
    @Override
    public Page<Vehicle> findAll(Pageable pageable, VehicleSearchCriteria criteria) {
        Stream<Vehicle> stream = fetchAll().stream();
        if (criteria != null && criteria.hasCriteria()) {
            if (criteria.getVehicleType() != null) {
                stream = stream.filter(v -> criteria.getVehicleType().equals(v.getVehicleType()));
            }
            if (criteria.getName() != null) {
                String nameFilter = criteria.getName().toLowerCase();
                stream = stream.filter(v -> v.getName() != null
                        && v.getName().getValue().toLowerCase().contains(nameFilter));
            }
        }
        if (pageable.getSort().isSorted()) {
            Comparator<Vehicle> comparator = null;
            for (Sort.Order order : pageable.getSort()) {
                Comparator<Vehicle> fieldComparator = switch (order.getProperty()) {
                    case FIELD_CAPACITY_KILOGRAMS ->
                            Comparator.comparingDouble(v -> v.getCapacityKilograms().getKilograms());
                    case FIELD_CAPACITY_LITERS ->
                            Comparator.comparingDouble(v -> v.getCapacityLiters().getLiters());
                    case FIELD_COST ->
                            Comparator.comparingDouble(v -> v.getCostPerKilometer().getAmount());
                    case FIELD_TYPE ->
                            Comparator.comparing((Vehicle v) -> v.getVehicleType().name());
                    default -> Comparator.comparingInt(v -> 0);
                };
                if (order.isDescending()) {
                    fieldComparator = fieldComparator.reversed();
                }
                comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
            }
            if (comparator != null) {
                stream = stream.sorted(comparator);
            }
        }
        List<Vehicle> filtered = stream.collect(Collectors.toList());
        long total = filtered.size();
        int start = (int) pageable.getOffset();
        if (start >= filtered.size()) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, total);
    }

    /**
     * Saves a vehicle to the repository.
     *
     * @param entity the vehicle to save
     * @return the saved vehicle, or null if the entity was null
     */
    @Override
    public Vehicle save(Vehicle entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle
     * @return an Optional containing the vehicle if found, or empty if not found
     */
    @Override
    public Optional<Vehicle> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Counts all vehicles in the store.
     *
     * @return total vehicle count
     */
    @Override
    public long count() {
        return store.size();
    }

    /**
     * Counts vehicles grouped by {@link VehicleType}.
     *
     * @return map with every vehicle type and its count
     */
    @Override
    public Map<VehicleType, Long> countByVehicleType() {
        return InMemoryEnumTypeCounts.countByEnum(
                store.values().stream(),
                VehicleType.class,
                Vehicle::getVehicleType);
    }
}
