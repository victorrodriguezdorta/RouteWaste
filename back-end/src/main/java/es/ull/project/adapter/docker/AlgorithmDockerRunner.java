package es.ull.project.adapter.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.application.port.algorithm.AlgorithmRunner;

/**
 * AlgorithmDockerRunner
 *
 * Docker-based adapter that builds and runs the algorithm container through
 * docker compose and returns the JSON printed by the process.
 */
@Component
public class AlgorithmDockerRunner implements AlgorithmRunner {

    private static final int PROCESS_SUCCESS_EXIT_CODE = 0;

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

        CommandResult commandResult = this.executeCommand(List.of(
                "docker",
                "compose",
                "-f",
                this.composeFile,
                "run",
                "--rm",
                this.serviceName,
                processedJson));

        String jsonOutput = this.extractJsonPayload(commandResult.standardOutput());
        if (jsonOutput.isBlank()) {
            throw new AlgorithmExecutionException("The algorithm process did not return a JSON payload");
        }
        return jsonOutput;
    }

    /**
     * Builds the algorithm image before running the container.
     */
    private void buildAlgorithmImage() {
        this.executeCommand(List.of(
                "docker",
                "compose",
                "-f",
                this.composeFile,
                "build",
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
            throw new AlgorithmExecutionException("Failed to execute the docker command", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgorithmExecutionException("The docker command execution was interrupted", e);
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

        int objectStart = trimmedOutput.indexOf('{');
        int objectEnd = trimmedOutput.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            return trimmedOutput.substring(objectStart, objectEnd + 1);
        }

        int arrayStart = trimmedOutput.indexOf('[');
        int arrayEnd = trimmedOutput.lastIndexOf(']');
        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            return trimmedOutput.substring(arrayStart, arrayEnd + 1);
        }

        return "";
    }

    /**
     * CommandResult
     *
     * Simple immutable container for process outputs.
     */
    private record CommandResult(String standardOutput) {
    }
}
