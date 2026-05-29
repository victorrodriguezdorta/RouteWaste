import { describe, expect, it } from 'vitest';
import { Stop } from '@/domain/entity/stop';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';
import { sampleContainer } from '../fixtures';

describe('Stop', () => {
  it('should create stop with metrics', () => {
    const container = sampleContainer();
    const stop = new Stop(
      RouteSequence.of(1),
      container,
      CollectedWeightKilograms.fromKilograms(12),
      CollectedVolumeLiters.fromLiters(30),
      Distance.fromMeters(100),
      Distance.fromMeters(500),
      25,
      [{ type: 'OVERFLOW', message: 'High fill' }],
    );

    expect(stop.getSequence().getSequence()).toBe(1);
    expect(stop.getContainer()).toBe(container);
    expect(stop.getCollectedKilograms().getKilograms()).toBe(12);
    expect(stop.getCollectedLiters().getLiters()).toBe(30);
    expect(stop.getDistanceFromPreviousMeters().toMeters()).toBe(100);
    expect(stop.getCumulativeDistanceMeters().toMeters()).toBe(500);
    expect(stop.getContainerActualLiters()).toBe(25);
    expect(stop.getAlerts()).toHaveLength(1);
  });

  it('should compare equality by sequence and container', () => {
    const container = sampleContainer();
    const base = () =>
      new Stop(
        RouteSequence.of(2),
        container,
        CollectedWeightKilograms.fromKilograms(1),
        CollectedVolumeLiters.fromLiters(1),
        Distance.fromMeters(0),
        Distance.fromMeters(0),
      );

    expect(base().equals(base())).toBe(true);
    expect(base().equals(new Stop(RouteSequence.of(3), container, CollectedWeightKilograms.fromKilograms(1), CollectedVolumeLiters.fromLiters(1), Distance.fromMeters(0), Distance.fromMeters(0)))).toBe(false);
  });

  it('should reject invalid constructor data', () => {
    const container = sampleContainer();
    const kg = CollectedWeightKilograms.fromKilograms(1);
    const l = CollectedVolumeLiters.fromLiters(1);
    const d = Distance.fromMeters(0);

    expect(() => new Stop(null as never, container, kg, l, d, d)).toThrow('Route sequence is not defined');
    expect(() => new Stop(RouteSequence.of(1), null as never, kg, l, d, d)).toThrow('Container is not defined');
  });
});
