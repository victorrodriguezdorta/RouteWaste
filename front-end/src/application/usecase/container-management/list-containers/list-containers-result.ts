import type { Container } from '@/domain/entity/container';

/**
 * Result type for listing containers
 */
export interface ListContainersResult {
    items: Container[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
}
