package es.ull.project.adapter.rest.json.container;

import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.request.container.ContainerBulkPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.support.AbstractBulkPostRequestBodyDeserializer;

/**
 * Deserializes bulk container creation payloads.
 */
public class ContainerBulkPostRequestBodyDeserializer
        extends AbstractBulkPostRequestBodyDeserializer<ContainerPostRequestBody, ContainerBulkPostRequestBody> {

    /**
     * Registers the single-item container deserializer for each array element.
     */
    public ContainerBulkPostRequestBodyDeserializer() {
        super(
                new ContainerPostRequestBodyDeserializer(),
                ContainerBulkPostRequestBody::new,
                JsonFields.CONTAINERS,
                (wrapper, items) -> wrapper.containers = items);
    }
}
