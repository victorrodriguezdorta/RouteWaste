import type { BulkImportResult } from '@/adapter/http/response/common/bulk-import-result';
import type { CreateVehicleCommand, CreateVehicleResult } from '@/application/usecase/vehicle-management/create-vehicle/create-vehicle-use-case';
import type { DeleteVehicleCommand, DeleteVehicleResult } from '@/application/usecase/vehicle-management/delete-vehicle/delete-vehicle-use-case';
import type { GetVehicleCommand, GetVehicleResult } from '@/application/usecase/vehicle-management/get-vehicle/get-vehicle-use-case';
import type { ListVehiclesCommand, ListVehiclesResult } from '@/application/usecase/vehicle-management/list-vehicles/list-vehicles-use-case';
import type { UpdateVehicleCommand, UpdateVehicleResult } from '@/application/usecase/vehicle-management/update-vehicle/update-vehicle-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Repository interface for Vehicle entity.
 * Defines methods for CRUD operations and filtering of vehicles.
 */
export interface VehicleRepository {
  /**
   * @brief List all vehicles (optional pagination).
   * @param command Optional pagination parameters.
   * @return Either a DataError or a list of Vehicle entities.
   */
  list(command?: ListVehiclesCommand): Promise<Either<DataError, ListVehiclesResult>>;

  /**
   * @brief Get a vehicle by id.
   * @param command Data containing the id of the vehicle to retrieve.
   * @return Either a DataError or the Vehicle entity.
   */
  getById(command: GetVehicleCommand): Promise<Either<DataError, GetVehicleResult>>;

  /**
   * @brief Create a new vehicle.
   * @param command Data required to create the vehicle.
   * @return Either a DataError or the created Vehicle entity.
   */
  create(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>>;

  /**
   * @brief Update an existing vehicle.
   * @param command Data required to update the vehicle.
   * @return Either a DataError or the updated Vehicle entity.
   */
  update(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>>;

  /**
   * @brief Delete a vehicle by id.
   * @param command Data containing the id of the vehicle to delete.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>>;

  /**
   * @brief Import vehicles from a JSON file.
   * @param file JSON file selected by the user.
   * @return Either a DataError or bulk import statistics.
   */
  importFromFile(file: File): Promise<Either<DataError, BulkImportResult>>;
}
