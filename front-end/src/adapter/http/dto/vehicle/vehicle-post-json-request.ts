import type { CreateVehicleCommand } from '@/application/model/vehicle-management/create-vehicle/create-vehicle-command';

/**
 * VehiclePostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Vehicle.
 * Uses primitive types for all fields so it can be serialized directly to JSON.
 */
export class VehiclePostJsonRequest {
  /** Human-readable vehicle name. */
  name: string;
  /** Vehicle type as enum key string. */
  vehicleType: string;
  /** Capacity in kilograms. */
  capacityKilograms: number;
  /** Capacity in liters. */
  CapacityLiters: number;
  /** Cost per kilometer expressed with primitive parts. */
  costPerKilometer: {
    amount: number;
    currency?: string;
  };

  /**
   * Build a request DTO instance.
   * @param vehicleType vehicle type string
   * @param capacityKilograms capacity in kilograms
   * @param CapacityLiters capacity in liters
   * @param costPerKilometer cost parts
   */
  constructor(
    name: string,
    vehicleType: string,
    capacityKilograms: number,
    CapacityLiters: number,
    costPerKilometer: { amount: number; currency?: string }
  ) {
    this.name = name;
    this.vehicleType = vehicleType;
    this.capacityKilograms = capacityKilograms;
    this.CapacityLiters = CapacityLiters;
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
      data.name.getValue(),
      data.vehicleType,
      data.capacityKilograms.getKilograms(),
      data.capacityLiters.getLiters(),
      {
        amount: data.costPerKilometer.getAmount(),
        currency: data.costPerKilometer.getCurrency().getCode(),
      }
    );
  }
}
