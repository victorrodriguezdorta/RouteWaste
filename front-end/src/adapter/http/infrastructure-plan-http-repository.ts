import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { CreateInfrastructurePlanCommand, CreateInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/create-infrastructure-plan/create-infrastructure-plan-use-case';
import type { DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/delete-infrastructure-plan/delete-infrastructure-plan-use-case';
import type { GetInfrastructurePlanCommand, GetInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-use-case';
import type { ListInfrastructurePlansCommand, ListInfrastructurePlansResult } from '@/application/usecase/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-use-case';
import type { UpdateInfrastructurePlanCommand, UpdateInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/update-infrastructure-plan/update-infrastructure-plan-use-case';
import type { ValidateInfrastructurePlanCommand, ValidateInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/validate-infrastructure-plan/validate-infrastructure-plan-use-case';
import {
  Either,
  http,
  type ApiError,
  type DataError,
} from '@ull-tfg/ull-tfg-typescript';
// Import DTOs
import { InfrastructurePlanJsonResponse } from './dto/infrastructure-plan/infrastructure-plan-json-response';
import { InfrastructurePlanPostJsonRequest } from './dto/infrastructure-plan/infrastructure-plan-post-json-request';
import { InfrastructurePlanPutJsonRequest } from './dto/infrastructure-plan/infrastructure-plan-put-json-request';

/**
 * HTTP repository implementation for InfrastructurePlan entity.
 * 
 * Manages HTTP interactions with the backend API for infrastructure plan-related operations.
 * Implements the InfrastructurePlanRepository interface and handles CRUD operations through
 * RESTful API calls.
 */
export class InfrastructurePlanHttpRepository implements InfrastructurePlanRepository {
  /**
   * URL base de la API para las operaciones de InfrastructurePlan.
   * Se construye usando la variable de entorno VITE_APP_API_URL.
   */
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'infrastructure-plans';

  /**
   * Cabeceras HTTP utilizadas en las peticiones a la API.
   * Incluye el tipo de contenido y puede incluir autenticación.
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
   * Retrieve a list of infrastructure plans with optional pagination.
   * 
   * @param command Optional pagination parameters (page, pageSize).
   * @return Either a DataError or a list of InfrastructurePlan entities.
   */
  public async list(
    command?: ListInfrastructurePlansCommand
  ): Promise<Either<DataError, ListInfrastructurePlansResult>> {
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
            response.json().then((data: InfrastructurePlanJsonResponse[]) => {
              const convertedData: ListInfrastructurePlansResult = [];
              
              // Transform each JSON response to domain entity
              data.forEach(plan => {
                convertedData.push(InfrastructurePlanJsonResponse.toInfrastructurePlan(plan));
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
   * Retrieve a specific infrastructure plan by its identifier.
   * 
   * @param command Data containing the id of the infrastructure plan to retrieve.
   * @return Either a DataError or the InfrastructurePlan entity.
   */
  public async getById(
    command: GetInfrastructurePlanCommand
  ): Promise<Either<DataError, GetInfrastructurePlanResult>> {
    const url = `${this.API_URL}/${command.planId.toString()}`;

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: InfrastructurePlanJsonResponse) => {
              resolve(Either.right(InfrastructurePlanJsonResponse.toInfrastructurePlan(data)));
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
   * Create a new infrastructure plan in the system.
   * 
   * @param command Data required to create the infrastructure plan.
   * @return Either a DataError or the created InfrastructurePlan entity.
   */
  public async create(
    command: CreateInfrastructurePlanCommand
  ): Promise<Either<DataError, CreateInfrastructurePlanResult>> {
    const body = InfrastructurePlanPostJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(this.API_URL, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: InfrastructurePlanJsonResponse) => {
              resolve(Either.right(InfrastructurePlanJsonResponse.toInfrastructurePlan(data)));
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
   * Update an existing infrastructure plan.
   * 
   * @param command Data required to update the infrastructure plan (including id).
   * @return Either a DataError or the updated InfrastructurePlan entity.
   */
  public async update(
    command: UpdateInfrastructurePlanCommand
  ): Promise<Either<DataError, UpdateInfrastructurePlanResult>> {
    const url = `${this.API_URL}/${command.planId.toString()}`;
    const body = InfrastructurePlanPutJsonRequest.toRequest(command);

    return new Promise((resolve) => {
      http
        .put(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: InfrastructurePlanJsonResponse) => {
              resolve(Either.right(InfrastructurePlanJsonResponse.toInfrastructurePlan(data)));
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
   * Delete an infrastructure plan by its identifier.
   * 
   * @param command Data containing the id of the infrastructure plan to delete.
   * @return Either a DataError or true on successful deletion.
   */
  public async delete(
    command: DeleteInfrastructurePlanCommand
  ): Promise<Either<DataError, DeleteInfrastructurePlanResult>> {
    const url = `${this.API_URL}/${command.planId.toString()}`;

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
   * Validate an infrastructure plan.
   * 
   * @param command Data containing the id of the plan to validate.
   * @return Either a DataError or a validation result.
   */
  public async validate(
    command: ValidateInfrastructurePlanCommand
  ): Promise<Either<DataError, ValidateInfrastructurePlanResult>> {
    const url = `${this.API_URL}/${command.planId.toString()}/validate`;

    return new Promise((resolve) => {
      http
        .post(url, {}, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ValidateInfrastructurePlanResult) => {
              resolve(Either.right(data));
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
}
