import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Read-only vehicle snapshot embedded in an infrastructure plan detail.
 */
export class InfrastructurePlanVehicleDetail {
  constructor(
    public readonly id: UllUUID,
    public readonly name: Name | null = null,
    public readonly vehicleType: VehicleType | null = null,
    public readonly capacityKilograms: VehicleCapacityKilograms | null = null,
    public readonly capacityLiters: VehicleCapacityLiters | null = null,
    public readonly costPerKilometer: TransportationVariableCost | null = null,
  ) {}
}
