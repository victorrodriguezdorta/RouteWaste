import type { UpdateVehicleCommand } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-command';

/**
 * VehiclePutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating an existing
 * Vehicle. All fields are optional to support partial updates; primitive types
 * are used so the object can be serialized directly.
 */
export class VehiclePutJsonRequest {
  /** Optional human-readable name. */
  name?: string;
  /** Optional vehicle type string. */
  vehicleType?: string;
  /** Optional capacity in kilograms. */
  capacityKilograms?: number;
  /** Optional capacity in liters. */
  CapacityLiters?: number;
  /** Optional cost per kilometer parts. */
  costPerKilometer?: {
    amount: number;
    currency?: string;
  };

  /**
   * Create a PUT request DTO instance. All params are optional.
   * @param vehicleType optional vehicle type
   * @param capacityKilograms optional capacity in kilograms
   * @param CapacityLiters optional capacity in liters
   * @param costPerKilometer optional cost parts
   */
  constructor(
    name?: string,
    vehicleType?: string,
    capacityKilograms?: number,
    CapacityLiters?: number,
    costPerKilometer?: { amount: number; currency?: string }
  ) {
    this.name = name;
    this.vehicleType = vehicleType;
    this.capacityKilograms = capacityKilograms;
    this.CapacityLiters = CapacityLiters;
    this.costPerKilometer = costPerKilometer;
  }

  /**
   * Mapea un `UpdateVehicleCommand` (actualización parcial de dominio) a este DTO.
   * Extrae valores primitivos de cualquier value object proporcionado.
   * @param data Comando de actualización de dominio.
   * @returns Una instancia de VehiclePutJsonRequest con los datos mapeados.
   */
  public static toRequest(data: UpdateVehicleCommand): VehiclePutJsonRequest {
    const f = data.updatedFields;
    return new VehiclePutJsonRequest(
      f.name?.getValue(),
      f.vehicleType,
      f.capacityKilograms
        ? f.capacityKilograms.getKilograms()
        : undefined,
      f.capacityLiters
        ? f.capacityLiters.getLiters()
        : undefined,
      f.costPerKilometer
        ? { amount: f.costPerKilometer.getAmount(), currency: f.costPerKilometer.getCurrency().getCode() }
        : undefined
    );
  }
}
