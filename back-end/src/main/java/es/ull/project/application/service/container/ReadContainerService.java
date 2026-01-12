package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.domain.entity.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadContainerService implements ReadContainerUseCase {

    @Autowired
    private ContainerRepository repository;

    @Override
    public Container fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
    }

    @Override
    public List<Container> fetchAll() {
        return this.repository.findAll();
    }
}
