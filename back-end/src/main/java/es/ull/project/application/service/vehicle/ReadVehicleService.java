package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.ReadVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadVehicleService implements ReadVehicleUseCase {

    @Autowired
    private VehicleRepository repository;

    @Override
    public Vehicle fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
    }

    @Override
    public List<Vehicle> fetchAll() {
        return this.repository.findAll();
    }
}
