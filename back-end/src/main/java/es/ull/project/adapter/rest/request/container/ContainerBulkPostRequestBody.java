package es.ull.project.adapter.rest.request.container;

import java.util.List;

/**
 * Request body for bulk container creation.
 * Accepts either a JSON array of containers or an object with a {@code containers} field.
 */
public class ContainerBulkPostRequestBody {

    /**
     * Containers to create, in submission order.
     */
    public List<ContainerPostRequestBody> containers;
}
