package es.ull.project.adapter.rest.deserialization.container;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.deserialization.bulk.AbstractBulkPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.request.container.ContainerBulkPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;

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
