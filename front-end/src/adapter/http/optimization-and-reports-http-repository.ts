import {
  Either,
  type DataError,
  http,
  type ApiError,
} from '@ull-tfg/ull-tfg-typescript';
import type { OptimizationAndReportsRepository } from '../../application/repository/optimization-and-reports-repository';
import type { GenerateReportCommand, GenerateReportResult } from '../../application/usecase/optimization-and-reports/generate-report/generate-report-use-case';
import type { ExportReportCommand, ExportReportResult } from '../../application/usecase/optimization-and-reports/export-report/export-report-use-case';
import type { CalculateCostsCommand, CostCalculationResult } from '../../application/usecase/optimization-and-reports/calculate-costs/calculate-costs-use-case';
import type { OptimizeRoutesCommand, OptimizeRoutesResult } from '../../application/usecase/optimization-and-reports/optimize-routes/optimize-routes-use-case';
// Import DTOs (assuming they exist)
import { GenerateReportJsonRequest } from './dto/generate-report-json-request';
import { ExportReportJsonRequest } from './dto/export-report-json-request';
import { CalculateCostsJsonRequest } from './dto/calculate-costs-json-request';
import { OptimizeRoutesJsonRequest } from './dto/optimize-routes-json-request';

/**
 * HTTP repository implementation for optimization and reports operations.
 * 
 * Manages HTTP interactions with the backend API for report generation, export,
 * cost calculation, and route optimization. Implements the OptimizationAndReportsRepository
 * interface and handles operations through RESTful API calls.
 */
export class OptimizationAndReportsHttpRepository implements OptimizationAndReportsRepository {
  private readonly API_URL = import.meta.env.VITE_APP_API_URL;
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
   * Generate a report based on specified parameters.
   * 
   * @param command Parameters describing the report to generate (type, dates, filters, format).
   * @return Either a DataError or the generated report result.
   */
  public async generateReport(
    command: GenerateReportCommand
  ): Promise<Either<DataError, GenerateReportResult>> {
    const url = `${this.API_URL}reports/generate`;
    const body = GenerateReportJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: GenerateReportResult) => {
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

  /**
   * Export a report in the specified format.
   * 
   * @param command Parameters describing the report to export (reportId, format, options).
   * @return Either a DataError or an export result (e.g., URL or file id).
   */
  public async exportReport(
    command: ExportReportCommand
  ): Promise<Either<DataError, ExportReportResult>> {
    const url = `${this.API_URL}reports/export`;
    const body = ExportReportJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: ExportReportResult) => {
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

  /**
   * Calculate costs for a given scenario.
   * 
   * @param command Input data required to compute costs (planId, facilityIds, options).
   * @return Either a DataError or the computed cost calculation result.
   */
  public async calculateCosts(
    command: CalculateCostsCommand
  ): Promise<Either<DataError, CostCalculationResult>> {
    const url = `${this.API_URL}optimization/calculate-costs`;
    const body = CalculateCostsJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: CostCalculationResult) => {
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

  /**
   * Optimize routes for containers, facilities, vehicles, etc.
   * 
   * @param command Input data with containers, facilities, vehicles and optimization parameters.
   * @return Either a DataError or the optimized routes result.
   */
  public async optimizeRoutes(
    command: OptimizeRoutesCommand
  ): Promise<Either<DataError, OptimizeRoutesResult>> {
    const url = `${this.API_URL}optimization/routes`;
    const body = OptimizeRoutesJsonRequest.toRequest(command);

    return new Promise((resolve, reject) => {
      http
        .post(url, body, this.headers)
        .then(response => {
          if (response.ok) {
            response.json().then((data: OptimizeRoutesResult) => {
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
