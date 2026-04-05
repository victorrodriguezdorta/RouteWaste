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

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
/**
 * In-memory FacilityRepository for tests and local runs.
 */
public class InMemoryFacilityRepository implements FacilityRepository {

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

        // Filter by type and status
        if (type != null || status != null) {
            allFacilities = allFacilities.stream()
                    .filter(f -> type == null || f.getFacilityType() == type)
                    .filter(f -> status == null || f.getStatus() == status)
                    .toList();
        }

        // Sort
        if (!pageable.getSort().isEmpty()) {
            allFacilities = new ArrayList<>(allFacilities);
            allFacilities.sort((f1, f2) -> {
                for (var order : pageable.getSort()) {
                    int cmp = 0;
                    String property = order.getProperty();
                    switch (property) {
                        case "facilityType" -> cmp = f1.getFacilityType().compareTo(f2.getFacilityType());
                        case "status" -> cmp = f1.getStatus().compareTo(f2.getStatus());
                        case "location.postalAddress" -> {
                            String loc1 = f1.getLocation() != null ? f1.getLocation().getPostalAddress() : "";
                            String loc2 = f2.getLocation() != null ? f2.getLocation().getPostalAddress() : "";
                            cmp = loc1.compareTo(loc2);
                        }
                        case "capacity.value" -> {
                            double cap1 = f1.getCapacity() != null ? f1.getCapacity().getValue() : 0.0;
                            double cap2 = f2.getCapacity() != null ? f2.getCapacity().getValue() : 0.0;
                            cmp = Double.compare(cap1, cap2);
                        }
                    }
                    if (cmp != 0) {
                        return order.isAscending() ? cmp : -cmp;
                    }
                }
                return 0;
            });
        }

        // Paginate
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
}
