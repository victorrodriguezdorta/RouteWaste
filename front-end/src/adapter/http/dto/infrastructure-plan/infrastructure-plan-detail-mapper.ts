import type {
  ContainerDailyStateJsonResponse,
  InfrastructurePlanClusterJsonResponse,
  InfrastructurePlanDailyPlanJsonResponse,
  InfrastructurePlanDetailJsonResponse,
  InfrastructurePlanFacilityJsonResponse,
  InfrastructurePlanMoneyJsonResponse,
  InfrastructurePlanStopJsonResponse,
  StopAlertJsonResponse,
} from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-json-response';
import { containerStatusFromString } from '@/domain/enumerate/container-status';
import { infrastructurePlanValidityStateFromString } from '@/domain/enumerate/infrastructure-plan-validity-state';
import { facilityStatusFromString } from '@/domain/enumerate/facility-status';
import { facilityTypeFromString } from '@/domain/enumerate/facility-type';
import { serviceZoneFromString } from '@/domain/enumerate/service-zone';
import { vehicleTypeFromString } from '@/domain/enumerate/vehicle-type';
import { wasteTypeFromString } from '@/domain/enumerate/waste-type';
import { stopTypeFromString, StopType } from '@/domain/enumerate/stop-type';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { TotalCost } from '@/domain/valueobject/cost/total-cost';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Distance } from '@/domain/valueobject/location/distance';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';
import {
  InfrastructurePlanContainerDailyStateDetail,
  InfrastructurePlanContainerDetail,
  InfrastructurePlanDailyPlanDetail,
  InfrastructurePlanDetail,
  InfrastructurePlanFacilityDetail,
  InfrastructurePlanMetricsDetail,
  InfrastructurePlanStopAlertDetail,
  InfrastructurePlanStopDetail,
  InfrastructurePlanVehicleDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/** When the API omits `name`, the mapper uses these; the UI maps them to i18n (never IDs). */
export const infrastructurePlanDetailFallbackDisplayNames = {
  facility: 'Unnamed facility',
  container: 'Unnamed container',
} as const;

/**
 * Maps infrastructure plan HTTP responses into the read-only model consumed by the UI.
 */
export class InfrastructurePlanDetailMapper {
  /**
   * Convert the backend response into a normalized read-only model.
   */
  static toReadModel(data: InfrastructurePlanDetailJsonResponse): InfrastructurePlanDetail {
    const serviceDateToDayMap = this.buildServiceDateToDayMap(data);
    const facilities = this.mapFacilities(data, serviceDateToDayMap);
    const metrics = this.mapMetrics(data);
    const derivedNumberOfDays = facilities
      .flatMap((facility) => facility.dailyPlans)
      .reduce((max, dailyPlan) => Math.max(max, dailyPlan.planDay), 0);

    const validityState = infrastructurePlanValidityStateFromString(data.validityState);
    const executionRequestJson =
      typeof data.executionRequestJson === 'string' && data.executionRequestJson.length > 0
        ? data.executionRequestJson
        : null;

    return new InfrastructurePlanDetail(
      this.parseOptionalUuid(data.id),
      data.executedAt,
      this.extractPeriod(data),
      typeof data.numberOfDays === 'number' ? data.numberOfDays : derivedNumberOfDays || null,
      typeof data.status === 'string' && data.status.length > 0
        ? facilityStatusFromString(data.status)
        : facilities[0]?.status ?? null,
      metrics,
      facilities,
      (data.containerStateMonitoring ?? []).map((state) => this.mapContainerDailyState(state)),
      validityState,
      executionRequestJson,
    );
  }

  private static mapFacilities(
    data: InfrastructurePlanDetailJsonResponse,
    serviceDateToDayMap: Map<string, number>,
  ): InfrastructurePlanFacilityDetail[] {
    const topLevelDailyPlans = data.dailyPlans ?? [];

    if (Array.isArray(data.facilities) && data.facilities.length > 0) {
      return data.facilities.map((facility) =>
        this.mapFacility(
          facility,
          (facility.dailyPlans ?? []).length > 0
            ? facility.dailyPlans ?? []
            : topLevelDailyPlans.filter((dailyPlan) => this.extractFacilityId(dailyPlan) === facility.id),
          data.id ?? null,
          serviceDateToDayMap,
        ),
      );
    }

    if (Array.isArray(data.clusters) && data.clusters.length > 0) {
      return data.clusters.map((cluster) =>
        this.mapClusterFacility(cluster, topLevelDailyPlans, data.id ?? null, serviceDateToDayMap),
      );
    }

    return [];
  }

  private static mapClusterFacility(
    cluster: InfrastructurePlanClusterJsonResponse,
    topLevelDailyPlans: InfrastructurePlanDailyPlanJsonResponse[],
    infrastructurePlanId: string | null,
    serviceDateToDayMap: Map<string, number>,
  ): InfrastructurePlanFacilityDetail {
    const facility = cluster.facility;
    const facilityId = facility.id;

    return new InfrastructurePlanFacilityDetail(
      new UllUUID(facilityId),
      this.facilityNameFromJson(facility),
      facilityTypeFromString(facility.facilityType),
      facilityStatusFromString(facility.status),
      this.mapLocation(facility.location),
      new StorageCapacityKilograms(this.extractStorageCapacityKg(facility)),
      new ProcessingCapacityKilogramsPerDay(this.extractProcessingCapacityKgPerDay(facility)),
      (cluster.assignedContainers ?? []).map((container) => this.mapContainer(container)),
      topLevelDailyPlans
        .filter((dailyPlan) => this.extractFacilityId(dailyPlan) === facilityId)
        .map((dailyPlan) =>
          this.mapDailyPlan(
            dailyPlan,
            facilityId,
            infrastructurePlanId,
            serviceDateToDayMap,
            this.facilityNameFromJson(facility).getValue(),
          ),
        ),
    );
  }

  private static mapFacility(
    facility: InfrastructurePlanFacilityJsonResponse,
    dailyPlans: InfrastructurePlanDailyPlanJsonResponse[],
    infrastructurePlanId: string | null,
    serviceDateToDayMap: Map<string, number>,
  ): InfrastructurePlanFacilityDetail {
    return new InfrastructurePlanFacilityDetail(
      new UllUUID(facility.id),
      this.facilityNameFromJson(facility),
      facilityTypeFromString(facility.facilityType),
      facilityStatusFromString(facility.status),
      this.mapLocation(facility.location),
      new StorageCapacityKilograms(this.extractStorageCapacityKg(facility)),
      new ProcessingCapacityKilogramsPerDay(this.extractProcessingCapacityKgPerDay(facility)),
      (facility.assignedContainers ?? []).map((container) => this.mapContainer(container)),
      dailyPlans.map((dailyPlan) =>
        this.mapDailyPlan(
          dailyPlan,
          facility.id,
          infrastructurePlanId,
          serviceDateToDayMap,
          this.facilityNameFromJson(facility).getValue(),
        ),
      ),
    );
  }

  private static mapDailyPlan(
    dailyPlan: InfrastructurePlanDailyPlanJsonResponse,
    fallbackFacilityId: string,
    fallbackInfrastructurePlanId: string | null,
    serviceDateToDayMap: Map<string, number>,
    fallbackFacilityName: string | null = null,
  ): InfrastructurePlanDailyPlanDetail {
    const serviceDate = this.normalizeServiceDate(dailyPlan.serviceDate);
    const vehicle = this.mapVehicle(dailyPlan);
    if (!vehicle) {
      throw new Error('Daily plan response must include vehicle or vehicleId');
    }
    const facilityNameFromPlan =
      typeof dailyPlan.facilityName === 'string' && dailyPlan.facilityName.trim().length > 0
        ? dailyPlan.facilityName.trim()
        : null;
    const facilityName = facilityNameFromPlan ?? fallbackFacilityName ?? null;

    return new InfrastructurePlanDailyPlanDetail(
      this.parseOptionalUuid(dailyPlan.id),
      this.parseOptionalUuid(dailyPlan.infrastructurePlanId ?? fallbackInfrastructurePlanId),
      new UllUUID(this.extractFacilityId(dailyPlan) ?? fallbackFacilityId),
      facilityName,
      serviceDate,
      typeof dailyPlan.planDay === 'number'
        ? dailyPlan.planDay
        : serviceDateToDayMap.get(serviceDate) ?? 1,
      vehicle.id,
      CollectedWeightKilograms.fromKilograms(this.extractNumber(dailyPlan.totalCollectedKilograms)),
      CollectedVolumeLiters.fromLiters(this.extractNumber(dailyPlan.totalCollectedLiters)),
      Distance.fromMeters(this.extractNumber(dailyPlan.totalDistanceMeters)),
      (dailyPlan.stops ?? []).map((stop) => this.mapStop(stop)),
      vehicle,
    );
  }

  private static mapStop(stop: InfrastructurePlanStopJsonResponse): InfrastructurePlanStopDetail {
    const type = stop.type ? stopTypeFromString(stop.type) : StopType.CONTAINER;
    const isContainer = type === StopType.CONTAINER;
    const containerUuid = isContainer
      ? new UllUUID(stop.containerId ?? stop.container?.id ?? UllUUID.random().getValue())
      : null;

    const containerNameRaw =
      typeof stop.containerName === 'string' && stop.containerName.trim().length > 0
        ? stop.containerName.trim()
        : typeof stop.container?.name === 'string' && stop.container.name.trim().length > 0
          ? stop.container.name.trim()
          : null;
    const containerName = containerNameRaw ? new Name(containerNameRaw) : null;

    return new InfrastructurePlanStopDetail(
      RouteSequence.of(this.extractPositiveSequence(stop.sequence)),
      containerUuid,
      type,
      CollectedWeightKilograms.fromKilograms(this.extractNumber(stop.collectedKilograms)),
      CollectedVolumeLiters.fromLiters(this.extractNumber(stop.collectedLiters)),
      Distance.fromMeters(this.extractNumber(stop.distanceFromPreviousMeters)),
      Distance.fromMeters(this.extractNumber(stop.cumulativeDistanceMeters)),
      typeof stop.containerActualLiters === 'number' ? stop.containerActualLiters : null,
      (stop.alerts ?? []).map((alert) => this.mapAlert(alert)),
      containerName,
    );
  }

  private static mapAlert(alert: StopAlertJsonResponse): InfrastructurePlanStopAlertDetail {
    return new InfrastructurePlanStopAlertDetail(
      alert.type,
      alert.message,
      typeof alert.value === 'number' ? alert.value : null,
    );
  }

  private static mapVehicle(
    dailyPlan: InfrastructurePlanDailyPlanJsonResponse,
  ): InfrastructurePlanVehicleDetail | null {
    if (dailyPlan.vehicle) {
      const capacityKilograms = this.extractNamedNumber(
        dailyPlan.vehicle.capacityKilograms,
        'Kilograms',
      );
      const capacityLiters = this.extractNamedNumber(dailyPlan.vehicle.capacityLiters, 'liters');
      const costPerKilometer = dailyPlan.vehicle.costPerKilometer
        ? new TransportationVariableCost(
            typeof dailyPlan.vehicle.costPerKilometer.amount === 'number'
              ? dailyPlan.vehicle.costPerKilometer.amount
              : 0,
            dailyPlan.vehicle.costPerKilometer.currency ?? 'EUR',
          )
        : null;

      return new InfrastructurePlanVehicleDetail(
        new UllUUID(dailyPlan.vehicle.id),
        typeof dailyPlan.vehicle.name === 'string' && dailyPlan.vehicle.name.trim().length > 0
          ? new Name(dailyPlan.vehicle.name.trim())
          : null,
        dailyPlan.vehicle.vehicleType ? vehicleTypeFromString(dailyPlan.vehicle.vehicleType) : null,
        capacityKilograms != null ? new VehicleCapacityKilograms(capacityKilograms) : null,
        capacityLiters != null ? new VehicleCapacityLiters(capacityLiters) : null,
        costPerKilometer,
      );
    }

    if (typeof dailyPlan.vehicleId === 'string' && dailyPlan.vehicleId.length > 0) {
      return new InfrastructurePlanVehicleDetail(new UllUUID(dailyPlan.vehicleId));
    }

    return null;
  }

  private static mapContainer(container: {
    id: string;
    name?: string;
    location: { latitude: number; longitude: number; postalAddress: string; gisReference?: string };
    wasteType: string;
    capacityLiters: { liters: number };
    dailyDemandLitersPerDay: { litersPerDay: number };
    serviceZone?: string | null;
  }): InfrastructurePlanContainerDetail {
    return new InfrastructurePlanContainerDetail(
      new UllUUID(container.id),
      this.containerNameFromJson(container),
      this.mapLocation(container.location),
      wasteTypeFromString(container.wasteType),
      new ContainerCapacityLiters(this.extractNamedNumber(container.capacityLiters, 'liters') ?? 0),
      new DailyWasteDemandLitersPerDay(this.extractNamedNumber(container.dailyDemandLitersPerDay, 'litersPerDay') ?? 0),
      container.serviceZone ? serviceZoneFromString(container.serviceZone) : null,
    );
  }

  private static mapLocation(location: {
    latitude: number;
    longitude: number;
    postalAddress: string;
    gisReference?: string;
  }): Location {
    return new Location(
      location.latitude,
      location.longitude,
      location.postalAddress,
      location.gisReference ?? location.postalAddress,
    );
  }

  private static mapContainerDailyState(
    state: ContainerDailyStateJsonResponse,
  ): InfrastructurePlanContainerDailyStateDetail {
    const containerNameRaw =
      typeof state.containerName === 'string' && state.containerName.trim().length > 0
        ? state.containerName.trim()
        : null;
    const containerName = containerNameRaw ? new Name(containerNameRaw) : null;

    return new InfrastructurePlanContainerDailyStateDetail(
      this.parseOptionalUuid(state.id),
      new UllUUID(state.containerId),
      state.planDay,
      state.dailyFillingLiters,
      new ContainerCapacityLiters(state.containerCapacityLiters),
      typeof state.dailyDemandLitersPerDay === 'number'
        ? new DailyWasteDemandLitersPerDay(state.dailyDemandLitersPerDay)
        : null,
      containerStatusFromString(state.status),
      containerName,
    );
  }

  private static mapMetrics(data: InfrastructurePlanDetailJsonResponse): InfrastructurePlanMetricsDetail {
    const metrics = data.metrics ?? {};

    return new InfrastructurePlanMetricsDetail(
      CollectedWeightKilograms.fromKilograms(this.extractNumber(metrics.totalCollectedKilograms ?? data.totalCollectedKilograms)),
      CollectedVolumeLiters.fromLiters(this.extractNumber(metrics.totalCollectedLiters ?? data.totalCollectedLiters)),
      Distance.fromMeters(this.extractNumber(metrics.totalDistanceMeters ?? data.totalDistanceMeters)),
      this.extractOptionalNumber(metrics.averagePickupTimeMinutes ?? data.averagePickupTimeMinutes),
      this.mapTotalCost(metrics.estimatedTotalCost ?? data.estimatedTotalCost),
      this.mapMaximumBudget(metrics.maxBudget ?? data.maxBudget),
    );
  }

  private static mapTotalCost(value?: InfrastructurePlanMoneyJsonResponse): TotalCost {
    if (!value) {
      return new TotalCost(0, 'EUR');
    }

    return new TotalCost(
      typeof value.amount === 'number' ? value.amount : 0,
      value.currency ?? 'EUR',
    );
  }

  private static mapMaximumBudget(value?: InfrastructurePlanMoneyJsonResponse): MaximumBudget {
    if (!value) {
      return new MaximumBudget(0, 'EUR');
    }

    return new MaximumBudget(
      typeof value.amount === 'number' ? value.amount : 0,
      value.currency ?? 'EUR',
    );
  }

  private static buildServiceDateToDayMap(
    data: InfrastructurePlanDetailJsonResponse,
  ): Map<string, number> {
    const serviceDates = new Set<string>();
    const register = (dailyPlan: InfrastructurePlanDailyPlanJsonResponse): void => {
      const normalizedServiceDate = this.normalizeServiceDate(dailyPlan.serviceDate);
      if (normalizedServiceDate.length > 0) {
        serviceDates.add(normalizedServiceDate);
      }
    };

    (data.dailyPlans ?? []).forEach(register);
    (data.facilities ?? []).forEach((facility) => (facility.dailyPlans ?? []).forEach(register));

    return new Map(
      Array.from(serviceDates)
        .sort((left, right) => left.localeCompare(right))
        .map((serviceDate, index) => [serviceDate, index + 1]),
    );
  }

  private static extractFacilityId(
    dailyPlan: InfrastructurePlanDailyPlanJsonResponse,
  ): string | undefined {
    if (typeof dailyPlan.facilityId === 'string' && dailyPlan.facilityId.length > 0) {
      return dailyPlan.facilityId;
    }

    if (dailyPlan.facility && typeof dailyPlan.facility.id === 'string') {
      return dailyPlan.facility.id;
    }

    return undefined;
  }

  private static normalizeServiceDate(value: string | { date?: string; value?: string }): string {
    if (typeof value === 'string') {
      return value;
    }

    if (value && typeof value.value === 'string') {
      return value.value;
    }

    if (value && typeof value.date === 'string') {
      return value.date;
    }

    return '';
  }

  private static extractPeriod(data: InfrastructurePlanDetailJsonResponse): number | null {
    if (typeof data.period === 'number') {
      return data.period;
    }

    const parsedDate = new Date(data.executedAt);
    return Number.isNaN(parsedDate.getTime()) ? null : parsedDate.getUTCFullYear();
  }

  private static extractStorageCapacityKg(
    facility: InfrastructurePlanFacilityJsonResponse | InfrastructurePlanClusterJsonResponse['facility'],
  ): number {
    if ('capacities' in facility && facility.capacities) {
      return typeof facility.capacities.storageCapacityKg === 'number'
        ? facility.capacities.storageCapacityKg
        : 0;
    }

    if ('storageCapacity' in facility) {
      return this.extractNumber(facility.storageCapacity);
    }

    return 0;
  }

  private static extractProcessingCapacityKgPerDay(
    facility: InfrastructurePlanFacilityJsonResponse | InfrastructurePlanClusterJsonResponse['facility'],
  ): number {
    if ('capacities' in facility && facility.capacities) {
      return typeof facility.capacities.processingCapacityKgPerDay === 'number'
        ? facility.capacities.processingCapacityKgPerDay
        : 0;
    }

    if ('processingCapacity' in facility) {
      return this.extractNumber(facility.processingCapacity);
    }

    return 0;
  }

  private static extractNamedNumber(value: unknown, key: string): number | null {
    if (!value || typeof value !== 'object') {
      return null;
    }

    const candidate = (value as Record<string, unknown>)[key];
    return typeof candidate === 'number' ? candidate : null;
  }

  private static extractNumber(value: unknown): number {
    if (typeof value === 'number') {
      return value;
    }

    if (value && typeof value === 'object') {
      const nestedValue = (value as Record<string, unknown>).value;
      if (typeof nestedValue === 'number') {
        return nestedValue;
      }
    }

    return 0;
  }

  private static extractOptionalNumber(value: unknown): number | null {
    if (typeof value === 'number') {
      return value;
    }

    if (value && typeof value === 'object') {
      const nestedValue = (value as Record<string, unknown>).value;
      if (typeof nestedValue === 'number') {
        return nestedValue;
      }
    }

    return null;
  }

  private static extractPositiveSequence(value: unknown): number {
    const sequence = this.extractNumber(value);
    return sequence >= 1 ? sequence : 1;
  }

  private static parseOptionalUuid(value?: string | null): UllUUID | null {
    if (!value || value.trim().length === 0) {
      return null;
    }

    return new UllUUID(value);
  }

  private static facilityNameFromJson(facility: { id: string; name?: string }): Name {
    const trimmed = facility.name?.trim();
    if (trimmed) {
      return new Name(trimmed);
    }
    return new Name(infrastructurePlanDetailFallbackDisplayNames.facility);
  }

  private static containerNameFromJson(container: { id: string; name?: string }): Name {
    const trimmed = container.name?.trim();
    if (trimmed) {
      return new Name(trimmed);
    }
    return new Name(infrastructurePlanDetailFallbackDisplayNames.container);
  }
}
