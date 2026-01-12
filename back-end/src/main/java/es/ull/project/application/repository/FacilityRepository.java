package es.ull.project.application.repository;

import es.ull.project.domain.entity.Facility;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacilityRepository {
    Facility save(Facility facility);
    Optional<Facility> findById(UUID id);
    List<Facility> findAll();
    void delete(Facility facility);
}
