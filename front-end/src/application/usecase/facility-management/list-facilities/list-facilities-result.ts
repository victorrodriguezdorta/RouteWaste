import { Facility } from '@/domain/entity/facility';

// Result type for the use case with pagination metadata
export interface ListFacilitiesResult {
  items: Facility[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}