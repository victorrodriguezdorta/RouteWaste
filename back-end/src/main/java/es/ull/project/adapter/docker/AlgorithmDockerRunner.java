package es.ull.project.adapter.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.application.port.algorithm.AlgorithmRunner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AlgorithmDockerRunner
 *
 * Docker-based adapter that builds and runs the algorithm container through
 * docker compose and returns the JSON printed by the process.
 */
@Component
public class AlgorithmDockerRunner implements AlgorithmRunner {

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmDockerRunner.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final int PROCESS_SUCCESS_EXIT_CODE = 0;
    private static final int DEPTH_ZERO = 0;
    private static final int INDEX_NOT_FOUND = -1;
    private static final String CMD_DOCKER = "docker";
    private static final String CMD_COMPOSE = "compose";
    private static final String CMD_FLAG_FILE = "-f";
    private static final String CMD_RUN = "run";
    private static final String CMD_RM = "--rm";
    private static final String CMD_BUILD = "build";
    private static final String ERR_NO_JSON_PAYLOAD = "The algorithm process did not return a JSON payload";
    private static final String ERR_DOCKER_COMMAND_FAILED = "Failed to execute the docker command";
    private static final String ERR_DOCKER_INTERRUPTED = "The docker command execution was interrupted";

    private final String projectDirectory;
    private final String composeFile;
    private final String serviceName;

    /**
     * Creates a new docker runner with the configured paths.
     *
     * @param projectDirectory base directory where docker compose is located
     * @param composeFile compose file name or relative path
     * @param serviceName compose service used to run the algorithm
     */
    public AlgorithmDockerRunner(
            @Value("${algorithm.docker.project-directory:..}") String projectDirectory,
            @Value("${algorithm.docker.compose-file:docker-compose.yml}") String composeFile,
            @Value("${algorithm.docker.service-name:algorithm}") String serviceName) {
        this.projectDirectory = projectDirectory;
        this.composeFile = composeFile;
        this.serviceName = serviceName;
    }

    /**
     * Builds the algorithm image and runs it with the provided JSON payload.
     *
     * @param processedJson JSON payload to send to the algorithm
     * @return raw JSON returned by the container process
     */
    @Override
    public String run(String processedJson) {
        this.buildAlgorithmImage();
        logger.info("=== ALGORITHM EXECUTION START ===");
        logger.info("JSON payload size: {} bytes", processedJson.length());
        logger.debug("JSON payload being sent to algorithm:\n{}", processedJson);
        CommandResult commandResult = this.executeCommandWithStdin(List.of(
                CMD_DOCKER,
                CMD_COMPOSE,
                CMD_FLAG_FILE,
                this.composeFile,
                CMD_RUN,
                CMD_RM,
                this.serviceName),
                processedJson);
        String jsonOutput = this.extractJsonPayload(commandResult.standardOutput());
        logger.info("Algorithm response size: {} bytes", jsonOutput.length());
        logger.debug("Algorithm response received:\n{}", jsonOutput);
        logger.info("=== ALGORITHM EXECUTION END ===");
        if (jsonOutput.isBlank()) {
            throw new AlgorithmExecutionException(ERR_NO_JSON_PAYLOAD);
        }
        return jsonOutput;
    }

    /**
     * Builds the algorithm image before running the container.
     */
    private void buildAlgorithmImage() {
        this.executeCommand(List.of(
                CMD_DOCKER,
                CMD_COMPOSE,
                CMD_FLAG_FILE,
                this.composeFile,
                CMD_BUILD,
                this.serviceName));
    }

    /**
     * Executes a system command and captures stdout and stderr.
     *
     * @param command command to execute
     * @return captured command result
     */
    private CommandResult executeCommand(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(this.resolveProjectDirectory().toFile());
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            String standardOutput = this.readStream(process.getInputStream());
            int exitCode = process.waitFor();
            if (exitCode != PROCESS_SUCCESS_EXIT_CODE) {
                throw new AlgorithmExecutionException(
                        "Docker command failed with exit code " + exitCode + ": " + standardOutput);
            }
            return new CommandResult(standardOutput);
        } catch (IOException e) {
            throw new AlgorithmExecutionException(ERR_DOCKER_COMMAND_FAILED, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgorithmExecutionException(ERR_DOCKER_INTERRUPTED, e);
        }
    }

    /**
     * Executes a system command with input provided via stdin.
     * This method is used to send JSON payloads that may exceed command-line argument size limits.
     *
     * @param command command to execute (without the JSON payload)
     * @param stdinInput text to send to the process's standard input
     * @return captured command result
     */
    private CommandResult executeCommandWithStdin(List<String> command, String stdinInput) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(this.resolveProjectDirectory().toFile());
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            logger.debug("Docker process started");
            try (OutputStream stdin = process.getOutputStream()) {
                logger.info("Writing JSON to stdin ({} bytes)...", stdinInput.length());
                stdin.write(stdinInput.getBytes(StandardCharsets.UTF_8));
                stdin.flush();
                logger.info("JSON written to stdin successfully");
            }
            logger.info("Reading algorithm output...");
            String standardOutput = this.readStream(process.getInputStream());
            int exitCode = process.waitFor();
            logger.info("Docker process exited with code: {}", exitCode);
            if (exitCode != PROCESS_SUCCESS_EXIT_CODE) {
                logger.error("Docker command failed. Raw output:\n{}", standardOutput);
                throw new AlgorithmExecutionException(
                        "Docker command failed with exit code " + exitCode + ": " + standardOutput);
            }
            return new CommandResult(standardOutput);
        } catch (IOException e) {
            throw new AlgorithmExecutionException(ERR_DOCKER_COMMAND_FAILED, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgorithmExecutionException(ERR_DOCKER_INTERRUPTED, e);
        }
    }

    /**
     * Resolves the docker compose project directory.
     *
     * @return absolute path to the project directory
     */
    private Path resolveProjectDirectory() {
        return Paths.get(this.projectDirectory).toAbsolutePath().normalize();
    }

    /**
     * Reads the complete contents of a process stream.
     *
     * @param inputStream stream to read
     * @return stream contents as a string
     * @throws IOException if the stream cannot be read
     */
    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                if (!builder.isEmpty()) {
                    builder.append(System.lineSeparator());
                }
                builder.append(line);
                line = reader.readLine();
            }
            return builder.toString();
        }
    }

    /**
     * Extracts the JSON payload from the docker output.
     *
     * @param output raw command stdout
     * @return extracted JSON payload or an empty string if none is found
     */
    private String extractJsonPayload(String output) {
        String trimmedOutput = output == null ? "" : output.trim();
        if (trimmedOutput.isBlank()) {
            return "";
        }
        String lastValidObject = this.extractLastValidJsonObject(trimmedOutput);
        if (!lastValidObject.isBlank()) {
            return lastValidObject;
        }
        String lastValidArray = this.extractLastValidJsonArray(trimmedOutput);
        if (!lastValidArray.isBlank()) {
            return lastValidArray;
        }
        return "";
    }

    /**
     * Extracts the last valid JSON object found in the provided text.
     *
     * @param text text that may contain one or more JSON objects
     * @return last valid JSON object, or an empty string if none is found
     */
    private String extractLastValidJsonObject(String text) {
        int depth = DEPTH_ZERO;
        int start = INDEX_NOT_FOUND;
        boolean inString = false;
        boolean escaped = false;
        String lastValid = "";
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (character == '\\') {
                escaped = inString;
                continue;
            }
            if (character == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (character == '{') {
                if (depth == DEPTH_ZERO) {
                    start = i;
                }
                depth++;
            } else if (character == '}') {
                if (depth > DEPTH_ZERO) {
                    depth--;
                    if (depth == DEPTH_ZERO && start != INDEX_NOT_FOUND) {
                        String candidate = text.substring(start, i + 1);
                        if (this.isValidJson(candidate)) {
                            lastValid = candidate;
                        }
                        start = INDEX_NOT_FOUND;
                    }
                }
            }
        }
        return lastValid;
    }

    /**
     * Extracts the last valid JSON array found in the provided text.
     *
     * @param text text that may contain one or more JSON arrays
     * @return last valid JSON array, or an empty string if none is found
     */
    private String extractLastValidJsonArray(String text) {
        int depth = DEPTH_ZERO;
        int start = INDEX_NOT_FOUND;
        boolean inString = false;
        boolean escaped = false;
        String lastValid = "";
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (character == '\\') {
                escaped = inString;
                continue;
            }
            if (character == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (character == '[') {
                if (depth == DEPTH_ZERO) {
                    start = i;
                }
                depth++;
            } else if (character == ']') {
                if (depth > DEPTH_ZERO) {
                    depth--;
                    if (depth == DEPTH_ZERO && start != INDEX_NOT_FOUND) {
                        String candidate = text.substring(start, i + 1);
                        if (this.isValidJson(candidate)) {
                            lastValid = candidate;
                        }
                        start = INDEX_NOT_FOUND;
                    }
                }
            }
        }
        return lastValid;
    }

    /**
     * Checks whether the provided text is valid JSON.
     *
     * @param text text to validate
     * @return true if the text can be parsed as JSON, false otherwise
     */
    private boolean isValidJson(String text) {
        try {
            OBJECT_MAPPER.readTree(text);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
