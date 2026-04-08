package com.ull.domain;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clase que representa el dominio del problema. Se crea a partir del JSON que se recibe.
 */
public class DeliveryPlanningProblem {
	private JSONArray orders;
	private JSONArray trucks;

	public DeliveryPlanningProblem() {
		this.orders = new JSONArray();
		this.trucks = new JSONArray();
	}

	public void setData(JSONObject problemData) {
		if (problemData.has("orders")) {
			this.orders = problemData.getJSONArray("orders");
		}
		if (problemData.has("trucks")) {
			this.trucks = problemData.getJSONArray("trucks");
		}
	}

	public JSONArray getOrders() {
		return orders;
	}

	public JSONArray getTrucks() {
		return trucks;
	}

	public int getNumberOfOrders() {
		return orders.length();
	}

	public int getNumberOfTrucks() {
		return trucks.length();
	}
}