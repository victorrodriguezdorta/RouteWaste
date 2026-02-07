import {
  Either,
  type DataError,
  http,
  type ApiError,
} from '@ull-tfg/ull-tfg-typescript';
import type { FacilityRepository } from '../../application/repository/facility-repository';
import type { CreateFacilityCommand, CreateFacilityResult } from '../../application/usecase/FacilityManagement/CreateFacility/create-facility-use-case';
import type { GetFacilityCommand, GetFacilityResult } from '../../application/usecase/FacilityManagement/GetFacility/get-facility-use-case';
import type { UpdateFacilityCommand, UpdateFacilityResult } from '../../application/usecase/FacilityManagement/UpdateFacility/update-facility-use-case';
import type { ListFacilitiesCommand, ListFacilitiesResult } from '../../application/usecase/FacilityManagement/ListFacilities/list-facilities-use-case';
import type { FilterFacilitiesCommand, FilterFacilitiesResult } from '../../application/usecase/FacilityManagement/FilterFacilities/filter-facilities-use-case';
import type { DeleteFacilityCommand, DeleteFacilityResult } from '../../application/usecase/FacilityManagement/DeleteFacility/delete-facility-use-case';
// Import DTOs (assuming they exist)
import { FacilityJsonResponse } from './dto/facility-json-response';
import { FacilityPostJsonRequest } from './dto/facility-post-json-request';
import { FacilityPutJsonRequest } from './dto/facility-put-json-request';
import type { FacilitiesResponse } from './dto/facilities-response';

/**
 * HTTP repository implementation for Facility entity.
 * 
 * Manages HTTP interactions with the backend API for facility-related operations.
 * Implements the FacilityRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class FacilityHttpRepository implements FacilityRepository {
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'facilities';
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
   * Retrieve a list of facilities with optional pagination.
   * 
   * @param command Optional pagination parameters (page, pageSize).
   * @return Either a DataError or a list of Facility entities.
   */
  public async list(
    command?: ListFacilitiesCommand
  ): Promise<Either<DataError, ListFacilitiesResult>> {
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
            response.json().then((data: FacilityJsonResponse[]) => {
              const convertedData: ListFacilitiesResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(facility => {
                convertedData.push(FacilityJsonResponse.toFacility(facility));
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
   * Retrieve a specific facility by its identifier.
   * 
   * @param command Data containing the id of the facility to retrieve.
   * @return Either a DataError or the Facility entity.
   */
  public async getById(
    command: GetFacilityCommand
  ): Promise<Either<DataError, GetFacilityResult>> {
    const url = `${this.API_URL}/${command.facilityId.toString()}`;

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse) => {
              resolve(Either.right(FacilityJsonResponse.toFacility(data)));
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
   * Create a new facility in the system.
   * 
   * @param command Data required to create the facility.
   * @return Either a DataError or the created Facility entity.
   */
  public async create(
    command: CreateFacilityCommand
  ): Promise<Either<DataError, CreateFacilityResult>> {
    const body = FacilityPostJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(this.API_URL, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse) => {
              resolve(Either.right(FacilityJsonResponse.toFacility(data)));
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
   * Update an existing facility.
   * 
   * @param command Data required to update the facility (including id).
   * @return Either a DataError or the updated Facility entity.
   */
  public async update(
    command: UpdateFacilityCommand
  ): Promise<Either<DataError, UpdateFacilityResult>> {
    const url = `${this.API_URL}/${command.facilityId.toString()}`;
    const body = FacilityPutJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse) => {
              resolve(Either.right(FacilityJsonResponse.toFacility(data)));
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
   * Delete a facility by its identifier.
   * 
   * @param command Data containing the id of the facility to delete.
   * @return Either a DataError or the deleted Facility entity.
   */
  public async delete(
    command: DeleteFacilityCommand
  ): Promise<Either<DataError, DeleteFacilityResult>> {
    const url = `${this.API_URL}/${command.facilityId.toString()}`;

    return new Promise((resolve, reject) => {
      http
        .delete(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse) => {
              resolve(Either.right(FacilityJsonResponse.toFacility(data)));
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
   * Filter facilities by criteria.
   * 
   * @param command Filter criteria for facilities (facilityType, status, etc).
   * @return Either a DataError or a list of matching Facility entities.
   */
  public async filter(
    command: FilterFacilitiesCommand
  ): Promise<Either<DataError, FilterFacilitiesResult>> {
    let url = this.API_URL + '/filter?';
    const params: string[] = [];

    // Build query parameters for filtering
    if (command.facilityType !== undefined) {
      params.push(`facilityType=${encodeURIComponent(command.facilityType)}`);
    }
    if (command.status !== undefined) {
      params.push(`status=${encodeURIComponent(command.status)}`);
    }
    if (command.minCapacity !== undefined) {
      params.push(`minCapacity=${command.minCapacity}`);
    }
    if (command.maxCapacity !== undefined) {
      params.push(`maxCapacity=${command.maxCapacity}`);
    }

    url += params.join('&');

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse[]) => {
              const convertedData: FilterFacilitiesResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(facility => {
                convertedData.push(FacilityJsonResponse.toFacility(facility));
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
}
