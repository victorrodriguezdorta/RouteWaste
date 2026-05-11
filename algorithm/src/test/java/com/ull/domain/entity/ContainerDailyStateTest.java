package com.ull.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ull.domain.enumerate.ContainerStatus;

class ContainerDailyStateTest {

  @Test
  void shouldCreateContainerDailyStateWithExternalStatusEnum() {
    ContainerDailyState state = new ContainerDailyState(
        "container-1",
        3,
        45.0,
        100.0,
        15.0,
        ContainerStatus.CORRECT);

    assertEquals("container-1", state.getContainerId());
    assertEquals(3, state.getPlanDay());
    assertEquals(45.0, state.getDailyFillingLiters());
    assertEquals(100.0, state.getContainerCapacityLiters());
    assertEquals(15.0, state.getDailyDemandLitersPerDay());
    assertEquals(ContainerStatus.CORRECT, state.getStatus());
    assertEquals(45.0, state.getFillPercentage());
  }

  @Test
  void shouldDefaultNullStatusToCorrect() {
    ContainerDailyState state = new ContainerDailyState(
        "container-2",
        1,
        120.0,
        100.0,
        10.0,
        null);

    assertEquals(ContainerStatus.CORRECT, state.getStatus());
    assertEquals(120.0, state.getFillPercentage());
  }

  @Test
  void shouldSupportDefaultDemandConstructor() {
    ContainerDailyState state = new ContainerDailyState("container-3", 2, 20.0, 80.0);

    assertEquals(0.0, state.getDailyDemandLitersPerDay());
    assertEquals(ContainerStatus.CORRECT, state.getStatus());
  }

  @Test
  void shouldCompareStatesByContainerAndPlanDay() {
    ContainerDailyState first = new ContainerDailyState(
        "container-4",
        5,
        60.0,
        100.0,
        12.0,
        ContainerStatus.OVERFLOWED);

    ContainerDailyState second = new ContainerDailyState(
        "container-4",
        5,
        10.0,
        100.0,
        2.0,
        ContainerStatus.CORRECT);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldIncludeDemandAndStatusInToString() {
    ContainerDailyState state = new ContainerDailyState(
        "container-5",
        4,
        75.5,
        150.0,
        18.0,
        ContainerStatus.OVERFLOWED);

    String value = state.toString();

    assertTrue(value.contains("container-5"));
    assertTrue(value.contains("planDay=4"));
    assertTrue(value.contains("dailyDemandLitersPerDay=18"));
    assertTrue(value.contains("status=OVERFLOWED"));
  }

  @Test
  void shouldRejectInvalidContainerDailyStateData() {
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState(null, 1, 10.0, 100.0, 5.0, ContainerStatus.CORRECT));
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState(" ", 1, 10.0, 100.0, 5.0, ContainerStatus.CORRECT));
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState("container-6", 0, 10.0, 100.0, 5.0, ContainerStatus.CORRECT));
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState("container-6", 1, -1.0, 100.0, 5.0, ContainerStatus.CORRECT));
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState("container-6", 1, 10.0, 0.0, 5.0, ContainerStatus.CORRECT));
    assertThrows(IllegalArgumentException.class,
        () -> new ContainerDailyState("container-6", 1, 10.0, 100.0, -1.0, ContainerStatus.CORRECT));
  }
}