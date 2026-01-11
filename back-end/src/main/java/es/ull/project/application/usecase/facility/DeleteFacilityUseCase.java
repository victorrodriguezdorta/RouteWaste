package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import java.util.UUID;

public interface DeleteFacilityUseCase {
    Facility delete(UUID id);
}
