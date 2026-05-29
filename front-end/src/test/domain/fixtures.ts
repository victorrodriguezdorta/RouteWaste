import { Container } from '@/domain/entity/container';
import { Facility } from '@/domain/entity/facility';
import { InfrastructurePlan } from '@/domain/entity/infrastructure-plan';
import { Vehicle } from '@/domain/entity/vehicle';
import { ContainerStatus } from '@/domain/enumerate/container-status';
import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { ServiceZone } from '@/domain/enumerate/service-zone';
import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { WasteType } from '@/domain/enumerate/waste-type';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { PlanningPeriod } from '@/domain/valueobject/time/planning-period';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

export function santaCruzLocation(): Location {
  return new Location(28.4636, -16.2518, 'Santa Cruz de Tenerife', 'SC-001');
}

export function laLagunaLocation(): Location {
  return new Location(28.4874, -16.3159, 'San Cristobal de La Laguna', 'LL-001');
}

export function sampleName(value = 'Test entity'): Name {
  return new Name(value);
}

export function sampleContainer(overrides?: {
  name?: Name;
  location?: Location;
  wasteType?: WasteType;
  capacityLiters?: ContainerCapacityLiters;
  dailyDemand?: DailyWasteDemandLitersPerDay;
  serviceZone?: ServiceZone | null;
  id?: UllUUID;
}): Container {
  return new Container(
    overrides?.name ?? sampleName('Container A'),
    overrides?.location ?? santaCruzLocation(),
    overrides?.wasteType ?? WasteType.ORGANIC,
    overrides?.capacityLiters ?? new ContainerCapacityLiters(3200),
    overrides?.dailyDemand ?? new DailyWasteDemandLitersPerDay(180),
    overrides?.serviceZone ?? ServiceZone.NEIGHBORHOOD,
    overrides?.id,
  );
}

export function sampleFacility(overrides?: {
  name?: Name;
  facilityType?: FacilityType;
  location?: Location;
  status?: FacilityStatus;
  id?: UllUUID;
}): Facility {
  return new Facility(
    overrides?.name ?? sampleName('Facility A'),
    overrides?.facilityType ?? FacilityType.TRANSFER_STATION,
    overrides?.location ?? santaCruzLocation(),
    new StorageCapacityKilograms(1500),
    new ProcessingCapacityKilogramsPerDay(600),
    new UnloadingTime(30),
    new OpeningFixedCost(250),
    overrides?.status ?? FacilityStatus.OPEN,
    overrides?.id,
  );
}

export function sampleVehicle(overrides?: {
  name?: Name;
  vehicleType?: VehicleType;
  id?: UllUUID;
}): Vehicle {
  return new Vehicle(
    overrides?.name ?? sampleName('Vehicle A'),
    overrides?.vehicleType ?? VehicleType.COLLECTION_TRUCK,
    new VehicleCapacityKilograms(5000),
    new VehicleCapacityLiters(12000),
    new TransportationVariableCost(1.25),
    overrides?.id,
  );
}

export function sampleInfrastructurePlan(overrides?: {
  period?: PlanningPeriod;
  maxBudget?: MaximumBudget;
  id?: UllUUID;
}): InfrastructurePlan {
  return new InfrastructurePlan(
    overrides?.period ?? new PlanningPeriod('2026'),
    overrides?.maxBudget ?? new MaximumBudget(100_000),
    null,
    overrides?.id,
  );
}

export function sampleContainerDailyStateInput() {
  return {
    containerId: 'container-1',
    planDay: 1,
    dailyFillingLiters: 45,
    containerCapacityLiters: new ContainerCapacityLiters(100),
    dailyDemandLitersPerDay: new DailyWasteDemandLitersPerDay(15),
    status: ContainerStatus.CORRECT,
  };
}
