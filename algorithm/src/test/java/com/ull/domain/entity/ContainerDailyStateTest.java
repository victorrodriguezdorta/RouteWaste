package com.ull.domain.entity;

import com.ull.domain.enumerate.ContainerStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ContainerDailyStateTest {

  private static final String CONTAINER_1_ID = "container-1";
  private static final String CONTAINER_5_ID = "container-5";
  private static final String PLAN_DAY_4_FRAGMENT = "planDay=4";
  private static final String DAILY_DEMAND_18_FRAGMENT = "dailyDemandLitersPerDay=18";
  private static final String STATUS_OVERFLOWED_FRAGMENT = "status=OVERFLOWED";

  /**
   * Tests that fill levels before and after collection are tracked correctly.
   */
  @Test
  void shouldTrackFillLevelsBeforeAndAfterCollection() {
    ContainerDailyState state = new ContainerDailyState(
        "container-before-after",
        1,
        0.0,
        80.0,
        100.0,
        20.0,
        ContainerStatus.CORRECT);
    assertEquals(0.0, state.getDailyFillingLiters());
    assertEquals(80.0, state.getDailyFillingLitersBeforeCollection());
    assertEquals(0.0, state.getFillPercentage());
    assertEquals(80.0, state.getFillPercentageBeforeCollection());
  }

  /**
   * Tests container daily state creation with an external status enum.
   */
  @Test
  void shouldCreateContainerDailyStateWithExternalStatusEnum() {
    ContainerDailyState state = new ContainerDailyState(
        CONTAINER_1_ID,
        3,
        45.0,
        100.0,
        15.0,
        ContainerStatus.CORRECT);
    assertEquals(CONTAINER_1_ID, state.getContainerId());
    assertEquals(3, state.getPlanDay());
    assertEquals(45.0, state.getDailyFillingLiters());
    assertEquals(100.0, state.getContainerCapacityLiters());
    assertEquals(15.0, state.getDailyDemandLitersPerDay());
    assertEquals(ContainerStatus.CORRECT, state.getStatus());
    assertEquals(45.0, state.getFillPercentage());
  }

  /**
   * Tests that a null status defaults to correct.
   */
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

  /**
   * Tests the default demand constructor.
   */
  @Test
  void shouldSupportDefaultDemandConstructor() {
    ContainerDailyState state = new ContainerDailyState("container-3", 2, 20.0, 80.0);
    assertEquals(0.0, state.getDailyDemandLitersPerDay());
    assertEquals(ContainerStatus.CORRECT, state.getStatus());
  }

  /**
   * Tests that states are compared by container identifier and plan day.
   */
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

  /**
   * Tests that demand and status are included in the string representation.
   */
  @Test
  void shouldIncludeDemandAndStatusInToString() {
    ContainerDailyState state = new ContainerDailyState(
        CONTAINER_5_ID,
        4,
        75.5,
        150.0,
        18.0,
        ContainerStatus.OVERFLOWED);
    String value = state.toString();
    assertTrue(value.contains(CONTAINER_5_ID));
    assertTrue(value.contains(PLAN_DAY_4_FRAGMENT));
    assertTrue(value.contains(DAILY_DEMAND_18_FRAGMENT));
    assertTrue(value.contains(STATUS_OVERFLOWED_FRAGMENT));
  }

  /**
   * Tests that invalid container daily state data is rejected.
   */
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
