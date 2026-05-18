package com.ull;

import com.ull.algorithm.Algorithm;
import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.io.DeliveryPlanningProblemJsonFileSupplier;
import com.ull.io.DeliveryPlanningSolutionToJson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/**
 * Entry point of the algorithm module.
 *
 * <p>Receives a serialized JSON string as its first argument or from stdin,
 * parses it into a fully typed {@link DeliveryPlanningProblem}, runs the 
 * algorithm and prints the JSON result to stdout so the backend can capture it.
 * 
 * <p>When JSON size exceeds command-line argument limits, the backend sends
 * the JSON through stdin instead. This method supports both:
 * 1. args[0] - JSON as command line argument (for small payloads)
 * 2. stdin - JSON from standard input (for large payloads)
 */
public class Main {

  private static final int FIRST_ARGUMENT_INDEX = 0;
  private static final int JSON_PREVIEW_MAX_LENGTH = 2000;
  private static final String ALGORITHM_EXECUTION_FAILED = "Algorithm execution failed";
  private static final String ERROR_READING_STDIN = "Error reading from stdin: ";
  private static final String ERROR_RUNNING_ALGORITHM = "Error while parsing/running algorithm: ";
  private static final String INPUT_JSON_PREVIEW = "Input JSON preview (truncated):\n";
  private static final String NO_JSON_PROBLEM_PROVIDED =
      "No JSON problem provided. Supply either as command-line argument or via stdin";
  private static final String PARSE_PROBLEM_FAILED = "Could not parse the planning problem";
  private static final String TRUNCATED_SUFFIX = "... [truncated]";
  private static final String UTILITY_CLASS_INSTANTIATION =
      "This is a utility class and cannot be instantiated.";

  /**
   * Prevents instantiation of the application entry point.
   */
  private Main() {
    throw new UnsupportedOperationException(UTILITY_CLASS_INSTANTIATION);
  }

  /**
   * Runs the algorithm from a JSON payload supplied as an argument or stdin.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    String problemString = readProblemJson(args);
    JSONObject jsonInstance = new JSONObject(problemString);
    try {
      DeliveryPlanningProblem problem =
          new DeliveryPlanningProblemJsonFileSupplier()
              .get(jsonInstance)
              .findFirst()
              .orElseThrow(() -> new IllegalStateException(PARSE_PROBLEM_FAILED));
      Algorithm solver = new Algorithm(problem);
      DeliveryPlanningSolution solution = solver.run();
      JSONObject output = new DeliveryPlanningSolutionToJson().apply(solution);
      System.out.println(output.toString(4));
    } catch (Exception e) {
      try {
        System.err.println(ERROR_RUNNING_ALGORITHM + e.toString());
        e.printStackTrace(System.err);
        if (jsonInstance != null) {
          String jsonPreview = jsonInstance.toString();
          if (jsonPreview.length() > JSON_PREVIEW_MAX_LENGTH) {
            jsonPreview = jsonPreview.substring(FIRST_ARGUMENT_INDEX, JSON_PREVIEW_MAX_LENGTH) + TRUNCATED_SUFFIX;
          }
          System.err.println(INPUT_JSON_PREVIEW + jsonPreview);
        }
      } catch (Exception ex) {
      }
      throw new RuntimeException(ALGORITHM_EXECUTION_FAILED, e);
    }
  }

  /**
   * Reads the problem JSON from either command-line arguments or stdin.
   * 
   * <p>Priority order:
   * 1. If args is not empty, read from args[0] (legacy support for small payloads)
   * 2. Otherwise, read from stdin (for large payloads that exceed command-line limits)
   *
   * @param args command-line arguments
   * @return JSON string representing the delivery planning problem
   */
  private static String readProblemJson(String[] args) {
    if (args != null
        && args.length > FIRST_ARGUMENT_INDEX
        && args[FIRST_ARGUMENT_INDEX] != null
        && !args[FIRST_ARGUMENT_INDEX].isBlank()) {
      return args[FIRST_ARGUMENT_INDEX];
    }
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(System.in, StandardCharsets.UTF_8));
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
      String jsonFromStdin = builder.toString().trim();
      if (!jsonFromStdin.isBlank()) {
        return jsonFromStdin;
      }
    } catch (Exception e) {
      System.err.println(ERROR_READING_STDIN + e.getMessage());
    }
    throw new IllegalArgumentException(NO_JSON_PROBLEM_PROVIDED);
  }
}