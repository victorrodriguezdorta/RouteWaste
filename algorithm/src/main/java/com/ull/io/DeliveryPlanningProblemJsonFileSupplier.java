package com.ull.io;

import com.ull.domain.DeliveryPlanningProblem;
import org.json.JSONObject;
import java.util.stream.Stream;

/**
 * Clase que traduce el JSON a una clase del dominio del problema (en este caso,
 * DeliveryPlanningProblem)
 */
public class DeliveryPlanningProblemJsonFileSupplier {

    public Stream<DeliveryPlanningProblem> get(JSONObject json) {
        DeliveryPlanningProblem problem = new DeliveryPlanningProblem();
        
        // Si el JSON viene directamente con orders y trucks
        if (json.has("orders") && json.has("trucks")) {
            problem.setData(json);
        } 
        // Si el JSON viene envuelto en otra estructura
        else if (json.has("problemData")) {
            problem.setData(json.getJSONObject("problemData"));
        }
        // Si el JSON viene en el formato antiguo
        else if (json.has("jsonArrayTest")) {
            JSONObject convertedData = new JSONObject();
            convertedData.put("orders", json.getJSONArray("jsonArrayTest"));
            problem.setData(convertedData);
        }
        else {
            throw new IllegalArgumentException("Invalid JSON format for DeliveryPlanningProblem");
        }
        
        return Stream.of(problem);
    }
}
