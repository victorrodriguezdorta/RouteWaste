/** Shape of error objects returned by the HTTP adapter. */
export type BackendErrorPayload = {
  kind?: string;
  error?: unknown;
  message?: unknown;
  details?: unknown;
  status?: string;
};
