import { describe, expect, it } from 'vitest';
import { Container } from '@/domain/entity/container';
import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { laLagunaLocation, sampleContainer, sampleName, santaCruzLocation } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('Container', () => {
  it('should create container with required attributes', () => {
    const container = sampleContainer();

    expect(container.getId()).toBeDefined();
    expect(container.getName().getValue()).toBe('Container A');
    expect(container.getLocation()).toEqual(santaCruzLocation());
    expect(container.getWasteType()).toBe(WasteType.ORGANIC);
    expect(container.getCapacityLiters().getLiters()).toBe(3200);
    expect(container.getDailyDemandLitersPerDay().getLitersPerDay()).toBe(180);
    expect(container.getServiceZone()).toBe(ServiceZone.NEIGHBORHOOD);
  });

  it('should update container attributes', () => {
    const container = sampleContainer();

    container.updateName(sampleName('Updated'));
    container.updateLocation(laLagunaLocation());
    container.updateWasteType(WasteType.PACKAGING);
    container.updateCapacityLiters(new ContainerCapacityLiters(3500));
    container.updateDailyDemandLitersPerDay(new DailyWasteDemandLitersPerDay(220));
    container.updateServiceZone(ServiceZone.DISTRICT);

    expect(container.getName().getValue()).toBe('Updated');
    expect(container.getLocation()).toEqual(laLagunaLocation());
    expect(container.getWasteType()).toBe(WasteType.PACKAGING);
    expect(container.getCapacityLiters().getLiters()).toBe(3500);
    expect(container.getDailyDemandLitersPerDay().getLitersPerDay()).toBe(220);
    expect(container.getServiceZone()).toBe(ServiceZone.DISTRICT);
  });

  it('should compare equality by id', () => {
    const id = UllUUID.random();
    const first = sampleContainer({ id });
    const second = sampleContainer({ id });
    const other = sampleContainer();

    expect(first.equals(second)).toBe(true);
    expect(first.equals(other)).toBe(false);
  });

  it('should reject invalid constructor data', () => {
    const location = santaCruzLocation();
    const capacity = new ContainerCapacityLiters(100);
    const demand = new DailyWasteDemandLitersPerDay(10);

    expect(() => new Container(null as never, location, WasteType.ORGANIC, capacity, demand)).toThrow('Container name is not defined');
    expect(() => new Container(sampleName(), null as never, WasteType.ORGANIC, capacity, demand)).toThrow('Container location is not defined');
    expect(() => new Container(sampleName(), location, null as never, capacity, demand)).toThrow('Waste type is not defined');
    expect(() => new Container(sampleName(), location, WasteType.ORGANIC, null as never, demand)).toThrow('Container capacity in liters is not defined');
    expect(() => new Container(sampleName(), location, WasteType.ORGANIC, capacity, null as never)).toThrow('Daily waste demand in liters is not defined');
  });
});
