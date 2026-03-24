import type { CreateVehicleCommand } from '@/application/usecase/vehicle-management/create-vehicle/create-vehicle-command';

/**
 * VehiclePostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Vehicle.
 * Uses primitive types for all fields so it can be serialized directly to JSON.
 */
export class VehiclePostJsonRequest {
  /** Vehicle type as enum key string. */
  vehicleType: string;
  /** Transport capacity expressed with primitive parts. */
  transportCapacity: {
    value: number;
    quantityUnit: string;
    timeUnit: string;
  };
  /** Cost per kilometer expressed with primitive parts. */
  costPerKilometer: {
    amount: number;
    currency?: string;
  };

  /**
   * Build a request DTO instance.
   * @param vehicleType vehicle type string
   * @param transportCapacity capacity parts
   * @param costPerKilometer cost parts
   */
  constructor(
    vehicleType: string,
    transportCapacity: { value: number; quantityUnit: string; timeUnit: string },
    costPerKilometer: { amount: number; currency?: string }
  ) {
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
    this.costPerKilometer = costPerKilometer;
  }

  /**
   * Mapea un `CreateVehicleCommand` (entrada de dominio) a este DTO.
   * Extrae valores primitivos de los value objects de dominio para producir un
   * payload serializable para una petición POST.
   * @param data Comando de creación de vehículo del dominio.
   * @returns Instancia de VehiclePostJsonRequest lista para enviar como payload.
   */
  public static toRequest(data: CreateVehicleCommand): VehiclePostJsonRequest {
    return new VehiclePostJsonRequest(
      data.vehicleType,
      {
        value: data.transportCapacity.getValue(),
        quantityUnit: data.transportCapacity.getQuantityUnit().getValue(),
        timeUnit: data.transportCapacity.getTimeUnit().toString(),
      },
      {
        amount: data.costPerKilometer.getAmount(),
        currency: data.costPerKilometer.getCurrency().getCode(),
      }
    );
  }
}
