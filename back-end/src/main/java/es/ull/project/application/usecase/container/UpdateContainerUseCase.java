package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import java.util.UUID;

public interface UpdateContainerUseCase {
    Container update(UUID id, Container newContainer);
}
