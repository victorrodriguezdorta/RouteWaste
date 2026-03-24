import type { UpdateVehicleCommand } from '@/application/usecase/vehicle-management/update-vehicle/update-vehicle-command';

/**
 * VehiclePutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating an existing
 * Vehicle. All fields are optional to support partial updates; primitive types
 * are used so the object can be serialized directly.
 */
export class VehiclePutJsonRequest {
  /** Optional vehicle type string. */
  vehicleType?: string;
  /** Optional transport capacity parts. */
  transportCapacity?: {
    value: number;
    quantityUnit: string;
    timeUnit: string;
  };
  /** Optional cost per kilometer parts. */
  costPerKilometer?: {
    amount: number;
    currency?: string;
  };

  /**
   * Create a PUT request DTO instance. All params are optional.
   * @param vehicleType optional vehicle type
   * @param transportCapacity optional capacity parts
   * @param costPerKilometer optional cost parts
   */
  constructor(
    vehicleType?: string,
    transportCapacity?: { value: number; quantityUnit: string; timeUnit: string },
    costPerKilometer?: { amount: number; currency?: string }
  ) {
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
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
      f.vehicleType,
      f.transportCapacity
        ? {
            value: f.transportCapacity.getValue(),
            quantityUnit: f.transportCapacity.getQuantityUnit().getValue(),
            timeUnit: f.transportCapacity.getTimeUnit().toString(),
          }
        : undefined,
      f.costPerKilometer
        ? { amount: f.costPerKilometer.getAmount(), currency: f.costPerKilometer.getCurrency().getCode() }
        : undefined
    );
  }
}
