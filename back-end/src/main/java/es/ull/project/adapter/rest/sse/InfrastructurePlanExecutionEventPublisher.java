package es.ull.project.adapter.rest.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Manages SSE subscribers and broadcasts infrastructure plan execution updates.
 */
@Component
public class InfrastructurePlanExecutionEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructurePlanExecutionEventPublisher.class);
    private static final String EVENT_PLAN_UPDATED = "plan-updated";
    private static final String EVENT_CONNECTED = "connected";
    private static final String CONNECTED_PAYLOAD = "ok";

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    /**
     * Creates the publisher.
     *
     * @param objectMapper shared JSON mapper
     */
    public InfrastructurePlanExecutionEventPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Registers a new SSE client and sends an initial connected event.
     *
     * @param emitter SSE connection for the client
     */
    public void register(SseEmitter emitter) {
        this.emitters.add(emitter);
        emitter.onCompletion(() -> this.remove(emitter));
        emitter.onTimeout(() -> this.remove(emitter));
        emitter.onError(error -> this.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name(EVENT_CONNECTED).data(CONNECTED_PAYLOAD));
        } catch (IOException e) {
            logger.warn("Failed to send SSE connected event", e);
            this.remove(emitter);
        }
    }

    /**
     * Broadcasts a plan execution state change to all connected clients.
     *
     * @param planId          updated plan identifier
     * @param executionState  new execution state
     * @param failureReason   optional failure description
     */
    public void publishPlanUpdated(
            UUID planId,
            InfrastructurePlanExecutionState executionState,
            String failureReason) {
        if (planId == null || executionState == null) {
            return;
        }
        InfrastructurePlanExecutionEventBody body =
                new InfrastructurePlanExecutionEventBody(planId, executionState, failureReason);
        String json;
        try {
            json = this.objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize infrastructure plan execution SSE payload", e);
            return;
        }
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event().name(EVENT_PLAN_UPDATED).data(json));
            } catch (IOException e) {
                logger.debug("Removing SSE emitter after send failure", e);
                this.remove(emitter);
            }
        }
    }

    private void remove(SseEmitter emitter) {
        this.emitters.remove(emitter);
        try {
            emitter.complete();
        } catch (RuntimeException ignored) {
            ignored.getMessage();
        }
    }
}
