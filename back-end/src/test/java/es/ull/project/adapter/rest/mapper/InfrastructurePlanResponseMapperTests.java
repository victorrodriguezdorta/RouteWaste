package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.PlanDay;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class InfrastructurePlanResponseMapperTests {

    /**
     * Verifies that response mapping includes container state monitoring entries.
     */
    @Test
    void toResponseBodyIncludesContainerStateMonitoring() {
        InfrastructurePlan plan = new InfrastructurePlan(
                new PlanningPeriod("2026"),
                null,
                new MaximumBudget(1000.0),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED
        );
        Container container = new Container(
                new Name("Test container"),
                new Location(28.1, -16.2, "Test address", "GIS-1"),
                WasteType.ORGANIC,
                new ContainerCapacityLiters(1000.0),
                new DailyWasteDemandLitersPerDay(100.0),
                ServiceZone.random());
        ContainerDailyState state = new ContainerDailyState(
                plan,
                container,
                new PlanDay(2),
                CollectedVolumeLiters.fromLiters(850.0),
                new ContainerCapacityLiters(1000.0),
                new DailyWasteDemandLitersPerDay(100.0),
                ContainerStatus.CORRECT
        );
        plan.addContainerDailyState(state);
        InfrastructurePlanResponseBody response = InfrastructurePlanResponseMapper.toResponseBody(plan, List.of());
        assertNotNull(response);
        assertNotNull(response.containerStateMonitoring);
        assertEquals(1, response.containerStateMonitoring.size());
        assertEquals(state.getId(), response.containerStateMonitoring.get(0).id);
        assertEquals(state.getContainerId(), response.containerStateMonitoring.get(0).containerId);
        assertEquals(state.getPlanDay(), response.containerStateMonitoring.get(0).planDay.getDay());
        assertEquals(state.getDailyFillingLiters(), response.containerStateMonitoring.get(0).dailyFillingLiters.getValue());
        assertEquals(state.getContainerCapacityLiters(), response.containerStateMonitoring.get(0).containerCapacityLiters.getLiters());
        assertEquals(state.getDailyDemandLitersPerDay(), response.containerStateMonitoring.get(0).dailyDemandLitersPerDay.getLitersPerDay());
        assertEquals(state.getStatus(), response.containerStateMonitoring.get(0).status);
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, response.executionState);
    }
}
