import type { ContainerDailyStateJsonResponse } from '@/adapter/http/dto/infrastructure-plan/container-daily-state-json-response';
import type { InfrastructurePlanClusterJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-cluster-json-response';
import type { InfrastructurePlanDailyPlanJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-daily-plan-json-response';
import type { InfrastructurePlanDetailJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-json-response';
import type { InfrastructurePlanFacilityJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-facility-json-response';
import type { InfrastructurePlanMoneyJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-money-json-response';
import type { InfrastructurePlanStopJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-stop-json-response';
import type { StopAlertJsonResponse } from '@/adapter/http/dto/infrastructure-plan/stop-alert-json-response';
import { containerStatusFromString } from '@/domain/enumerate/container-status';
import { facilityStatusFromString } from '@/domain/enumerate/facility-status';
import { facilityTypeFromString } from '@/domain/enumerate/facility-type';
import { infrastructurePlanValidityStateFromString } from '@/domain/enumerate/infrastructure-plan-validity-state';
import { serviceZoneFromString } from '@/domain/enumerate/service-zone';
import { StopType, stopTypeFromString } from '@/domain/enumerate/stop-type';
import { vehicleTypeFromString } from '@/domain/enumerate/vehicle-type';
import { wasteTypeFromString } from '@/domain/enumerate/waste-type';
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
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';
import { Name } from '@/domain/valueobject/name/name';
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
   *
   * @param data Infrastructure plan detail JSON from the API
   * @returns Normalized read-only infrastructure plan detail
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

  /**
   * Maps facilities or legacy clusters from the API response.
   *
   * @param data Infrastructure plan detail JSON from the API
   * @param serviceDateToDayMap Map from normalized service date to plan day index
   * @returns Facility read models for the plan
   */
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

  /**
   * Maps a legacy cluster payload into a facility read model.
   *
   * @param cluster Cluster JSON from the API
   * @param topLevelDailyPlans Daily plans defined at plan root level
   * @param infrastructurePlanId Parent plan identifier when present
   * @param serviceDateToDayMap Map from normalized service date to plan day index
   * @returns Facility read model built from the cluster
   */
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

  /**
   * Maps a facility JSON node into the read model.
   *
   * @param facility Facility JSON from the API
   * @param dailyPlans Daily plans assigned to the facility
   * @param infrastructurePlanId Parent plan identifier when present
   * @param serviceDateToDayMap Map from normalized service date to plan day index
   * @returns Facility read model
   */
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

  /**
   * Maps a daily plan JSON node into the read model.
   *
   * @param dailyPlan Daily plan JSON from the API
   * @param fallbackFacilityId Facility id used when the plan omits facilityId
   * @param fallbackInfrastructurePlanId Plan id used when the daily plan omits infrastructurePlanId
   * @param serviceDateToDayMap Map from normalized service date to plan day index
   * @param fallbackFacilityName Facility display name used when the plan omits facilityName
   * @returns Daily plan read model
   */
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

  /**
   * Maps a route stop JSON node into the read model.
   *
   * @param stop Stop JSON from the API
   * @returns Stop read model
   */
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

  /**
   * Maps a stop alert JSON node into the read model.
   *
   * @param alert Alert JSON from the API
   * @returns Stop alert read model
   */
  private static mapAlert(alert: StopAlertJsonResponse): InfrastructurePlanStopAlertDetail {
    return new InfrastructurePlanStopAlertDetail(
      alert.type,
      alert.message,
      typeof alert.value === 'number' ? alert.value : null,
    );
  }

  /**
   * Maps vehicle data embedded in a daily plan.
   *
   * @param dailyPlan Daily plan JSON that may embed or reference a vehicle
   * @returns Vehicle read model, or null when no vehicle data is present
   */
  private static mapVehicle(
    dailyPlan: InfrastructurePlanDailyPlanJsonResponse,
  ): InfrastructurePlanVehicleDetail | null {
    if (dailyPlan.vehicle) {
      const capacityKilograms = this.extractNamedNumber(
        dailyPlan.vehicle.capacityKilograms,
        'Kilograms',
      );
      const capacityLiters = this.extractNamedNumber(dailyPlan.vehicle.capacityLiters, 'liters');
      const costPerKilometer = this.mapTransportationVariableCost(dailyPlan.vehicle.costPerKilometer);

      return new InfrastructurePlanVehicleDetail(
        new UllUUID(dailyPlan.vehicle.id),
        this.parseOptionalName(dailyPlan.vehicle.name),
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

  /**
   * Maps a container JSON node into the read model.
   *
   * @param container Container JSON from the API
   * @returns Container read model
   */
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

  /**
   * Maps a location JSON object into a domain location value object.
   *
   * @param location Location JSON from the API
   * @returns Domain location value object
   */
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

  /**
   * Maps container daily state monitoring JSON into the read model.
   *
   * @param state Container daily state JSON from the API
   * @returns Container daily state read model
   */
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

  /**
   * Maps plan-level metrics from the API response.
   *
   * @param data Infrastructure plan detail JSON from the API
   * @returns Plan metrics read model
   */
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

  /**
   * Maps estimated total cost money JSON into a value object.
   *
   * @param value Money JSON from the API, when present
   * @returns Total cost value object
   */
  private static mapTotalCost(value?: InfrastructurePlanMoneyJsonResponse): TotalCost {
    if (!value) {
      return new TotalCost(0, 'EUR');
    }

    return new TotalCost(
      typeof value.amount === 'number' ? value.amount : 0,
      value.currency ?? 'EUR',
    );
  }

  /**
   * Maps maximum budget money JSON into a value object.
   *
   * @param value Money JSON from the API, when present
   * @returns Maximum budget value object
   */
  private static mapMaximumBudget(value?: InfrastructurePlanMoneyJsonResponse): MaximumBudget {
    if (!value) {
      return new MaximumBudget(0, 'EUR');
    }

    return new MaximumBudget(
      typeof value.amount === 'number' ? value.amount : 0,
      value.currency ?? 'EUR',
    );
  }

  /**
   * Builds a stable plan-day index keyed by normalized service date.
   *
   * @param data Infrastructure plan detail JSON from the API
   * @returns Map from normalized service date to plan day index
   */
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

  /**
   * Extracts the facility identifier from a daily plan JSON node.
   *
   * @param dailyPlan Daily plan JSON from the API
   * @returns Facility id when present, otherwise undefined
   */
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

  /**
   * Normalizes service date values returned as string or nested object.
   *
   * @param value Service date as plain string or nested JSON object
   * @returns Normalized ISO date string, or empty string when missing
   */
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

  /**
   * Extracts the planning period year from the API payload.
   *
   * @param data Infrastructure plan detail JSON from the API
   * @returns Planning period year, or null when it cannot be derived
   */
  private static extractPeriod(data: InfrastructurePlanDetailJsonResponse): number | null {
    if (typeof data.period === 'number') {
      return data.period;
    }

    const parsedDate = new Date(data.executedAt);
    return Number.isNaN(parsedDate.getTime()) ? null : parsedDate.getUTCFullYear();
  }

  /**
   * Reads storage capacity in kilograms from facility JSON variants.
   *
   * @param facility Facility JSON in either modern or legacy shape
   * @returns Storage capacity in kilograms
   */
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

  /**
   * Reads processing capacity in kilograms per day from facility JSON variants.
   *
   * @param facility Facility JSON in either modern or legacy shape
   * @returns Processing capacity in kilograms per day
   */
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

  /**
   * Parses an optional name from plain string or nested value object JSON.
   *
   * @param value Name as plain string or nested JSON object
   * @returns Parsed name, or null when absent or blank
   */
  private static parseOptionalName(value: unknown): Name | null {
    if (typeof value === 'string') {
      const trimmed = value.trim();
      return trimmed.length > 0 ? new Name(trimmed) : null;
    }

    if (value && typeof value === 'object') {
      const nested = (value as Record<string, unknown>).value;
      if (typeof nested === 'string') {
        const trimmed = nested.trim();
        return trimmed.length > 0 ? new Name(trimmed) : null;
      }
    }

    return null;
  }

  /**
   * Extracts a numeric field from plain numbers or nested JSON objects.
   *
   * @param value Numeric value as plain number or nested JSON object
   * @param key Property name to read when value is an object
   * @returns Extracted number, or null when missing
   */
  private static extractNamedNumber(value: unknown, key: string): number | null {
    if (typeof value === 'number') {
      return value;
    }

    if (!value || typeof value !== 'object') {
      return null;
    }

    const record = value as Record<string, unknown>;
    const candidate = record[key];
    if (typeof candidate === 'number') {
      return candidate;
    }

    return this.extractOptionalNumber(value);
  }

  /**
   * Maps transportation variable cost JSON into a value object.
   *
   * @param value Cost as plain number or nested money JSON object
   * @param defaultCurrency Currency code used when the payload omits currency
   * @returns Transportation variable cost value object, or null when amount is missing
   */
  private static mapTransportationVariableCost(
    value: unknown,
    defaultCurrency = 'EUR',
  ): TransportationVariableCost | null {
    if (typeof value === 'number') {
      return new TransportationVariableCost(value, defaultCurrency);
    }

    if (!value || typeof value !== 'object') {
      return null;
    }

    const record = value as Record<string, unknown>;
    const amount =
      typeof record.amount === 'number' ? record.amount : this.extractOptionalNumber(value);
    if (amount == null) {
      return null;
    }

    let currency = defaultCurrency;
    if (typeof record.currency === 'string' && record.currency.length > 0) {
      currency = record.currency;
    } else if (record.currency && typeof record.currency === 'object') {
      const code = (record.currency as Record<string, unknown>).code;
      if (typeof code === 'string' && code.length > 0) {
        currency = code;
      }
    }

    return new TransportationVariableCost(amount, currency);
  }

  /**
   * Extracts a numeric value, defaulting to zero when missing.
   *
   * @param value Numeric value as plain number or nested JSON object
   * @returns Extracted number, or zero when missing
   */
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

  /**
   * Extracts an optional numeric value from plain or nested JSON.
   *
   * @param value Numeric value as plain number or nested JSON object
   * @returns Extracted number, or null when missing
   */
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

  /**
   * Extracts a positive route sequence number from JSON.
   *
   * @param value Sequence as plain number or nested JSON object
   * @returns Positive sequence number, defaulting to 1 when missing or invalid
   */
  private static extractPositiveSequence(value: unknown): number {
    const sequence = this.extractNumber(value);
    return sequence >= 1 ? sequence : 1;
  }

  /**
   * Parses an optional UUID string into a value object.
   *
   * @param value UUID string from the API, when present
   * @returns Parsed UUID value object, or null when absent or blank
   */
  private static parseOptionalUuid(value?: string | null): UllUUID | null {
    if (!value || value.trim().length === 0) {
      return null;
    }

    return new UllUUID(value);
  }

  /**
   * Resolves a facility display name, using a fallback when the API omits it.
   *
   * @param facility Facility JSON containing an optional name
   * @returns Facility display name value object
   */
  private static facilityNameFromJson(facility: { id: string; name?: string }): Name {
    const trimmed = facility.name?.trim();
    if (trimmed) {
      return new Name(trimmed);
    }
    return new Name(infrastructurePlanDetailFallbackDisplayNames.facility);
  }

  /**
   * Resolves a container display name, using a fallback when the API omits it.
   *
   * @param container Container JSON containing an optional name
   * @returns Container display name value object
   */
  private static containerNameFromJson(container: { id: string; name?: string }): Name {
    const trimmed = container.name?.trim();
    if (trimmed) {
      return new Name(trimmed);
    }
    return new Name(infrastructurePlanDetailFallbackDisplayNames.container);
  }
}
