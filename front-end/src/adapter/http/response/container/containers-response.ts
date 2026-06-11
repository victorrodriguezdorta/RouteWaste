import { ContainerJsonResponse } from './container-json-response';

/**
 * ContainersResponse
 *
 * Alias type for an array of `ContainerJsonResponse` objects returned by the
 * backend when listing containers. Keeping this alias allows easy extension
 * (e.g. adding pagination metadata) later without changing call sites.
 */
export type ContainersResponse = ContainerJsonResponse[];
