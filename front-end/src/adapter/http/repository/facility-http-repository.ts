import { toEntityTypeStatistics } from '@/adapter/http/mapper/entity-statistics-json-mapper';
import { postBulkImportFile } from '@/adapter/http/repository/bulk-import-http';
import { FacilityPostJsonRequest } from '@/adapter/http/request/facility/facility-post-json-request';
import { FacilityPutJsonRequest } from '@/adapter/http/request/facility/facility-put-json-request';
import type { BulkImportJsonResponse } from '@/adapter/http/response/common/bulk-import-json-response';
import {
  type BulkImportResult,
  toBulkImportResult,
} from '@/adapter/http/response/common/bulk-import-result';
import { FacilityJsonResponse } from '@/adapter/http/response/facility/facility-json-response';
import type { FacilityPageJsonResponse } from '@/adapter/http/response/facility/facility-page-json-response';
import type { FacilityRepository } from '@/application/repository/facility-repository';
import type { CreateFacilityCommand, CreateFacilityResult } from '@/application/usecase/facility-management/create-facility/create-facility-use-case';
import type { DeleteFacilityCommand, DeleteFacilityResult } from '@/application/usecase/facility-management/delete-facility/delete-facility-use-case';
import type { FilterFacilitiesCommand, FilterFacilitiesResult } from '@/application/usecase/facility-management/filter-facilities/filter-facilities-use-case';
import type { GetFacilityCommand, GetFacilityResult } from '@/application/usecase/facility-management/get-facility/get-facility-use-case';
import type { ListFacilitiesCommand, ListFacilitiesResult } from '@/application/usecase/facility-management/list-facilities/list-facilities-use-case';
import type { UpdateFacilityCommand, UpdateFacilityResult } from '@/application/usecase/facility-management/update-facility/update-facility-use-case';
import {
  type ApiError,
  type DataError,
  Either,
  http,
} from '@ull-tfg/ull-tfg-typescript';

/**
 * HTTP repository implementation for Facility entity.
 * 
 * Manages HTTP interactions with the backend API for facility-related operations.
 * Implements the FacilityRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class FacilityHttpRepository implements FacilityRepository {
  /**
   * Base URL for facility API endpoints.
   */
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'facilities/';

  /**
   * HTTP headers for API requests including authorization and content type.
   */
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
   * Retrieve a list of facilities with optional pagination, sort and filters.
   * 
   * @param command Optional pagination, sort and filter parameters.
   * @returns Either a DataError or a paginated list of Facility entities.
   */
  public async list(
    command?: ListFacilitiesCommand
  ): Promise<Either<DataError, ListFacilitiesResult>> {
    let url = this.API_URL;
    const requestedPage = command?.page ?? 0;
    const requestedSize = command?.pageSize ?? 10;

    url += `?page=${requestedPage}&size=${requestedSize}`;
    
    // Add sort parameters
    if (command?.sortBy) {
      url += `&sortBy=${encodeURIComponent(command.sortBy)}&sortOrder=${command.sortOrder ?? 'asc'}`;
    }
    
    // Add filter parameters
    if (command?.facilityType) {
      url += `&facilityType=${encodeURIComponent(command.facilityType)}`;
    }
    if (command?.status) {
      url += `&status=${encodeURIComponent(command.status)}`;
    }
    if (command?.location) {
      url += `&location=${encodeURIComponent(command.location)}`;
    }
    if (command?.name) {
      url += `&name=${encodeURIComponent(command.name)}`;
    }

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityPageJsonResponse | FacilityJsonResponse[]) => {
              let convertedData: ListFacilitiesResult;

              if (Array.isArray(data)) {
                const items = data.map((facility: FacilityJsonResponse) => FacilityJsonResponse.toFacility(facility));
                convertedData = {
                  items,
                  totalElements: items.length,
                  totalPages: items.length > 0 ? 1 : 0,
                  page: requestedPage,
                  size: requestedSize,
                };
              } else {
                const items = data.content.map((facility: FacilityJsonResponse) => FacilityJsonResponse.toFacility(facility));
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
          console.error('[FacilityHttpRepository.list] Error:', error);
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Retrieve a specific facility by its identifier.
   * 
   * @param command Data containing the id of the facility to retrieve.
   * @returns Either a DataError or the Facility entity.
   */
  public async getById(
    command: GetFacilityCommand
  ): Promise<Either<DataError, GetFacilityResult>> {
    const url = `${this.API_URL}${command.facilityId.toString()}`;

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
   * @returns Either a DataError or the created Facility entity.
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
              console.error('[FacilityHttpRepository.create] Error response:', data);
              reject(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          console.error('[FacilityHttpRepository.create] Request error:', error);
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Update an existing facility.
   * 
   * @param command Data required to update the facility (including id).
   * @returns Either a DataError or the updated Facility entity.
   */
  public async update(
    command: UpdateFacilityCommand
  ): Promise<Either<DataError, UpdateFacilityResult>> {
    const url = `${this.API_URL}${command.facilityId.toString()}`;
    const body = FacilityPutJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: FacilityJsonResponse) => {
              resolve(Either.right(FacilityJsonResponse.toFacility(data)));
            });
          } else {
            response.json().then((data: ApiError) => {
              console.error('[FacilityHttpRepository.update] Error response:', data);
              data.kind = 'ApiError';
              resolve(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          console.error('[FacilityHttpRepository.update] Request error:', error);
          resolve(Either.left({ kind: 'UnexpectedError', message: error }));
        });
    });
  }

  /**
   * Delete a facility by its identifier.
   * 
   * @param command Data containing the id of the facility to delete.
   * @returns Either a DataError or true on successful deletion.
   */
  public async delete(
    command: DeleteFacilityCommand
  ): Promise<Either<DataError, DeleteFacilityResult>> {
    const url = `${this.API_URL}${command.facilityId.toString()}`;

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
   * Filter facilities by criteria.
   * 
   * @param command Filter criteria for facilities (facilityType, status, etc).
   * @returns Either a DataError or a list of matching Facility entities.
   */
  public async filter(
    command: FilterFacilitiesCommand
  ): Promise<Either<DataError, FilterFacilitiesResult>> {
    let url = this.API_URL + 'filter?';
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
              const items: FilterFacilitiesResult['items'] = [];

              data.forEach(facility => {
                items.push(FacilityJsonResponse.toFacility(facility));
              });

              resolve(Either.right({ items }));
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
   * Import facilities from a JSON file uploaded as multipart form data.
   *
   * @param file JSON file with an array of facilities or {@code { facilities: [...] }}
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
