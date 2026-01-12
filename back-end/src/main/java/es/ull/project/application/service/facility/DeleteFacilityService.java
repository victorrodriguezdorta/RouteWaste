package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteFacilityService implements DeleteFacilityUseCase {

    @Autowired
    private FacilityRepository repository;

    @Override
    public Facility delete(UUID id) {
        Facility existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
        this.repository.delete(existing);
        return existing;
    }
}
