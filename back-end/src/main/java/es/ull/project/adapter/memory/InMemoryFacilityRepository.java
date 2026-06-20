package es.ull.project.adapter.memory;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.query.FacilitySearchCriteria;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * In-memory FacilityRepository for tests and local runs.
 */
public class InMemoryFacilityRepository implements FacilityRepository {

    private static final int ZERO = 0;

    private static final String FIELD_FACILITY_TYPE = "facilityType";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_LOCATION_POSTAL_ADDRESS = "location.postalAddress";
    private static final String FIELD_CAPACITY_VALUE = "capacity.value";
    private static final String FIELD_STORAGE_CAPACITY = "storageCapacity";
    private static final String FIELD_PROCESSING_CAPACITY = "processingCapacity";
    private static final String FIELD_UNLOADING_TIME = "unloadingTime";
    private static final String FIELD_OPENING_FIXED_COST = "openingFixedCost";
    private static final String FIELD_CURRENT_FILLING_LEVEL = "currentFillingLevel";

    private final Map<UUID, Facility> store = new LinkedHashMap<>();

    /**
     * Deletes a facility from the repository.
     *
     * @param entity the facility to delete
     */
    @Override
    public void delete(Facility entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all facilities from the repository.
     *
     * @return a list of all facilities
     */
    @Override
    public List<Facility> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all facilities in the repository.
     *
     * @return a list of all facilities
     */
    @Override
    public List<Facility> findAll() {
        return fetchAll();
    }

    /**
     * Saves a facility to the repository.
     *
     * @param entity the facility to save
     * @return the saved facility, or null if the entity was null
     */
    @Override
    public Facility save(Facility entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a facility by its unique identifier.
     *
     * @param id the unique identifier of the facility
     * @return an Optional containing the facility if found, or empty if not found
     */
    @Override
    public Optional<Facility> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Finds all facilities with pagination support.
     *
     * @param pageable the pagination information
     * @return a Page containing the paginated facilities
     */
    @Override
    public Page<Facility> findAll(Pageable pageable) {
        return this.findAll(pageable, null, null);
    }

    /**
     * Finds all facilities with pagination, type filter, and status filter support.
     *
     * @param pageable the pagination information
     * @param type optional facility type filter
     * @param status optional facility status filter
     * @return a Page containing the paginated and filtered facilities
     */
    @Override
    public Page<Facility> findAll(Pageable pageable, FacilityType type, FacilityStatus status) {
        List<Facility> allFacilities = new ArrayList<>(store.values());
        if (type != null || status != null) {
            allFacilities = allFacilities.stream()
                    .filter(f -> type == null || f.getFacilityType() == type)
                    .filter(f -> status == null || f.getStatus() == status)
                    .toList();
        }
        if (!pageable.getSort().isEmpty()) {
            allFacilities = new ArrayList<>(allFacilities);
            allFacilities.sort((f1, f2) -> {
                for (var order : pageable.getSort()) {
                    int cmp = ZERO;
                    String property = order.getProperty();
                    switch (property) {
                        case FIELD_FACILITY_TYPE -> cmp = f1.getFacilityType().compareTo(f2.getFacilityType());
                        case FIELD_STATUS -> cmp = f1.getStatus().compareTo(f2.getStatus());
                        case FIELD_LOCATION_POSTAL_ADDRESS -> {
                            String loc1 = f1.getLocation() != null ? f1.getLocation().getPostalAddress() : "";
                            String loc2 = f2.getLocation() != null ? f2.getLocation().getPostalAddress() : "";
                            cmp = loc1.compareTo(loc2);
                        }
                        case FIELD_CAPACITY_VALUE -> {
                            double cap1 = f1.getStorageCapacity() != null ? f1.getStorageCapacity().getKilograms() : 0.0;
                            double cap2 = f2.getStorageCapacity() != null ? f2.getStorageCapacity().getKilograms() : 0.0;
                            cmp = Double.compare(cap1, cap2);
                        }
                    }
                    if (cmp != ZERO) {
                        return order.isAscending() ? cmp : -cmp;
                    }
                }
                return ZERO;
            });
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allFacilities.size());
        List<Facility> pageContent;
        if (start >= allFacilities.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = allFacilities.subList(start, end);
        }
        return new PageImpl<>(pageContent, pageable, allFacilities.size());
    }

    /**
     * Finds all facilities with advanced search criteria and pagination.
     * Supports filtering by multiple attributes dynamically.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return page of matching facilities
     */
    @Override
    public Page<Facility> findAll(Pageable pageable, FacilitySearchCriteria criteria) {
        List<Facility> allFacilities = new ArrayList<>(store.values());
        if (criteria != null && criteria.hasCriteria()) {
            allFacilities = allFacilities.stream()
                    .filter(f -> criteria.getFacilityType() == null || f.getFacilityType() == criteria.getFacilityType())
                    .filter(f -> criteria.getStatus() == null || f.getStatus() == criteria.getStatus())
                    .filter(f -> criteria.getLocationPostalAddress() == null ||
                            (f.getLocation() != null && f.getLocation().getPostalAddress() != null &&
                             f.getLocation().getPostalAddress().toLowerCase()
                                .contains(criteria.getLocationPostalAddress().toLowerCase())))
                    .filter(f -> criteria.getName() == null ||
                            (f.getName() != null && f.getName().getValue().toLowerCase()
                                .contains(criteria.getName().toLowerCase())))
                    .toList();
        }
        if (!pageable.getSort().isEmpty()) {
            allFacilities = new ArrayList<>(allFacilities);
            allFacilities.sort((f1, f2) -> {
                for (var order : pageable.getSort()) {
                    int cmp = ZERO;
                    String property = order.getProperty();
                    switch (property) {
                        case FIELD_FACILITY_TYPE -> cmp = f1.getFacilityType().compareTo(f2.getFacilityType());
                        case FIELD_STATUS -> cmp = f1.getStatus().compareTo(f2.getStatus());
                        case FIELD_LOCATION -> {
                            String loc1 = f1.getLocation() != null ? f1.getLocation().getPostalAddress() : "";
                            String loc2 = f2.getLocation() != null ? f2.getLocation().getPostalAddress() : "";
                            cmp = loc1.compareTo(loc2);
                        }
                        case FIELD_STORAGE_CAPACITY -> {
                            double cap1 = f1.getStorageCapacity() != null ? f1.getStorageCapacity().getKilograms() : 0.0;
                            double cap2 = f2.getStorageCapacity() != null ? f2.getStorageCapacity().getKilograms() : 0.0;
                            cmp = Double.compare(cap1, cap2);
                        }
                        case FIELD_PROCESSING_CAPACITY -> {
                            double proc1 = f1.getProcessingCapacity() != null ? f1.getProcessingCapacity().getKilogramsPerDay() : 0.0;
                            double proc2 = f2.getProcessingCapacity() != null ? f2.getProcessingCapacity().getKilogramsPerDay() : 0.0;
                            cmp = Double.compare(proc1, proc2);
                        }
                        case FIELD_UNLOADING_TIME -> {
                            int time1 = f1.getUnloadingTime() != null ? f1.getUnloadingTime().getMinutes() : 0;
                            int time2 = f2.getUnloadingTime() != null ? f2.getUnloadingTime().getMinutes() : 0;
                            cmp = Integer.compare(time1, time2);
                        }
                        case FIELD_OPENING_FIXED_COST -> {
                            double cost1 = f1.getOpeningFixedCost() != null ? f1.getOpeningFixedCost().getAmount() : 0.0;
                            double cost2 = f2.getOpeningFixedCost() != null ? f2.getOpeningFixedCost().getAmount() : 0.0;
                            cmp = Double.compare(cost1, cost2);
                        }
                        case FIELD_CURRENT_FILLING_LEVEL -> {
                            double fill1 = f1.getCurrentFillingLevel() != null ? f1.getCurrentFillingLevel().getLitersPerDay() : 0.0;
                            double fill2 = f2.getCurrentFillingLevel() != null ? f2.getCurrentFillingLevel().getLitersPerDay() : 0.0;
                            cmp = Double.compare(fill1, fill2);
                        }
                    }
                    if (cmp != ZERO) {
                        return order.isAscending() ? cmp : -cmp;
                    }
                }
                return ZERO;
            });
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allFacilities.size());
        List<Facility> pageContent;
        if (start >= allFacilities.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = allFacilities.subList(start, end);
        }
        return new PageImpl<>(pageContent, pageable, allFacilities.size());
    }

    /**
     * Counts all facilities in the store.
     *
     * @return total facility count
     */
    @Override
    public long count() {
        return store.size();
    }

    /**
     * Counts facilities grouped by {@link FacilityType}.
     *
     * @return map with every facility type and its count
     */
    @Override
    public Map<FacilityType, Long> countByFacilityType() {
        return InMemoryEnumTypeCounts.countByEnum(
                store.values().stream(),
                FacilityType.class,
                Facility::getFacilityType);
    }
}
