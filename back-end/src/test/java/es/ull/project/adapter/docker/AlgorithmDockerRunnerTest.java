package es.ull.project.adapter.docker;

import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AlgorithmDockerRunnerTest {

    private static final String PROJECT_DIRECTORY = "..";
    private static final String COMPOSE_FILE = "docker-compose.yml";
    private static final String SERVICE_NAME = "algorithm";
    private static final String OUTPUT_LINE_SEPARATOR = "\n";
    private static final String COMPOSE_OUTPUT_LINE = "docker compose run --rm algorithm";
    private static final String ERROR_OUTPUT_LINE =
            "Error while parsing/running algorithm: java.lang.IllegalArgumentException: bad input";
    private static final String STACK_TRACE_OUTPUT_LINE = "at com.ull.Main.main(Main.java:30)";
    private static final String NON_FINAL_JSON_PAYLOAD = "{\"not\":\"the final payload\"}";
    private static final String RANDOM_LOG_OUTPUT_LINE = "some random log line";
    private static final String JSON_OPEN_BRACE = "{";
    private static final String JSON_STATUS_LINE = "  \"status\": \"SUBOPTIMAL\",";
    private static final String JSON_CLUSTERS_LINE = "  \"clusters\": [],";
    private static final String JSON_DAILY_PLANS_LINE = "  \"dailyPlans\": []";
    private static final String JSON_CLOSE_BRACE = "}";
    private static final String EXTRACT_JSON_PAYLOAD_METHOD = "extractJsonPayload";
    private static final String EXPECTED_STATUS_FRAGMENT = "\"status\": \"SUBOPTIMAL\"";
    private static final String EXPECTED_DAILY_PLANS_FRAGMENT = "\"dailyPlans\": []";
    private static final String PLAIN_LOG_OUTPUT = "plain logs without json";
    private static final String EMPTY_JSON_PAYLOAD = "";

    /**
     * Verifies that the extractor returns the last valid JSON object from mixed docker output.
     */
    @Test
    void extractJsonPayloadShouldReturnLastValidJsonObjectFromMixedOutput() throws Exception {
        AlgorithmDockerRunner runner = new AlgorithmDockerRunner(PROJECT_DIRECTORY, COMPOSE_FILE, SERVICE_NAME);
        String mixedOutput = String.join(OUTPUT_LINE_SEPARATOR,
                COMPOSE_OUTPUT_LINE,
                ERROR_OUTPUT_LINE,
                STACK_TRACE_OUTPUT_LINE,
                NON_FINAL_JSON_PAYLOAD,
                RANDOM_LOG_OUTPUT_LINE,
                JSON_OPEN_BRACE,
                JSON_STATUS_LINE,
                JSON_CLUSTERS_LINE,
                JSON_DAILY_PLANS_LINE,
                JSON_CLOSE_BRACE);
        Method extractMethod = AlgorithmDockerRunner.class.getDeclaredMethod(
                EXTRACT_JSON_PAYLOAD_METHOD,
                String.class);
        extractMethod.setAccessible(true);
        String extracted = (String) extractMethod.invoke(runner, mixedOutput);
        assertTrue(extracted.contains(EXPECTED_STATUS_FRAGMENT));
        assertTrue(extracted.contains(EXPECTED_DAILY_PLANS_FRAGMENT));
    }

    /**
     * Verifies that the extractor returns an empty string when the output has no JSON payload.
     */
    @Test
    void extractJsonPayloadShouldReturnEmptyWhenNoJsonExists() throws Exception {
        AlgorithmDockerRunner runner = new AlgorithmDockerRunner(PROJECT_DIRECTORY, COMPOSE_FILE, SERVICE_NAME);
        Method extractMethod = AlgorithmDockerRunner.class.getDeclaredMethod(
                EXTRACT_JSON_PAYLOAD_METHOD,
                String.class);
        extractMethod.setAccessible(true);
        String extracted = (String) extractMethod.invoke(runner, PLAIN_LOG_OUTPUT);
        assertEquals(EMPTY_JSON_PAYLOAD, extracted);
    }
}
