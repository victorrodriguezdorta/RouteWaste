package com.ull;

import com.ull.algorithm.Algorithm;
import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.io.DeliveryPlanningProblemJsonFileSupplier;
import com.ull.io.DeliveryPlanningSolutionToJson;
import org.json.JSONObject;

/**
 * Entry point of the algorithm module.
 *
 * <p>Receives a serialized JSON string as its first argument, parses it into a
 * fully typed {@link DeliveryPlanningProblem}, runs the algorithm and prints
 * the JSON result to stdout so the backend can capture it.
 */
public class Main {

  public static void main(String[] args) {
    String problemString = args[0];
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
      DeliveryPlanningSolution solution = new DeliveryPlanningSolution();
      JSONObject output = new DeliveryPlanningSolutionToJson().apply(solution);
      System.out.println(output.toString(4));
    }
  }
}