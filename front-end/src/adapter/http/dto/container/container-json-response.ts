import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../../domain/entity/container';
import { serviceZoneFromString } from '../../../../domain/enumerate/service-zone';
import { wasteTypeFromString } from '../../../../domain/enumerate/waste-type';
import { QuantityUnit } from '../../../../domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '../../../../domain/valueobject/demand/waste-demand';
import { Location } from '../../../../domain/valueobject/location/location';

/**
 * ContainerJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Container
 * resource. Uses primitive fields for serialization and provides a helper
 * `toContainer` to convert the payload into the domain `Container` entity.
 */
export class ContainerJsonResponse {
  id: string;
  location: {
    latitude: number;
    longitude: number;
    postalAddress: string;
    gisReference: string;
  };
  wasteType: string;
  wasteDemand: {
    value: number;
    quantityUnit: string;
    timeUnit: string;
  };
  serviceZone?: string | null;

  constructor(
    id: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType: string,
    wasteDemand: { value: number; quantityUnit: string; timeUnit: string },
    serviceZone?: string | null
  ) {
    this.id = id;
    this.location = location;
    this.wasteType = wasteType;
    this.wasteDemand = wasteDemand;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Convert JSON response into domain `Container` entity.
   * Builds the required value objects (`Location`, `WasteDemand`) and parses
   * enumerations for `WasteType` and `ServiceZone`.
   */
  public static toContainer(data: ContainerJsonResponse): Container {
    const id = new UllUUID(data.id);
    const loc = new Location(
      data.location.latitude,
      data.location.longitude,
      data.location.postalAddress,
      data.location.gisReference
    );
    const quantityUnit = new QuantityUnit(data.wasteDemand.quantityUnit);
    const demand = new WasteDemand(data.wasteDemand.value, quantityUnit, (data.wasteDemand.timeUnit as any));
    const wasteType = wasteTypeFromString(data.wasteType);
    const serviceZone = data.serviceZone ? serviceZoneFromString(data.serviceZone) : undefined;

    return new Container(loc, wasteType, demand, serviceZone, id);
  }
}
