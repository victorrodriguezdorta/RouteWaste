import type { Container } from '@/domain/entity/container';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';

/**
 * Result type for listing containers
 */
export interface ListContainersResult {
    items: Container[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
    statistics?: EntityTypeStatistics;
}
