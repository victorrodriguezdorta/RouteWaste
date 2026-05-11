package es.ull.project.adapter.rest.response.infrastructureplan;

import java.util.UUID;

/**
 * DTO for a single container daily state entry in infrastructure plan responses.
 */
public class ContainerDailyStateResponseBody {

    public UUID id;
    public String containerId;
    public Integer planDay;
    public Double dailyFillingLiters;
    public Double containerCapacityLiters;
    public Double dailyDemandLitersPerDay;
    public String status;
}
