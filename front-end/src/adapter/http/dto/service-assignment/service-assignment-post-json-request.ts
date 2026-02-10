import type { AssignContainerToFacilityCommand } from '../../../../application/usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-command';

/**
 * ServiceAssignmentPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when assigning a
 * `Container` to a `Facility`. Uses primitive fields for serialization.
 */
export class ServiceAssignmentPostJsonRequest {
  containerId: string;
  facilityId: string;
  wasteDemand: { value: number; quantityUnit: string; timeUnit: string };
  distanceMeters: number;
  serviceTimeMinutes: number;
  transportCost: { amount: number; currency?: string };

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

  /** Map an `AssignContainerToFacilityCommand` into a serializable request body. */
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
