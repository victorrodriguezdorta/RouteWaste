package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadFacilityService implements ReadFacilityUseCase {

    @Autowired
    private FacilityRepository repository;

    @Override
    public Facility fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
    }

    @Override
    public List<Facility> fetchAll() {
        return this.repository.findAll();
    }
}
