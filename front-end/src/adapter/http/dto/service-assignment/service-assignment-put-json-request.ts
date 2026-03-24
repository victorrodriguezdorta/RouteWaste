import type { UpdateServiceAssignmentCommand } from '@/application/usecase/service-assignment-management/update-service-assignment/update-service-assignment-command';

/**
 * ServiceAssignmentPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a
 * ServiceAssignment. Only updated fields are included to support partial updates.
 */
export class ServiceAssignmentPutJsonRequest {
  /**
   * Demanda de residuo (valor, unidad de cantidad y unidad de tiempo).
   */
  wasteDemand?: { value: number; quantityUnit: string; timeUnit: string } | undefined;
  /**
   * Distancia en metros.
   */
  distanceMeters?: number | undefined;
  /**
   * Tiempo de servicio en minutos.
   */
  serviceTimeMinutes?: number | undefined;
  /**
   * Coste de transporte (monto y moneda).
   */
  transportCost?: { amount: number; currency?: string } | undefined;

  constructor(
    wasteDemand?: { value: number; quantityUnit: string; timeUnit: string } | undefined,
    distanceMeters?: number | undefined,
    serviceTimeMinutes?: number | undefined,
    transportCost?: { amount: number; currency?: string } | undefined
  ) {
    this.wasteDemand = wasteDemand;
    this.distanceMeters = distanceMeters;
    this.serviceTimeMinutes = serviceTimeMinutes;
    this.transportCost = transportCost;
  }

  /**
   * Mapea un `UpdateServiceAssignmentCommand` a un cuerpo JSON parcial para la petición.
   * @param data Comando con los campos actualizados.
   * @returns Instancia de ServiceAssignmentPutJsonRequest con los campos correspondientes.
   */
  public static toRequest(data: UpdateServiceAssignmentCommand): ServiceAssignmentPutJsonRequest {
    const f = data.updatedFields;
    return new ServiceAssignmentPutJsonRequest(
      f.wasteDemand
        ? { value: f.wasteDemand.getValue(), quantityUnit: f.wasteDemand.getQuantityUnit().getValue(), timeUnit: f.wasteDemand.getTimeUnit().toString() }
        : undefined,
      f.distance ? f.distance.toMeters() : undefined,
      f.serviceTime ? f.serviceTime.getValue() : undefined,
      f.transportCost ? { amount: f.transportCost.getAmount(), currency: f.transportCost.getCurrency().getCode() } : undefined
    );
  }
}
