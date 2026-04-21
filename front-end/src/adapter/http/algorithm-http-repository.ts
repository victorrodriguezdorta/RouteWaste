import { AlgorithmExecutionRequestJson } from '@/adapter/http/dto/algorithm/algorithm-execution-request-json';
import type { AlgorithmRepository } from '@/application/repository/algorithm-repository';
import type { CreateAlgorithmCommand, CreateAlgorithmResult } from '@/application/usecase/algorithm-management/create-algorithm/create-algorithm-use-case';
import type { ApiError, DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import { http } from '@ull-tfg/ull-tfg-typescript';

/**
 * AlgorithmJsonResponse
 *
 * JSON response from the backend after algorithm execution.
 */
interface AlgorithmJsonResponse {
  executionId: string;
  status: string;
  message: string;
}

/**
 * AlgorithmHttpRepository
 *
 * HTTP repository implementation for Algorithm execution.
 * Manages HTTP interactions with the backend API for algorithm-related operations.
 * Implements the AlgorithmRepository interface and handles algorithm execution through RESTful API calls.
 */
export class AlgorithmHttpRepository implements AlgorithmRepository {
  /**
   * Base URL for the API endpoint for algorithm operations.
   */
  private readonly API_URL = import.meta.env.VITE_APP_API_URL + 'algorithms/';

  /**
   * HTTP headers for API requests including content type and authentication.
   */
  private headers: Headers = new Headers();

  /**
   * Initialize the HTTP repository with authentication headers.
   * Sets up the required headers for API communication including
   * authorization token and content type.
   */
  constructor() {
    // TODO: Add keycloak integration when available
    // this.headers.append('Authorization', `Bearer ${this.keycloak?.token}`);
    this.headers.append('Content-Type', 'application/json');
  }

  /**
   * Create and execute an algorithm execution request.
   * Sends the algorithm execution parameters to the backend for processing.
   *
   * @param command Data required to execute the algorithm.
   * @returns Either a DataError or the algorithm execution result.
   */
  public async create(
    command: CreateAlgorithmCommand
  ): Promise<Either<DataError, CreateAlgorithmResult>> {
    const body = AlgorithmExecutionRequestJson.toRequest(command);
    const url = this.API_URL + 'execute';

    return new Promise((resolve) => {
      http
        .post(url, body, this.headers)
        .then((response: Response) => {
          if (response.ok) {
            response.json().then((data: AlgorithmJsonResponse) => {
              const result: CreateAlgorithmResult = {
                executionId: data.executionId,
                status: data.status,
                message: data.message,
              };
              resolve(Either.right(result));
            });
          } else {
            response.json().then((data: ApiError) => {
              resolve(Either.left(data));
            });
          }
        })
        .catch((error: any) => {
          resolve(Either.left({ kind: 'UnexpectedError', message: error.message || error } as DataError));
        });
    });
  }
}
