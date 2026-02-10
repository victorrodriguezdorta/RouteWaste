import {
  Either,
  http,
  type ApiError,
  type DataError,
} from '@ull-tfg/ull-tfg-typescript';
import type { VehicleRepository } from '../../application/repository/vehicle-repository';
import type { CreateVehicleCommand, CreateVehicleResult } from '../../application/usecase/vehicle-management/create-vehicle/create-vehicle-use-case';
import type { DeleteVehicleCommand, DeleteVehicleResult } from '../../application/usecase/vehicle-management/delete-vehicle/delete-vehicle-use-case';
import type { GetVehicleCommand, GetVehicleResult } from '../../application/usecase/vehicle-management/get-vehicle/get-vehicle-use-case';
import type { ListVehiclesCommand, ListVehiclesResult } from '../../application/usecase/vehicle-management/list-vehicles/list-vehicles-use-case';
import type { UpdateVehicleCommand, UpdateVehicleResult } from '../../application/usecase/vehicle-management/update-vehicle/update-vehicle-use-case';
// Import DTOs (assuming they exist)
import { VehicleJsonResponse } from './dto/vehicle/vehicle-json-response';
import { VehiclePostJsonRequest } from './dto/vehicle/vehicle-post-json-request';
import { VehiclePutJsonRequest } from './dto/vehicle/vehicle-put-json-request';

/**
 * HTTP repository implementation for Vehicle entity.
 * 
 * Manages HTTP interactions with the backend API for vehicle-related operations.
 * Implements the VehicleRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class VehicleHttpRepository implements VehicleRepository {
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'vehicles';
  private headers: Headers = new Headers();

  /**
   * Initialize the HTTP repository with authentication headers.
   * 
   * Sets up the required headers for API communication including
   * authorization token and content type.
   */
  constructor() {
    // TODO: Add keycloak integration when available
    // this.headers.append('Authorization', `Bearer ${this.keycloak?.token}`);
    this.headers.append('Content-Type', 'application/json');
  }
  
  /**
   * Retrieve a list of vehicles with optional pagination.
   * 
   * @param command Optional pagination parameters (page, pageSize).
   * @return Either a DataError or a list of Vehicle entities.
   */
  public async list(
    command?: ListVehiclesCommand
  ): Promise<Either<DataError, ListVehiclesResult>> {
    let url = this.API_URL;
    
    // Build query parameters for pagination
    if (command?.page !== undefined && command?.pageSize !== undefined) {
      url += `?page=${command.page}&size=${command.pageSize}`;
    }

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: VehicleJsonResponse[]) => {
              const convertedData: ListVehiclesResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(vehicle => {
                convertedData.push(VehicleJsonResponse.toVehicle(vehicle));
              });
              
              resolve(Either.right(convertedData));
            });
          } else {
            response.json().then((data: ApiError) => {
              reject(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Retrieve a specific vehicle by its identifier.
   * 
   * @param command Data containing the id of the vehicle to retrieve.
   * @return Either a DataError or the Vehicle entity.
   */
  public async getById(
    command: GetVehicleCommand
  ): Promise<Either<DataError, GetVehicleResult>> {
    const url = `${this.API_URL}/${command.vehicleId.toString()}`;

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: VehicleJsonResponse) => {
              resolve(Either.right(VehicleJsonResponse.toVehicle(data)));
            });
          } else {
            response.json().then((data: ApiError) => {
              reject(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Create a new vehicle in the system.
   * 
   * @param command Data required to create the vehicle.
   * @return Either a DataError or the created Vehicle entity.
   */
  public async create(
    command: CreateVehicleCommand
  ): Promise<Either<DataError, CreateVehicleResult>> {
    const body = VehiclePostJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(this.API_URL, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: VehicleJsonResponse) => {
              resolve(Either.right(VehicleJsonResponse.toVehicle(data)));
            });
          } else {
            response.json().then((data: ApiError) => {
              reject(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Update an existing vehicle.
   * 
   * @param command Data required to update the vehicle (including id).
   * @return Either a DataError or the updated Vehicle entity.
   */
  public async update(
    command: UpdateVehicleCommand
  ): Promise<Either<DataError, UpdateVehicleResult>> {
    const url = `${this.API_URL}/${command.vehicleId.toString()}`;
    const body = VehiclePutJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: VehicleJsonResponse) => {
              resolve(Either.right(VehicleJsonResponse.toVehicle(data)));
            });
          } else {
            response.json().then((data: ApiError) => {
              data.kind = 'ApiError';
              resolve(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Delete a vehicle by its identifier.
   * 
   * @param command Data containing the id of the vehicle to delete.
   * @return Either a DataError or true on successful deletion.
   */
  public async delete(
    command: DeleteVehicleCommand
  ): Promise<Either<DataError, DeleteVehicleResult>> {
    const url = `${this.API_URL}/${command.vehicleId.toString()}`;

    return new Promise((resolve) => {
      http
        .delete(url, this.headers)
        .then(response => {
          if (response.ok) {
            resolve(Either.right(true));
          } else {
            response.json().then((data: ApiError) => {
              data.kind = 'ApiError';
              resolve(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }
}
