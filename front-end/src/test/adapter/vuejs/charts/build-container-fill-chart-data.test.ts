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

describe('buildContainerFillChartData', () => {
  const containerId = new UllUUID('11111111-1111-1111-1111-111111111111');

  const container = new InfrastructurePlanContainerDetail(
    containerId,
    new Name('Container A'),
    new Location(28.47, -16.25, 'Test street', 'GIS-1'),
    WasteType.ORGANIC,
    new ContainerCapacityLiters(100),
    new DailyWasteDemandLitersPerDay(20),
    ServiceZone.DISTRICT,
  );

  it('builds before and after points for each day', () => {
    const monitoringStates = [
      new InfrastructurePlanContainerDailyStateDetail(
        null,
        containerId,
        1,
        0,
        80,
        new ContainerCapacityLiters(100),
        new DailyWasteDemandLitersPerDay(20),
        ContainerStatus.CORRECT,
      ),
      new InfrastructurePlanContainerDailyStateDetail(
        null,
        containerId,
        2,
        0,
        20,
        new ContainerCapacityLiters(100),
        new DailyWasteDemandLitersPerDay(20),
        ContainerStatus.CORRECT,
      ),
    ];

    const result = buildContainerFillChartData({
      containers: [container],
      monitoringStates,
      labelForContainer: (entry) => entry.name.getValue(),
    });

    expect(result.data).toHaveLength(4);
    expect(result.data[0].stop).toBeCloseTo(0.75);
    expect(result.data[0][containerId.getValue()]).toBe(80);
    expect(result.data[1].stop).toBeCloseTo(1.25);
    expect(result.data[1][containerId.getValue()]).toBe(0);
    expect(result.data[2].stop).toBeCloseTo(1.75);
    expect(result.data[2][containerId.getValue()]).toBe(20);
    expect(result.data[3].stop).toBeCloseTo(2.25);
    expect(result.data[3][containerId.getValue()]).toBe(0);
  });

  it('falls back to end-of-day liters when before-collection data is missing', () => {
    const monitoringStates = [
      new InfrastructurePlanContainerDailyStateDetail(
        null,
        containerId,
        1,
        35,
        null,
        new ContainerCapacityLiters(100),
        new DailyWasteDemandLitersPerDay(20),
        ContainerStatus.CORRECT,
      ),
    ];

    const result = buildContainerFillChartData({
      containers: [container],
      monitoringStates,
      labelForContainer: (entry) => entry.name.getValue(),
    });

    expect(result.data[0][containerId.getValue()]).toBe(35);
    expect(result.data[1][containerId.getValue()]).toBe(35);
  });
});
