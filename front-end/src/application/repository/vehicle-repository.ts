import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateVehicleCommand, CreateVehicleResult } from '../usecase/VehicleManagement/CreateVehicle/create-vehicle-use-case';
import type { GetVehicleCommand, GetVehicleResult } from '../usecase/VehicleManagement/GetVehicle/get-vehicle-use-case';
import type { UpdateVehicleCommand, UpdateVehicleResult } from '../usecase/VehicleManagement/UpdateVehicle/update-vehicle-use-case';
import type { ListVehiclesCommand, ListVehiclesResult } from '../usecase/VehicleManagement/ListVehicles/list-vehicles-use-case';
import type { DeleteVehicleCommand, DeleteVehicleResult } from '../usecase/VehicleManagement/DeleteVehicle/delete-vehicle-use-case';

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
}
