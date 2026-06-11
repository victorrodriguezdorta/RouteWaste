import type { ContainerPageJsonResponse } from './container-page-json-response';
import type { ContainersResponse } from './containers-response';

/**
 * Union response type kept for compatibility with non-paginated backends.
 */
export type ContainersListResponse = ContainersResponse | ContainerPageJsonResponse;
