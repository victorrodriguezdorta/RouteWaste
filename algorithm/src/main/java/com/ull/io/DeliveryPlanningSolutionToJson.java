package com.ull.io;

import com.ull.domain.DeliveryPlanningSolution;
import org.json.JSONObject;

/**
 * Clase que traduce el dominio de la solución (en este caso,
 * DeliveryPlanningSolution) a un JSON
 */
public class DeliveryPlanningSolutionToJson implements java.util.function.Function<DeliveryPlanningSolution, JSONObject> {

    public JSONObject apply(DeliveryPlanningSolution solution) {
        JSONObject jsonSolution = new JSONObject();
        
        // Add solution status
        jsonSolution.put("status", solution.getStatus().toString());
        
        // Add routes
        jsonSolution.put("routes", solution.getRoutes());
        
        // Add total distance
        jsonSolution.put("totalDistance", solution.getTotalDistance());
        
        return jsonSolution;
    }
}