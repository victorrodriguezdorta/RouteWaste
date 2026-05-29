import { describe, expect, it } from 'vitest';
import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { sampleName, sampleVehicle } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('Vehicle', () => {
  it('should create vehicle with required attributes', () => {
    const vehicle = sampleVehicle();

    expect(vehicle.getId()).toBeDefined();
    expect(vehicle.getName().getValue()).toBe('Vehicle A');
    expect(vehicle.getVehicleType()).toBe(VehicleType.COLLECTION_TRUCK);
    expect(vehicle.getCapacityKilograms().getKilograms()).toBe(5000);
    expect(vehicle.getCapacityLiters().getLiters()).toBe(12000);
    expect(vehicle.getCostPerKilometer().getAmount()).toBe(1.25);
  });

  it('should update vehicle attributes', () => {
    const vehicle = sampleVehicle();

    vehicle.updateName(sampleName('Updated vehicle'));
    vehicle.updateVehicleType(VehicleType.TRANSFER_TRUCK);
    vehicle.updateCapacityKilograms(new VehicleCapacityKilograms(6000));
    vehicle.updateCapacityLiters(new VehicleCapacityLiters(15000));
    vehicle.updateCostPerKilometer(new TransportationVariableCost(2));

    expect(vehicle.getName().getValue()).toBe('Updated vehicle');
    expect(vehicle.getVehicleType()).toBe(VehicleType.TRANSFER_TRUCK);
    expect(vehicle.getCapacityKilograms().getKilograms()).toBe(6000);
    expect(vehicle.getCapacityLiters().getLiters()).toBe(15000);
    expect(vehicle.getCostPerKilometer().getAmount()).toBe(2);
  });

  it('should compare equality by id', () => {
    const id = UllUUID.random();
    expect(sampleVehicle({ id }).equals(sampleVehicle({ id }))).toBe(true);
    expect(sampleVehicle().equals(sampleVehicle())).toBe(false);
  });
});
