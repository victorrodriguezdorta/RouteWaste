import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../../domain/entity/container';
import { Facility } from '../../../../domain/entity/facility';
import { ServiceAssignment } from '../../../../domain/entity/service-assignment';
import { timeUnitFromString } from '../../../../domain/enumerate/time-unit';
import { TransportationVariableCost } from '../../../../domain/valueobject/cost/transportation-variable-cost';
import { QuantityUnit } from '../../../../domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '../../../../domain/valueobject/demand/waste-demand';
import { Distance } from '../../../../domain/valueobject/location/distance';
import { ServiceTime } from '../../../../domain/valueobject/location/service-time';
import { ContainerJsonResponse } from '../container/container-json-response';
import { FacilityJsonResponse } from '../facility/facility-json-response';

/**
 * ServiceAssignmentJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a
 * ServiceAssignment resource. Provides `toServiceAssignment` to convert the
 * JSON payload into the domain `ServiceAssignment` entity using existing
 * `Container` and `Facility` DTO converters.
 */
export class ServiceAssignmentJsonResponse {
  id: string;
  container: any;
  facility: any;
  wasteDemand: { value: number; quantityUnit: string; timeUnit: string };
  distanceMeters: number;
  serviceTimeMinutes: number;
  transportCost: { amount: number; currency?: string };

  constructor(
    id: string,
    container: any,
    facility: any,
    wasteDemand: { value: number; quantityUnit: string; timeUnit: string },
    distanceMeters: number,
    serviceTimeMinutes: number,
    transportCost: { amount: number; currency?: string }
  ) {
    this.id = id;
    this.container = container;
    this.facility = facility;
    this.wasteDemand = wasteDemand;
    this.distanceMeters = distanceMeters;
    this.serviceTimeMinutes = serviceTimeMinutes;
    this.transportCost = transportCost;
  }

  /**
   * Convert the JSON response into a domain `ServiceAssignment` entity.
   * Uses `ContainerJsonResponse.toContainer` and `FacilityJsonResponse.toFacility`
   * to rebuild nested entities and constructs the required value objects.
   */
  public static toServiceAssignment(data: ServiceAssignmentJsonResponse): ServiceAssignment {
    const id = new UllUUID(data.id);

    const container: Container = ContainerJsonResponse.toContainer(data.container);
    const facility: Facility = FacilityJsonResponse.toFacility(data.facility);

    const quantityUnit = new QuantityUnit(data.wasteDemand.quantityUnit);
    const timeUnit = timeUnitFromString(data.wasteDemand.timeUnit);
    const wasteDemand = new WasteDemand(data.wasteDemand.value, quantityUnit, timeUnit);

    const distance = Distance.fromMeters(data.distanceMeters);
    const serviceTime = new ServiceTime(data.serviceTimeMinutes);
    const transportCost = new TransportationVariableCost(data.transportCost.amount, data.transportCost.currency);

    return new ServiceAssignment(container, facility, wasteDemand, distance, serviceTime, transportCost, id);
  }
}
