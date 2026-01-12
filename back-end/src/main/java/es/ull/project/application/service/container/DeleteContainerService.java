package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.domain.entity.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteContainerService implements DeleteContainerUseCase {

    @Autowired
    private ContainerRepository repository;

    @Override
    public Container delete(UUID id) {
        Container existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
        this.repository.delete(existing);
        return existing;
    }
}
