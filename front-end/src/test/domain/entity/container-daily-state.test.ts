import { describe, expect, it } from 'vitest';
import { ContainerDailyState } from '@/domain/entity/container-daily-state';
import { ContainerStatus } from '@/domain/enumerate/container-status';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { sampleContainerDailyStateInput } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('ContainerDailyState', () => {
  it('should create container daily state', () => {
    const input = sampleContainerDailyStateInput();
    const state = new ContainerDailyState(
      undefined,
      input.containerId,
      input.planDay,
      input.dailyFillingLiters,
      input.containerCapacityLiters,
      input.dailyDemandLitersPerDay,
      input.status,
    );

    expect(state.getId()).toBeDefined();
    expect(state.getContainerId()).toBe('container-1');
    expect(state.getPlanDay()).toBe(1);
    expect(state.getDailyFillingLiters()).toBe(45);
    expect(state.getContainerCapacityLiters().getLiters()).toBe(100);
    expect(state.getDailyDemandLitersPerDay()?.getLitersPerDay()).toBe(15);
    expect(state.getStatus()).toBe(ContainerStatus.CORRECT);
  });

  it('should compare by id when both have ids', () => {
    const id = UllUUID.random();
    const input = sampleContainerDailyStateInput();
    const first = new ContainerDailyState(id, input.containerId, input.planDay, 10, input.containerCapacityLiters, undefined, input.status);
    const second = new ContainerDailyState(id, input.containerId, 99, 99, input.containerCapacityLiters, undefined, ContainerStatus.OVERFLOWED);

    expect(first.equals(second)).toBe(true);
  });

  it('should not be equal when auto-generated ids differ', () => {
    const input = sampleContainerDailyStateInput();
    const first = new ContainerDailyState(undefined, input.containerId, 2, 10, input.containerCapacityLiters, undefined, input.status);
    const second = new ContainerDailyState(undefined, input.containerId, 2, 99, input.containerCapacityLiters, undefined, ContainerStatus.OVERFLOWED);

    expect(first.equals(second)).toBe(false);
  });

  it('should reject invalid data', () => {
    const capacity = new ContainerCapacityLiters(100);
    const demand = new DailyWasteDemandLitersPerDay(5);

    expect(() => new ContainerDailyState(undefined, '', 1, 10, capacity, demand, ContainerStatus.CORRECT)).toThrow('containerId is required');
    expect(() => new ContainerDailyState(undefined, 'c1', 0, 10, capacity, demand, ContainerStatus.CORRECT)).toThrow('planDay must be >= 1');
    expect(() => new ContainerDailyState(undefined, 'c1', 1, -1, capacity, demand, ContainerStatus.CORRECT)).toThrow('dailyFillingLiters must be >= 0');
    expect(() => new ContainerDailyState(undefined, 'c1', 1, 10, null as never, demand, ContainerStatus.CORRECT)).toThrow('containerCapacityLiters is required');
    expect(() => new ContainerDailyState(undefined, 'c1', 1, 10, capacity, demand, null as never)).toThrow('status is required');
  });
});
