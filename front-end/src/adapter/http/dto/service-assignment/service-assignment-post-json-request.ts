import type { AssignContainerToFacilityCommand } from '@/application/usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-command';

/**
 * ServiceAssignmentPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when assigning a
 * `Container` to a `Facility`. Uses primitive fields for serialization.
 */
export class ServiceAssignmentPostJsonRequest {
  /** Unique identifier of the container being assigned */
  containerId: string;

  /** Unique identifier of the facility receiving the container */
  facilityId: string;

  /** Waste demand information including value, quantity unit, and time unit */
  wasteDemand: { value: number; quantityUnit: string; timeUnit: string };

  /** Distance in meters between the container and facility */
  distanceMeters: number;

  /** Service time in minutes required at the facility */
  serviceTimeMinutes: number;

  /** Transport cost including amount and currency code */
  transportCost: { amount: number; currency?: string };

  /**
   * Constructor for ServiceAssignmentPostJsonRequest
   *
   * @param containerId - The container ID
   * @param facilityId - The facility ID
   * @param wasteDemand - The waste demand object with value, quantityUnit, and timeUnit
   * @param distanceMeters - The distance in meters
   * @param serviceTimeMinutes - The service time in minutes
   * @param transportCost - The transport cost object with amount and optional currency
   */
  constructor(
    containerId: string,
    facilityId: string,
    wasteDemand: { value: number; quantityUnit: string; timeUnit: string },
    distanceMeters: number,
    serviceTimeMinutes: number,
    transportCost: { amount: number; currency?: string }
  ) {
    this.containerId = containerId;
    this.facilityId = facilityId;
    this.wasteDemand = wasteDemand;
    this.distanceMeters = distanceMeters;
    this.serviceTimeMinutes = serviceTimeMinutes;
    this.transportCost = transportCost;
  }

  /**
   * Map an `AssignContainerToFacilityCommand` into a serializable request body.
   *
   * @param data - The command containing container assignment information
   * @returns A new ServiceAssignmentPostJsonRequest with serialized data
   */
  public static toRequest(data: AssignContainerToFacilityCommand): ServiceAssignmentPostJsonRequest {
    return new ServiceAssignmentPostJsonRequest(
      data.containerId.toString(),
      data.facilityId.toString(),
      {
        value: data.wasteDemand.getValue(),
        quantityUnit: data.wasteDemand.getQuantityUnit().getValue(),
        timeUnit: data.wasteDemand.getTimeUnit().toString(),
      },
      data.distance.toMeters(),
      data.serviceTime.getValue(),
      { amount: data.transportCost.getAmount(), currency: data.transportCost.getCurrency().getCode() }
    );
  }
}
