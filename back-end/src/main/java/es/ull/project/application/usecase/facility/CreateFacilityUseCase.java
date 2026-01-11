package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;

public interface CreateFacilityUseCase {
    Facility create(Facility facility);
}
