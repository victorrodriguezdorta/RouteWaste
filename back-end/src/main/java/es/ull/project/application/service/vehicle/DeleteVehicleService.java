package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.DeleteVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteVehicleService implements DeleteVehicleUseCase {

    @Autowired
    private VehicleRepository repository;

    @Override
    public Vehicle delete(UUID id) {
        Vehicle existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
        this.repository.delete(existing);
        return existing;
    }
}
