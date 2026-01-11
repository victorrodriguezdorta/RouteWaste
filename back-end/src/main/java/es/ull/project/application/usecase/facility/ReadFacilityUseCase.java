package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import java.util.List;
import java.util.UUID;

public interface ReadFacilityUseCase {
    Facility fetch(UUID id);
    List<Facility> fetchAll();
}
