import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';

import { FacilityHttpRepository } from '@/adapter/http/facility-http-repository';
import {
  CreateFacilityService,
  DeleteFacilityService,
  FilterFacilitiesService,
  GetFacilityService,
  ListFacilitiesService,
  UpdateFacilityService
} from '@/application/service/facility';
import type { Facility } from '@/domain/entity/facility';

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
     * Retrieve all facilities from the backend with optional pagination
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     */
    async getFacilities(page?: number, rowsPerPage?: number) {
      this.loading = true;
      this.facilities = [];
      
      // Create service instance with repository
      const listService = new ListFacilitiesService(this.facilityRepository);
      
      // Execute the list operation
      const result = await listService.execute({ page, pageSize: rowsPerPage });
      
      // Handle the result using Either pattern
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with facilities
          this.facilities = data;
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
        facilityType: facility.getFacilityType(),
        location: facility.getLocation(),
        capacity: facility.getCapacity(),
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
          this.setNotification(
            'Success', 
            'Facility added successfully', 
            'mdi-check', 
            'success'
          );
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
          facilityType: facility.getFacilityType(),
          location: facility.getLocation(),
          capacity: facility.getCapacity(),
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
          this.setNotification(
            'Success', 
            'Facility updated successfully', 
            'mdi-check', 
            'success'
          );
        }
      );
    },

    /**
     * Delete a facility from the system
     * 
     * @param id UUID string of the facility to delete
     */
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
            this.setNotification(
              'Success', 
              'Facility deleted successfully', 
              'mdi-check', 
              'success'
            );
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
          this.facilities = data;
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
      
      // Determine error message based on error kind
      let errorMessage = 'An unexpected error occurred';
      
      if (error.kind === 'ValidationError') {
        errorMessage = error.message || 'Invalid facility data';
      } else if (error.kind === 'NotFoundError') {
        errorMessage = 'Facility not found';
      } else if (error.kind === 'ConflictError') {
        errorMessage = 'Facility already exists or conflicts with existing data';
      } else if (error.kind === 'CapacityExceededError') {
        errorMessage = 'Facility capacity exceeded';
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      // Display error notification to user
      this.setNotification(
        'Error', 
        errorMessage, 
        'mdi-alert', 
        'error'
      );
    },
  },
});
