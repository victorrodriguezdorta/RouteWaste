import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Read-only container snapshot embedded in an infrastructure plan detail.
 */
export class InfrastructurePlanContainerDetail {
  constructor(
    public readonly id: UllUUID,
    public readonly name: Name,
    public readonly location: Location,
    public readonly wasteType: WasteType,
    public readonly capacityLiters: ContainerCapacityLiters,
    public readonly dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay,
    public readonly serviceZone: ServiceZone | null = null,
  ) {}
}
