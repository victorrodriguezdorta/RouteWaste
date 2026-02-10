import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../../domain/entity/facility';
import { facilityStatusFromString } from '../../../../domain/enumerate/facility-status';
import { facilityTypeFromString } from '../../../../domain/enumerate/facility-type';
import { OpeningFixedCost } from '../../../../domain/valueobject/cost/opening-fixed-cost';
import { Capacity } from '../../../../domain/valueobject/demand/capacity';
import { QuantityUnit } from '../../../../domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '../../../../domain/valueobject/demand/waste-demand';
import { Location } from '../../../../domain/valueobject/location/location';

/**
 * FacilityJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Facility
 * resource. Uses primitive fields for serialization and provides a helper
 * `toFacility` to convert the payload into the domain `Facility` entity.
 */
export class FacilityJsonResponse {
  id: string;
  facilityType: string;
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };
  capacity: { value: number; quantityUnit: string; timeUnit: string };
  openingFixedCost: { amount: number; currency?: string };
  status: string;
  assignedWasteDemand?: { value: number; quantityUnit: string; timeUnit: string };

  constructor(
    id: string,
    facilityType: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    capacity: { value: number; quantityUnit: string; timeUnit: string },
    openingFixedCost: { amount: number; currency?: string },
    status: string,
    assignedWasteDemand?: { value: number; quantityUnit: string; timeUnit: string }
  ) {
    this.id = id;
    this.facilityType = facilityType;
    this.location = location;
    this.capacity = capacity;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
    this.assignedWasteDemand = assignedWasteDemand;
  }

  /**
   * Convert the JSON response into a domain `Facility` entity.
   * Builds Location, Capacity and OpeningFixedCost value objects and parses
   * enums for facility type and status.
   */
  public static toFacility(data: FacilityJsonResponse): Facility {
    const id = new UllUUID(data.id);
    const loc = new Location(
      data.location.latitude,
      data.location.longitude,
      data.location.postalAddress,
      data.location.gisReference
    );
    const quantityUnit = new QuantityUnit(data.capacity.quantityUnit);
    const cap = new Capacity(data.capacity.value, quantityUnit, (data.capacity.timeUnit as any));
    const openingCost = new OpeningFixedCost(data.openingFixedCost.amount, data.openingFixedCost.currency);
    const facilityType = facilityTypeFromString(data.facilityType);
    const status = facilityStatusFromString(data.status);
    const facility = new Facility(facilityType, loc, cap, openingCost, status, id);

    // If assigned waste demand present, set it via internal update
    if (data.assignedWasteDemand) {
      const q = new QuantityUnit(data.assignedWasteDemand.quantityUnit);
      const assigned = new WasteDemand(data.assignedWasteDemand.value, q, (data.assignedWasteDemand.timeUnit as any));
      facility.assignWasteDemand(assigned);
    }

    return facility;
  }
}
