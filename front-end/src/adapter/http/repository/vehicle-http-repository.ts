import { toEntityTypeStatistics } from '@/adapter/http/mapper/entity-statistics-json-mapper';
import { postBulkImportFile } from '@/adapter/http/repository/bulk-import-http';
import { VehiclePostJsonRequest } from '@/adapter/http/request/vehicle/vehicle-post-json-request';
import { VehiclePutJsonRequest } from '@/adapter/http/request/vehicle/vehicle-put-json-request';
import type { BulkImportJsonResponse } from '@/adapter/http/response/common/bulk-import-json-response';
import {
  type BulkImportResult,
  toBulkImportResult,
} from '@/adapter/http/response/common/bulk-import-result';
import { VehicleJsonResponse } from '@/adapter/http/response/vehicle/vehicle-json-response';
import type { VehiclePageJsonResponse } from '@/adapter/http/response/vehicle/vehicle-page-json-response';
import type { VehicleRepository } from '@/application/repository/vehicle-repository';
import type { CreateVehicleCommand, CreateVehicleResult } from '@/application/usecase/vehicle-management/create-vehicle/create-vehicle-use-case';
import type { DeleteVehicleCommand, DeleteVehicleResult } from '@/application/usecase/vehicle-management/delete-vehicle/delete-vehicle-use-case';
import type { GetVehicleCommand, GetVehicleResult } from '@/application/usecase/vehicle-management/get-vehicle/get-vehicle-use-case';
import type { ListVehiclesCommand, ListVehiclesResult } from '@/application/usecase/vehicle-management/list-vehicles/list-vehicles-use-case';
import type { UpdateVehicleCommand, UpdateVehicleResult } from '@/application/usecase/vehicle-management/update-vehicle/update-vehicle-use-case';
import {
  type ApiError,
  type DataError,
  Either,
  http,
} from '@ull-tfg/ull-tfg-typescript';

/**
 * HTTP repository implementation for Vehicle entity.
 * 
 * Manages HTTP interactions with the backend API for vehicle-related operations.
 * Implements the VehicleRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class VehicleHttpRepository implements VehicleRepository {
  /** The base API URL for vehicle endpoints. */
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'vehicles/';

  /** HTTP headers for API requests including authentication and content type. */
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
   * @returns Either a DataError or a list of Vehicle entities.
   */
  public async list(
    command?: ListVehiclesCommand
  ): Promise<Either<DataError, ListVehiclesResult>> {
    let url = this.API_URL;
    const requestedPage = command?.page ?? 0;
    const requestedSize = command?.pageSize ?? 10;

    // Build query parameters for pagination, sort and filter
    url += `?page=${requestedPage}&size=${requestedSize}`;
    if (command?.sortBy) {
      url += `&sortBy=${encodeURIComponent(command.sortBy)}&sortOrder=${command.sortOrder ?? 'asc'}`;
    }
    if (command?.vehicleType) {
      url += `&vehicleType=${encodeURIComponent(command.vehicleType)}`;
    }
    if (command?.name) {
      url += `&name=${encodeURIComponent(command.name)}`;
    }

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: VehiclePageJsonResponse | VehicleJsonResponse[]) => {
              // Backward compatibility: accept old array response or new paginated response.
              let convertedData: ListVehiclesResult;

              if (Array.isArray(data)) {
                const items = data.map((vehicle: VehicleJsonResponse) => VehicleJsonResponse.toVehicle(vehicle));
                convertedData = {
                  items,
                  totalElements: items.length,
                  totalPages: items.length > 0 ? 1 : 0,
                  page: requestedPage,
                  size: requestedSize,
                };
              } else {
                const items = data.content.map((vehicle: VehicleJsonResponse) => VehicleJsonResponse.toVehicle(vehicle));
                convertedData = {
                  items,
                  totalElements: data.totalElements,
                  totalPages: data.totalPages,
                  page: data.page,
                  size: data.size,
                  statistics: toEntityTypeStatistics(data.statistics),
                };
              }

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
   * @returns Either a DataError or the Vehicle entity.
   */
  public async getById(
    command: GetVehicleCommand
  ): Promise<Either<DataError, GetVehicleResult>> {
    const url = `${this.API_URL}${command.vehicleId.toString()}`;

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
   * @returns Either a DataError or the created Vehicle entity.
   */
  public async create(
    command: CreateVehicleCommand
  ): Promise<Either<DataError, CreateVehicleResult>> {
    const body = VehiclePostJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .post(this.API_URL, body, this.headers)
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
   * Update an existing vehicle.
   * 
   * @param command Data required to update the vehicle (including id).
   * @returns Either a DataError or the updated Vehicle entity.
   */
  public async update(
    command: UpdateVehicleCommand
  ): Promise<Either<DataError, UpdateVehicleResult>> {
    const url = `${this.API_URL}${command.vehicleId.toString()}`;
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
   * @returns Either a DataError or true on successful deletion.
   */
  public async delete(
    command: DeleteVehicleCommand
  ): Promise<Either<DataError, DeleteVehicleResult>> {
    const url = `${this.API_URL}${command.vehicleId.toString()}`;

    return new Promise((resolve) => {
      http
        .delete(url, this.headers)
        .then(response => {
          if (response.ok) {
            resolve(Either.right({ success: true }));
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
   * Import vehicles from a JSON file uploaded as multipart form data.
   *
   * @param file JSON file with an array of vehicles or {@code { vehicles: [...] }}
   * @returns Either a DataError or bulk import statistics
   */
  public async importFromFile(
    file: File
  ): Promise<Either<DataError, BulkImportResult>> {
    const url = `${this.API_URL}bulk/import`;

    return new Promise((resolve) => {
      postBulkImportFile(url, file)
        .then(async (response) => {
          const body = await response.json();
          if (response.ok) {
            resolve(Either.right(toBulkImportResult(body as BulkImportJsonResponse)));
            return;
          }
          const apiError = body as ApiError;
          apiError.kind = 'ApiError';
          resolve(Either.left(apiError));
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }
}
