import { InfrastructurePlanDetailMapper } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-mapper';
import type { InfrastructurePlanJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-json-response';
import type { InfrastructurePlansResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plans-response';
import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/delete-infrastructure-plan/delete-infrastructure-plan-use-case';
import type { GetInfrastructurePlanCommand, GetInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-use-case';
import type { ListInfrastructurePlansCommand, ListInfrastructurePlansResult } from '@/application/usecase/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-use-case';
import {
  type ApiError,
  type DataError,
  Either,
  http,
} from '@ull-tfg/ull-tfg-typescript';

/**
 * HTTP repository implementation for InfrastructurePlan entity.
 * 
 * Manages HTTP interactions with the backend API for infrastructure plan-related operations.
 * Implements the InfrastructurePlanRepository interface and handles list/detail/delete calls.
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
   * Retrieve a list of infrastructure plans with optional pagination, sorting.
   * 
   * @param command Optional pagination, sort parameters (page, pageSize, sortBy, sortOrder).
   * @returns Either a DataError or a list of InfrastructurePlan entities.
   */
  public async list(
    command?: ListInfrastructurePlansCommand
  ): Promise<Either<DataError, ListInfrastructurePlansResult>> {
    let url = this.API_URL;
    const requestedPage = command?.page ?? 0;
    const requestedSize = command?.pageSize ?? 10;

    // The backend now returns a paginated summary response, so page and size are always sent.
    url += `?page=${requestedPage}&size=${requestedSize}`;
    
    // Add sort parameters
    if (command?.sortBy) {
      url += `&sortBy=${encodeURIComponent(command.sortBy)}&sortOrder=${command.sortOrder ?? 'asc'}`;
    }

    return new Promise((resolve, reject) => {
      http
        .get(url, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: InfrastructurePlansResponse) => {
              resolve(Either.right(data));
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
   * @returns Either a DataError or the InfrastructurePlan entity.
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
              resolve(Either.right(InfrastructurePlanDetailMapper.toReadModel(data)));
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
   * Delete an infrastructure plan by its identifier.
   * 
   * @param command Data containing the id of the infrastructure plan to delete.
   * @returns Either a DataError or true on successful deletion.
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
}
