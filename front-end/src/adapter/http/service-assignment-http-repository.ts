import {
  Either,
  http,
  type ApiError,
  type DataError,
} from '@ull-tfg/ull-tfg-typescript';
import type { ServiceAssignmentRepository } from '../../application/repository/service-assignment-repository';
import type { AssignContainerToFacilityCommand, AssignContainerToFacilityResult } from '../../application/usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-use-case';
import type { ListServiceAssignmentsCommand, ListServiceAssignmentsResult } from '../../application/usecase/service-assignment-management/list-service-assignments/list-service-assignments-use-case';
import type { RemoveServiceAssignmentCommand, RemoveServiceAssignmentResult } from '../../application/usecase/service-assignment-management/remove-service-assignment/remove-service-assignment-use-case';
import type { UpdateServiceAssignmentCommand, UpdateServiceAssignmentResult } from '../../application/usecase/service-assignment-management/update-service-assignment/update-service-assignment-use-case';
// Import DTOs
import { ServiceAssignmentJsonResponse } from './dto/service-assignment/service-assignment-json-response';
import { ServiceAssignmentPostJsonRequest } from './dto/service-assignment/service-assignment-post-json-request';
import { ServiceAssignmentPutJsonRequest } from './dto/service-assignment/service-assignment-put-json-request';

/**
 * HTTP repository implementation for ServiceAssignment entity.
 * 
 * Manages HTTP interactions with the backend API for service assignment-related operations.
 * Implements the ServiceAssignmentRepository interface and handles operations for assigning
 * containers to facilities through RESTful API calls.
 */
export class ServiceAssignmentHttpRepository implements ServiceAssignmentRepository {
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'service-assignments';
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
   * Retrieve a list of service assignments with optional pagination and filters.
   * 
   * @param command Optional pagination and filter parameters (page, pageSize).
   * @return Either a DataError or a list of ServiceAssignment entities.
   */
  public async list(
    command?: ListServiceAssignmentsCommand
  ): Promise<Either<DataError, ListServiceAssignmentsResult>> {
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
            response.json().then((data: ServiceAssignmentJsonResponse[]) => {
              const convertedData: ListServiceAssignmentsResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(assignment => {
                convertedData.push(ServiceAssignmentJsonResponse.toServiceAssignment(assignment));
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
   * Assign a container to a facility in the system.
   * 
   * @param command Data required to create the service assignment.
   * @return Either a DataError or the created ServiceAssignment entity.
   */
  public async assignContainerToFacility(
    command: AssignContainerToFacilityCommand
  ): Promise<Either<DataError, AssignContainerToFacilityResult>> {
    const body = ServiceAssignmentPostJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(this.API_URL, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ServiceAssignmentJsonResponse) => {
              resolve(Either.right(ServiceAssignmentJsonResponse.toServiceAssignment(data)));
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
   * Update an existing service assignment.
   * 
   * @param command Data required to update the assignment (including id).
   * @return Either a DataError or the updated ServiceAssignment entity.
   */
  public async update(
    command: UpdateServiceAssignmentCommand
  ): Promise<Either<DataError, UpdateServiceAssignmentResult>> {
    const url = `${this.API_URL}/${command.assignmentId.toString()}`;
    const body = ServiceAssignmentPutJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ServiceAssignmentJsonResponse) => {
              resolve(Either.right(ServiceAssignmentJsonResponse.toServiceAssignment(data)));
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
   * Remove a service assignment by its identifier.
   * 
   * @param command Data containing the id of the assignment to remove.
   * @return Either a DataError or true on successful removal.
   */
  public async delete(
    command: RemoveServiceAssignmentCommand
  ): Promise<Either<DataError, RemoveServiceAssignmentResult>> {
    const url = `${this.API_URL}/${command.assignmentId.toString()}`;

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
