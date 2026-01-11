package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import java.util.List;
import java.util.UUID;

public interface ReadContainerUseCase {
    Container fetch(UUID id);
    List<Container> fetchAll();
}
