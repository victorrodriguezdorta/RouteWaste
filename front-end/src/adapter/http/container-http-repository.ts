import { ContainerJsonResponse } from '@/adapter/http/dto/container/container-json-response';
import type { ContainerPageJsonResponse } from '@/adapter/http/dto/container/container-page-json-response';
import { ContainerPostJsonRequest } from '@/adapter/http/dto/container/container-post-json-request';
import { ContainerPutJsonRequest } from '@/adapter/http/dto/container/container-put-json-request';
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { CreateContainerCommand, CreateContainerResult } from '@/application/usecase/container-management/create-container/create-container-use-case';
import type { DeleteContainerCommand, DeleteContainerResult } from '@/application/usecase/container-management/delete-container/delete-container-use-case';
import type { FilterContainersCommand, FilterContainersResult } from '@/application/usecase/container-management/filter-containers/filter-containers-use-case';
import type { GetContainerCommand, GetContainerResult } from '@/application/usecase/container-management/get-container/get-container-use-case';
import type { ListContainersCommand, ListContainersResult } from '@/application/usecase/container-management/list-containers/list-containers-use-case';
import type { UpdateContainerCommand, UpdateContainerResult } from '@/application/usecase/container-management/update-container/update-container-use-case';
import type {
    ApiError,
    DataError,
} from '@ull-tfg/ull-tfg-typescript';
import {
    Either,
    http,
} from '@ull-tfg/ull-tfg-typescript';

/**
 * HTTP repository implementation for Container entity.
 * 
 * Manages HTTP interactions with the backend API for container-related operations.
 * Implements the ContainerRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class ContainerHttpRepository implements ContainerRepository {
  /**
   * Base URL for the API endpoint for container operations.
   */
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'containers/';

  /**
   * HTTP headers for API requests including content type and authentication.
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
   * Retrieve a list of containers with optional pagination, sort and filtering.
   * 
   * @param command Optional pagination, sort and filter parameters.
   * @returns Either a DataError or a paginated list of Container entities.
   */
  public async list(
    command?: ListContainersCommand
  ): Promise<Either<DataError, ListContainersResult>> {
    let url = this.API_URL;
    const requestedPage = command?.page ?? 0;
    const requestedSize = command?.pageSize ?? 10;

    url += `?page=${requestedPage}&size=${requestedSize}`;
    
    // Add sort parameters
    if (command?.sortBy) {
      url += `&sortBy=${encodeURIComponent(command.sortBy)}&sortOrder=${command.sortOrder ?? 'asc'}`;
    }
    
    // Add filter parameters
    if (command?.wasteType) {
      url += `&wasteType=${encodeURIComponent(command.wasteType)}`;
    }
    if (command?.serviceZone) {
      url += `&serviceZone=${encodeURIComponent(command.serviceZone)}`;
    }
    if (command?.location) {
      url += `&location=${encodeURIComponent(command.location)}`;
    }

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ContainerPageJsonResponse | ContainerJsonResponse[]) => {
              let convertedData: ListContainersResult;

              if (Array.isArray(data)) {
                const items = data.map((container: ContainerJsonResponse) => ContainerJsonResponse.toContainer(container));
                convertedData = {
                  items,
                  totalElements: items.length,
                  totalPages: items.length > 0 ? 1 : 0,
                  page: requestedPage,
                  size: requestedSize,
                };
              } else {
                const items = data.content.map((container: ContainerJsonResponse) => ContainerJsonResponse.toContainer(container));
                convertedData = {
                  items,
                  totalElements: data.totalElements,
                  totalPages: data.totalPages,
                  page: data.page,
                  size: data.size,
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
   * Retrieve a specific container by its identifier.
   * 
   * @param command Data containing the id of the container to retrieve.
   * @returns Either a DataError or the Container entity.
   */
  public async getById(
    command: GetContainerCommand
  ): Promise<Either<DataError, GetContainerResult>> {
    const url = `${this.API_URL}${command.containerId.toString()}`;

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ContainerJsonResponse) => {
              resolve(Either.right(ContainerJsonResponse.toContainer(data)));
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
   * Create a new container in the system.
   * 
   * @param command Data required to create the container.
   * @returns Either a DataError or the created Container entity.
   */
  public async create(
    command: CreateContainerCommand
  ): Promise<Either<DataError, CreateContainerResult>> {
    const body = ContainerPostJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(this.API_URL, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ContainerJsonResponse) => {
              resolve(Either.right(ContainerJsonResponse.toContainer(data)));
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
   * Update an existing container.
   * 
   * @param command Data required to update the container (including id).
   * @returns Either a DataError or the updated Container entity.
   */
  public async update(
    command: UpdateContainerCommand
  ): Promise<Either<DataError, UpdateContainerResult>> {
    const url = `${this.API_URL}${command.containerId.toString()}`;
    const body = ContainerPutJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ContainerJsonResponse) => {
              resolve(Either.right(ContainerJsonResponse.toContainer(data)));
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
   * Delete a container by its identifier.
   * 
   * @param command Data containing the id of the container to delete.
   * @returns Either a DataError or true on successful deletion.
   */
  public async delete(
    command: DeleteContainerCommand
  ): Promise<Either<DataError, DeleteContainerResult>> {
    const url = `${this.API_URL}${command.containerId.toString()}`;

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

  /**
   * Filter containers by criteria.
   * 
   * @param command Filter criteria for containers (wasteType, serviceZone, etc).
   * @returns Either a DataError or a list of matching Container entities.
   */
  public async filter(
    command: FilterContainersCommand
  ): Promise<Either<DataError, FilterContainersResult>> {
    let url = this.API_URL + 'filter?';
    const params: string[] = [];

    // Build query parameters for filtering
    if (command.wasteType !== undefined) {
      params.push(`wasteType=${encodeURIComponent(command.wasteType)}`);
    }
    if (command.serviceZone !== undefined) {
      params.push(`serviceZone=${encodeURIComponent(command.serviceZone)}`);
    }
    if (command.minDemand !== undefined) {
      params.push(`minDemand=${command.minDemand}`);
    }
    if (command.maxDemand !== undefined) {
      params.push(`maxDemand=${command.maxDemand}`);
    }

    url += params.join('&');

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ContainerJsonResponse[]) => {
              const convertedData: FilterContainersResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(container => {
                convertedData.push(ContainerJsonResponse.toContainer(container));
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
