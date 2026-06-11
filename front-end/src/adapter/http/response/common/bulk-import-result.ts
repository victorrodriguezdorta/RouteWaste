import type { BulkImportJsonResponse } from './bulk-import-json-response';

/**
 * Normalized bulk import statistics returned to the application layer.
 */
export interface BulkImportResult {
  success: boolean;
  totalRequested: number;
  createdCount: number;
  failedCount: number;
}

/**
 * @param data raw API response
 * @returns normalized bulk import result
 */
export function toBulkImportResult(data: BulkImportJsonResponse): BulkImportResult {
  return {
    success: data.success,
    totalRequested: data.totalRequested,
    createdCount: data.createdCount,
    failedCount: data.failedCount,
  };
}
