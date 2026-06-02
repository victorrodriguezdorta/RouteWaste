package es.ull.project.adapter.rest.serialization.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ull.project.adapter.rest.mapper.AlgorithmExecutionResponseMapper;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.application.port.algorithm.AlgorithmExecutionPayloadSerializer;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import org.springframework.stereotype.Component;

/**
 * Jackson-based serializer for algorithm runner input payloads.
 */
@Component
public class JacksonAlgorithmExecutionPayloadSerializer implements AlgorithmExecutionPayloadSerializer {

    private static final String ERR_SERIALIZE = "Failed to serialize the processed algorithm payload";

    private final ObjectMapper objectMapper;

    /**
     * Creates the serializer.
     *
     * @param objectMapper shared JSON mapper
     */
    public JacksonAlgorithmExecutionPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Serializes the processed algorithm execution data into a JSON payload.
     *
     * @param result    execution input resolved from domain data
     * @param maxBudget maximum budget constraint applied to the payload
     * @return JSON payload accepted by the algorithm runner
     */
    @Override
    public AlgorithmJsonPayload serialize(AlgorithmExecutionResult result, MaximumBudget maxBudget) {
        AlgorithmExecutionResponseBody responseBody = AlgorithmExecutionResponseMapper.toResponseBody(result);
        responseBody.maxBudget = maxBudget;
        try {
            return new AlgorithmJsonPayload(this.objectMapper.writeValueAsString(responseBody));
        } catch (JsonProcessingException exception) {
            throw new AlgorithmExecutionException(ERR_SERIALIZE, exception);
        }
    }
}
