package es.ull.project.adapter.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

class InfrastructurePlanResponseMapperTests {

    @Test
    void toResponseBody_includesContainerStateMonitoring() {
        InfrastructurePlan plan = new InfrastructurePlan(
            new PlanningPeriod("2026"),
            new MaximumBudget(1000.0),
            null,
            null,
            null,
            null
        );

        ContainerDailyState state = new ContainerDailyState(
            UUID.randomUUID(),
            "2dd7627e-f357-42e1-b257-2cf1160440d3",
            2,
            850.0,
            1000.0,
            100.0,
            ContainerStatus.CORRECT
        );
        plan.addContainerDailyState(state);

        InfrastructurePlanResponseBody response = InfrastructurePlanResponseMapper.toResponseBody(plan, List.of());

        assertNotNull(response);
        assertNotNull(response.containerStateMonitoring);
        assertEquals(1, response.containerStateMonitoring.size());
        assertEquals(state.getId(), response.containerStateMonitoring.get(0).id);
        assertEquals(state.getContainerId(), response.containerStateMonitoring.get(0).containerId);
        assertEquals(state.getPlanDay(), response.containerStateMonitoring.get(0).planDay);
        assertEquals(state.getDailyFillingLiters(), response.containerStateMonitoring.get(0).dailyFillingLiters);
        assertEquals(state.getContainerCapacityLiters(), response.containerStateMonitoring.get(0).containerCapacityLiters);
        assertEquals(state.getDailyDemandLitersPerDay(), response.containerStateMonitoring.get(0).dailyDemandLitersPerDay);
        assertEquals(state.getStatus().name(), response.containerStateMonitoring.get(0).status);
    }
}
