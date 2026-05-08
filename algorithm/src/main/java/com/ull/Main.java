package com.ull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.ull.algorithm.Algorithm;
import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.io.DeliveryPlanningProblemJsonFileSupplier;
import com.ull.io.DeliveryPlanningSolutionToJson;

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

  public static void main(String[] args) {
    String problemString = readProblemJson(args);
    JSONObject jsonInstance = new JSONObject(problemString);

    try {
      DeliveryPlanningProblem problem =
          new DeliveryPlanningProblemJsonFileSupplier()
              .get(jsonInstance)
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("Could not parse the planning problem"));

      Algorithm solver = new Algorithm(problem);
      DeliveryPlanningSolution solution = solver.run();

      JSONObject output = new DeliveryPlanningSolutionToJson().apply(solution);
      System.out.println(output.toString(4));
    } catch (Exception e) {
      // Print diagnostic info to stderr to help debugging when large JSON fails
      try {
        System.err.println("Error while parsing/running algorithm: " + e.toString());
        e.printStackTrace(System.err);
        // Also print a truncated preview of the input JSON to help identify format issues
        if (jsonInstance != null) {
          String jsonPreview = jsonInstance.toString();
          if (jsonPreview.length() > 2000) {
            jsonPreview = jsonPreview.substring(0, 2000) + "... [truncated]";
          }
          System.err.println("Input JSON preview (truncated):\n" + jsonPreview);
        }
      } catch (Exception ex) {
        // Ignore secondary errors when trying to log diagnostics
      }

      // Propagate failure to caller instead of returning an empty successful-like payload.
      throw new RuntimeException("Algorithm execution failed", e);
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
    // If JSON provided as command-line argument, use it (backward compatibility)
    if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
      return args[0];
    }

    // Otherwise, read from stdin (for large payloads)
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
      System.err.println("Error reading from stdin: " + e.getMessage());
    }

    throw new IllegalArgumentException(
        "No JSON problem provided. Supply either as command-line argument or via stdin");
  }
}