import { describe, expect, it } from 'vitest';
import { buildContainerFillChartData } from '@/adapter/vuejs/charts/container-fill/build-container-fill-chart-data';
import { ContainerStatus } from '@/domain/enumerate/container-status';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { Name } from '@/domain/valueobject/name/name';
import { InfrastructurePlanContainerDailyStateDetail } from '@/domain/read-model/infrastructure-plan-detail/details/infrastructure-plan-container-daily-state-detail';
import { InfrastructurePlanContainerDetail } from '@/domain/read-model/infrastructure-plan-detail/details/infrastructure-plan-container-detail';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';

const MINUTES_PER_DAY = 24 * 60;

function columnKey(day: number, hours: number, minutes: number): number {
  return day * MINUTES_PER_DAY + hours * 60 + minutes;
}

describe('buildContainerFillChartData', () => {
  const containerAId = new UllUUID('11111111-1111-1111-1111-111111111111');
  const containerBId = new UllUUID('22222222-2222-2222-2222-222222222222');

  function buildContainer(id: UllUUID, name: string): InfrastructurePlanContainerDetail {
    return new InfrastructurePlanContainerDetail(
      id,
      new Name(name),
      new Location(28.47, -16.25, 'Test street', 'GIS-1'),
      WasteType.ORGANIC,
      new ContainerCapacityLiters(100),
      new DailyWasteDemandLitersPerDay(20),
      ServiceZone.DISTRICT,
    );
  }

  function buildState(
    containerId: UllUUID,
    day: number,
    time: string,
    fillingLiters: number,
  ): InfrastructurePlanContainerDailyStateDetail {
    return new InfrastructurePlanContainerDailyStateDetail(
      null,
      containerId,
      day,
      time,
      fillingLiters,
      null,
      new ContainerCapacityLiters(100),
      new DailyWasteDemandLitersPerDay(20),
      ContainerStatus.CORRECT,
    );
  }

  const containerA = buildContainer(containerAId, 'Container A');
  const containerB = buildContainer(containerBId, 'Container B');

  it('builds a day+time timeline and carries forward fill levels between snapshots', () => {
    const monitoringStates = [
      buildState(containerAId, 1, '08:00', 80),
      buildState(containerBId, 1, '08:30', 50),
      buildState(containerAId, 1, '09:00', 0),
    ];

    const result = buildContainerFillChartData({
      containers: [containerA, containerB],
      monitoringStates,
      labelForContainer: (entry) => entry.name.getValue(),
    });

    expect(result.series).toHaveLength(2);
    expect(result.data).toHaveLength(3);

    expect(result.data[0].stop).toBe(columnKey(1, 8, 0));
    expect(result.data[0].stopLabel).toBe('D1 08:00');
    expect(result.data[0][containerAId.getValue()]).toBe(80);
    expect(result.data[0][containerBId.getValue()]).toBeUndefined();

    expect(result.data[1].stop).toBe(columnKey(1, 8, 30));
    expect(result.data[1].stopLabel).toBe('08:30');
    expect(result.data[1][containerAId.getValue()]).toBe(80);
    expect(result.data[1][containerBId.getValue()]).toBe(50);

    expect(result.data[2].stop).toBe(columnKey(1, 9, 0));
    expect(result.data[2].stopLabel).toBe('09:00');
    expect(result.data[2][containerAId.getValue()]).toBe(0);
    expect(result.data[2][containerBId.getValue()]).toBe(50);
  });

  it('prefixes the day on the first snapshot of each day', () => {
    const monitoringStates = [
      buildState(containerAId, 1, '08:00', 80),
      buildState(containerAId, 2, '08:00', 20),
    ];

    const result = buildContainerFillChartData({
      containers: [containerA],
      monitoringStates,
      labelForContainer: (entry) => entry.name.getValue(),
    });

    expect(result.data).toHaveLength(2);
    expect(result.data[0].stopLabel).toBe('D1 08:00');
    expect(result.data[1].stopLabel).toBe('D2 08:00');
    expect(result.data[0][containerAId.getValue()]).toBe(80);
    expect(result.data[1][containerAId.getValue()]).toBe(20);
  });
});
