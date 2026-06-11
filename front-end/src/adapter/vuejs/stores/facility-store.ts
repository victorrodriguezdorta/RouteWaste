import type { BulkImportResult } from '@/adapter/http/response/common/bulk-import-result';
import { FacilityHttpRepository } from '@/adapter/http/repository/facility-http-repository';
import { resolveStoreSuccessNotification } from '@/adapter/vuejs/utils/store-notification-messages';
import { resolveBackendError } from '@/adapter/vuejs/utils/translate-backend-error';
import { CreateFacilityService } from '@/application/service/facility/create-facility-service';
import { DeleteFacilityService } from '@/application/service/facility/delete-facility-service';
import { FilterFacilitiesService } from '@/application/service/facility/filter-facilities-service';
import { GetFacilityService } from '@/application/service/facility/get-facility-service';
import { ListFacilitiesService } from '@/application/service/facility/list-facilities-service';
import { UpdateFacilityService } from '@/application/service/facility/update-facility-service';
import type { Facility } from '@/domain/entity/facility';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';

/**
 * Facility Store
 * 
 * Manages the state of Facility entities in the front-end application.
 * This store is responsible for:
 * - Storing and managing facility data (list and individual facility)
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform CRUD operations
 * 
 * The store uses separate service instances for each operation following
 * the Single Responsibility Principle and maintaining clear separation of concerns.
 */
export const useFacilityStore = defineStore('Facility', {
  /**
   * State definition
   * 
   * Defines the reactive state properties that will be managed by this store.
   */
  state: () => ({
    /** Array of all facilities retrieved from the backend */
    facilities: [] as Facility[],
    
    /** Total amount of facilities available in backend */
    totalFacilities: 0,

    /** Global statistics from last list response (unfiltered totals by type) */
    facilityStatistics: undefined as EntityTypeStatistics | undefined,

    /** Current zero-based page index loaded from backend */
    currentPage: 0,

    /** Current requested page size */
    rowsPerPage: 10,
    
    /** Currently selected facility (if any) */
    facility: undefined as Facility | undefined,
    
    /** Repository instance for HTTP communication */
    facilityRepository: new FacilityHttpRepository(),
    
    /** Loading indicator for async operations */
    loading: false,
    
    /** Notification object for user feedback */
    facilityNotification: {
      flag: false,          // Whether to show the notification
      title: '',            // Notification title
      msg: '',              // Notification message content
      icon: '',             // Icon identifier (e.g., 'mdi-check')
      timeout: 4000,        // Auto-hide duration in milliseconds
      color: '',            // Color theme (e.g., 'success', 'error')
    },
  }),

  /**
   * Getters
   * 
   * Computed properties that provide derived state or convenient access to state properties.
   */
  getters: {
    /**
     * Get the current loading state
     * @param state Current store state
     * @returns Boolean indicating if an operation is in progress
     */
    getLoading: (state) => state.loading,
    
    /**
     * Get the total number of facilities in the store
     * @param state Current store state
     * @returns Count of facilities in the facilities array
     */
    getNumberOfFacilities: (state) => state.facilities.length,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform business operations.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Retrieve all facilities from the backend with optional pagination, sort and filter
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     * @param sortBy Optional sort column key
     * @param sortOrder Optional sort direction
     * @param facilityType Optional facility type filter
     * @param status Optional facility status filter
     * @param location Optional location filter (postal address)
     * @param name Optional name filter
     */
    async getFacilities(page?: number, rowsPerPage?: number, sortBy?: string, sortOrder?: 'asc' | 'desc', facilityType?: string, status?: string, location?: string, name?: string) {
      this.loading = true;
      this.facilities = [];
      const requestedPage = page ?? this.currentPage;
      const requestedRowsPerPage = rowsPerPage ?? this.rowsPerPage;
      
      // Create service instance with repository
      const listService = new ListFacilitiesService(this.facilityRepository);
      
      // Execute the list operation
      const result = await listService.execute({
        page: requestedPage,
        pageSize: requestedRowsPerPage,
        sortBy,
        sortOrder,
        facilityType,
        status,
        location,
        name
      });
      
      // Handle the result using Either pattern
      result.fold(
        error => {
          // Error case: handle and notify user
          console.error('[FacilityStore.getFacilities] Error:', error);
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with facilities
          this.facilities = data.items;
          this.totalFacilities = data.totalElements;
          this.facilityStatistics = data.statistics;
          this.currentPage = data.page;
          this.rowsPerPage = data.size;
          this.loading = false;
        }
      );
    },

    /**
     * Retrieve a specific facility by its identifier
     * 
     * @param id UUID string of the facility to retrieve
     */
    async getFacilityById(id: string) {
      this.facility = undefined;
      
      // Create service instance with repository
      const getService = new GetFacilityService(this.facilityRepository);
      
      // Convert string id to UllUUID
      const facilityId = new UllUUID(id);
      
      // Execute the get operation
      const result = await getService.execute({ facilityId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: set the retrieved facility
          this.facility = data;
        }
      );
    },

    /**
     * Register a new facility in the system
     * 
     * @param facility The Facility entity to create
     */
    async registerFacility(facility: Facility) {
      // Create service instance with repository
      const createService = new CreateFacilityService(this.facilityRepository);
      
      // Execute the create operation with facility properties
      const result = await createService.execute({
        name: facility.getName(),
        facilityType: facility.getFacilityType(),
        location: facility.getLocation(),
        storageCapacity: facility.getStorageCapacity(),
        processingCapacity: facility.getProcessingCapacity(),
        unloadingTime: facility.getUnloadingTime(),
        openingFixedCost: facility.getOpeningFixedCost(),
        status: facility.getStatus()
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: add to local array and notify
          this.facilities.push(data);
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.facilityAdded'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Update an existing facility
     * 
     * @param id UUID string of the facility to update
     * @param facility Updated Facility entity with new data
     */
    async updateFacility(id: string, facility: Facility) {
      // Create service instance with repository
      const updateService = new UpdateFacilityService(this.facilityRepository);
      
      // Convert string id to UllUUID
      const facilityId = new UllUUID(id);
      
      // Execute the update operation
      const result = await updateService.execute({ 
        facilityId, 
        updatedFields: {
          name: facility.getName(),
          facilityType: facility.getFacilityType(),
          location: facility.getLocation(),
          storageCapacity: facility.getStorageCapacity(),
          processingCapacity: facility.getProcessingCapacity(),
          unloadingTime: facility.getUnloadingTime(),
          openingFixedCost: facility.getOpeningFixedCost(),
          status: facility.getStatus()
        }
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: update in local array and notify
          this.facilities = this.facilities.map(f => 
            f.getId().equals(data.getId()) ? data : f
          );
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.facilityUpdated'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Delete a facility from the system
     * 
     * @param id UUID string of the facility to delete
     */
    async importFacilitiesFromFile(file: File): Promise<BulkImportResult | null> {
      this.loading = true;
      try {
        const result = await this.facilityRepository.importFromFile(file);
        return result.fold(
          (error) => {
            this.handleError(error);
            return null;
          },
          (data) => data,
        );
      } finally {
        this.loading = false;
      }
    },

    async deleteFacility(id: string) {
      // Create service instance with repository
      const deleteService = new DeleteFacilityService(this.facilityRepository);
      
      // Convert string id to UllUUID
      const facilityId = new UllUUID(id);
      
      // Execute the delete operation
      const result = await deleteService.execute({ facilityId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        success => {
          // Success case: remove from local array and notify
          if (success) {
            this.facilities = this.facilities.filter(facility => 
              facility.getId().toString() !== id
            );
            {
              const { title, message } = resolveStoreSuccessNotification(
                'common.notifications.facilityDeleted'
              );
              this.setNotification(title, message, 'mdi-check', 'success');
            }
          }
        }
      );
    },

    /**
     * Filter facilities based on specified criteria
     * 
     * @param filterCriteria Object containing filter parameters
     */
    async filterFacilities(filterCriteria: any) {
      this.loading = true;
      this.facilities = [];
      
      // Create service instance with repository
      const filterService = new FilterFacilitiesService(this.facilityRepository);
      
      // Execute the filter operation
      const result = await filterService.execute(filterCriteria);
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with filtered facilities
          this.facilities = data.items;
          this.loading = false;
        }
      );
    },

    /**
     * Set a notification to be displayed to the user
     * 
     * @param title Notification title
     * @param msg Notification message content
     * @param icon Material Design Icon identifier
     * @param color Color theme for the notification
     */
    setNotification(title: string, msg: string, icon: string, color: string) {
      this.facilityNotification = {
        flag: true,
        title,
        msg,
        icon,
        timeout: 4000,
        color,
      };
    },

    /**
     * Handle errors from backend operations
     * 
     * Logs the error to console and displays a user-friendly error notification.
     * Different error types can be handled differently based on the error kind.
     * 
     * @param error DataError object containing error information
     */
    handleError(error: any) {
      console.error('Facility store error:', error);
      const { title, message } = resolveBackendError(error, 'facility');
      this.setNotification(title, message, 'mdi-alert', 'error');
    },
  },
});
