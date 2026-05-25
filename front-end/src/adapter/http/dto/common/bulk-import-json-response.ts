/**
 * Response body from bulk import endpoints ({@code POST .../bulk/import}).
 */
export interface BulkImportJsonResponse {
  success: boolean;
  totalRequested: number;
  createdCount: number;
  failedCount: number;
  failures?: {
    index: number;
    errors?: { field: string; issue: string }[];
  }[];
}
