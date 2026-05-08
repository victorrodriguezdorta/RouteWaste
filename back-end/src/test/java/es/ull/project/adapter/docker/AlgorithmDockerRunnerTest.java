package es.ull.project.adapter.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class AlgorithmDockerRunnerTest {

    @Test
    void extractJsonPayload_shouldReturnLastValidJsonObjectFromMixedOutput() throws Exception {
        AlgorithmDockerRunner runner = new AlgorithmDockerRunner("..", "docker-compose.yml", "algorithm");

        String mixedOutput = String.join("\n",
                "docker compose run --rm algorithm",
                "Error while parsing/running algorithm: java.lang.IllegalArgumentException: bad input",
                "at com.ull.Main.main(Main.java:30)",
                "{\"not\":\"the final payload\"}",
                "some random log line",
                "{",
                "  \"status\": \"SUBOPTIMAL\",",
                "  \"clusters\": [],",
                "  \"dailyPlans\": []",
                "}");

        Method extractMethod = AlgorithmDockerRunner.class.getDeclaredMethod("extractJsonPayload", String.class);
        extractMethod.setAccessible(true);

        String extracted = (String) extractMethod.invoke(runner, mixedOutput);

        assertTrue(extracted.contains("\"status\": \"SUBOPTIMAL\""));
        assertTrue(extracted.contains("\"dailyPlans\": []"));
    }

    @Test
    void extractJsonPayload_shouldReturnEmptyWhenNoJsonExists() throws Exception {
        AlgorithmDockerRunner runner = new AlgorithmDockerRunner("..", "docker-compose.yml", "algorithm");

        Method extractMethod = AlgorithmDockerRunner.class.getDeclaredMethod("extractJsonPayload", String.class);
        extractMethod.setAccessible(true);

        String extracted = (String) extractMethod.invoke(runner, "plain logs without json");

        assertEquals("", extracted);
    }
}
