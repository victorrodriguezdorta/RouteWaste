package com.ull;

import org.json.JSONObject;

import com.ull.algorithm.Algorithm;
import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.io.DeliveryPlanningProblemJsonFileSupplier;
import com.ull.io.DeliveryPlanningSolutionToJson;

/**
 * Punto de entrada de la aplicación. En este caso recibe como argumento el String con el JSON serializado,
 * después se pasa a un objeto interno a través del JSonFileSupplier y ese es el que se pasa al algoritmo
 * (clase Algorithm). Una vez se tiene la solución, se serializa a JSON (con el ToJson) y se devuelve el
 * JSON serializado.
 */
public class Main {

    public static void main(String[] args) {
        String problemString = args[0]; // The serialized problem string
        JSONObject jsonInstance = new JSONObject(problemString);
        
        // DeliveryPlanningProblem optimizationProblem = new DeliveryPlanningProblemJsonFileSupplier().get(jsonInstance).findFirst().get();
        // Algorithm solver = new Algorithm(optimizationProblem);
        // DeliveryPlanningSolution solution = solver.run();
        // JSONObject output = new DeliveryPlanningSolutionToJson().apply(solution);
        
        // System.out.println(output.toString(4));

        // Return input JSON directly
        System.out.println(jsonInstance.toString(4));
    }
}