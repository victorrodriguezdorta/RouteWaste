import { describe, expect, it } from 'vitest';
import { DailyPlan } from '@/domain/entity/daily-plan';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { sampleFacility, sampleInfrastructurePlan, sampleVehicle } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('DailyPlan', () => {
  it('should create daily plan and add stops', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();
    const vehicle = sampleVehicle();
    const dailyPlan = new DailyPlan(
      plan,
      facility,
      new Date('2026-06-01'),
      1,
      vehicle,
      CollectedWeightKilograms.fromKilograms(100),
      CollectedVolumeLiters.fromLiters(200),
      Distance.fromMeters(1500),
    );

    expect(dailyPlan.getId()).toBeDefined();
    expect(dailyPlan.getInfrastructurePlan()).toBe(plan);
    expect(dailyPlan.getFacility()).toBe(facility);
    expect(dailyPlan.getVehicle()).toBe(vehicle);
    expect(dailyPlan.getNumberOfStops()).toBe(0);

    expect(() => dailyPlan.addStop(undefined)).toThrow('Stop is invalid');
  });

  it('should compare equality by id', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();
    const vehicle = sampleVehicle();
    const id = UllUUID.random();
    const metrics = {
      kg: CollectedWeightKilograms.fromKilograms(0),
      l: CollectedVolumeLiters.fromLiters(0),
      d: Distance.fromMeters(0),
    };
    const create = () =>
      new DailyPlan(plan, facility, new Date('2026-06-01'), 1, vehicle, metrics.kg, metrics.l, metrics.d, [], id);

    expect(create().equals(create())).toBe(true);
    expect(create().equals(new DailyPlan(plan, facility, new Date('2026-06-01'), 1, vehicle, metrics.kg, metrics.l, metrics.d))).toBe(false);
  });

  it('should reject missing required references', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();
    const vehicle = sampleVehicle();
    const kg = CollectedWeightKilograms.fromKilograms(0);
    const l = CollectedVolumeLiters.fromLiters(0);
    const d = Distance.fromMeters(0);

    expect(() => new DailyPlan(null as never, facility, new Date(), 1, vehicle, kg, l, d)).toThrow('Infrastructure plan is not defined');
    expect(() => new DailyPlan(plan, null as never, new Date(), 1, vehicle, kg, l, d)).toThrow('Facility is not defined');
    expect(() => new DailyPlan(plan, facility, null as never, 1, vehicle, kg, l, d)).toThrow('Service date is not defined');
    expect(() => new DailyPlan(plan, facility, new Date(), 1, null as never, kg, l, d)).toThrow('Vehicle is not defined');
  });
});
