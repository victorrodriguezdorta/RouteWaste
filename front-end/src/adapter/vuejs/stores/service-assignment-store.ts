import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';
import {
    AssignContainerToFacilityService,
    ListServiceAssignmentsService,
    RemoveServiceAssignmentService,
    UpdateServiceAssignmentService
} from '../../../application/service/service-assignment';
import type { ServiceAssignment } from '../../../domain/entity/service-assignment';
import { ServiceAssignmentHttpRepository } from '../../http/service-assignment-http-repository';

/**
 * Service Assignment Store
 * 
 * Manages the state of Service Assignment entities in the front-end application.
 * This store is responsible for:
 * - Storing and managing service assignment data (list and individual assignment)
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform CRUD operations
 * 
 * The store uses separate service instances for each operation following
 * the Single Responsibility Principle and maintaining clear separation of concerns.
 */
export const useServiceAssignmentStore = defineStore('ServiceAssignment', {
  /**
   * State definition
   * 
   * Defines the reactive state properties that will be managed by this store.
   */
  state: () => ({
    /** Array of all service assignments retrieved from the backend */
    serviceAssignments: [] as ServiceAssignment[],
    
    /** Currently selected service assignment (if any) */
    serviceAssignment: undefined as ServiceAssignment | undefined,
    
    /** Repository instance for HTTP communication */
    serviceAssignmentRepository: new ServiceAssignmentHttpRepository(),
    
    /** Loading indicator for async operations */
    loading: false,
    
    /** Notification object for user feedback */
    serviceAssignmentNotification: {
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
     * Get the total number of service assignments in the store
     * @param state Current store state
     * @returns Count of service assignments in the serviceAssignments array
     */
    getNumberOfServiceAssignments: (state) => state.serviceAssignments.length,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform business operations.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Retrieve all service assignments from the backend with optional pagination
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     */
    async getServiceAssignments(page?: number, rowsPerPage?: number) {
      this.loading = true;
      this.serviceAssignments = [];
      
      // Create service instance with repository
      const listService = new ListServiceAssignmentsService(this.serviceAssignmentRepository);
      
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
          // Success case: update state with service assignments
          this.serviceAssignments = data;
          this.loading = false;
        }
      );
    },

    /**
     * Assign a container to a facility, creating a new service assignment
     * 
     * @param assignment The Service Assignment entity to create
     */
    async assignContainerToFacility(assignment: ServiceAssignment) {
      // Create service instance with repository
      const assignService = new AssignContainerToFacilityService(this.serviceAssignmentRepository);
      
      // Execute the assignment operation with assignment properties
      const result = await assignService.execute({
        containerId: assignment.container.getId(),
        facilityId: assignment.facility.getId(),
        wasteDemand: assignment.wasteDemand,
        distance: assignment.distance,
        serviceTime: assignment.serviceTime,
        transportCost: assignment.transportCost
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: add to local array and notify
          this.serviceAssignments.push(data);
          this.setNotification(
            'Success', 
            'Service assignment created successfully', 
            'mdi-check', 
            'success'
          );
        }
      );
    },

    /**
     * Update an existing service assignment
     * 
     * @param id UUID string of the service assignment to update
     * @param assignment Updated Service Assignment entity with new data
     */
    async updateServiceAssignment(id: string, assignment: ServiceAssignment) {
      // Create service instance with repository
      const updateService = new UpdateServiceAssignmentService(this.serviceAssignmentRepository);
      
      // Convert string id to UllUUID
      const assignmentId = new UllUUID(id);
      
      // Execute the update operation
      const result = await updateService.execute({ 
        assignmentId, 
        updatedFields: {
          wasteDemand: assignment.wasteDemand,
          distance: assignment.distance,
          serviceTime: assignment.serviceTime,
          transportCost: assignment.transportCost
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
          this.serviceAssignments = this.serviceAssignments.map(a => 
            a.getServiceAssignmentId().equals(data.getServiceAssignmentId()) ? data : a
          );
          this.setNotification(
            'Success', 
            'Service assignment updated successfully', 
            'mdi-check', 
            'success'
          );
        }
      );
    },

    /**
     * Remove a service assignment from the system
     * 
     * @param id UUID string of the service assignment to remove
     */
    async removeServiceAssignment(id: string) {
      // Create service instance with repository
      const removeService = new RemoveServiceAssignmentService(this.serviceAssignmentRepository);
      
      // Convert string id to UllUUID
      const assignmentId = new UllUUID(id);
      
      // Execute the remove operation
      const result = await removeService.execute({ assignmentId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        success => {
          // Success case: remove from local array and notify
          if (success) {
            this.serviceAssignments = this.serviceAssignments.filter(assignment => 
              assignment.getServiceAssignmentId().toString() !== id
            );
            this.setNotification(
              'Success', 
              'Service assignment removed successfully', 
              'mdi-check', 
              'success'
            );
          }
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
      this.serviceAssignmentNotification = {
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
      console.error('Service assignment store error:', error);
      
      // Determine error message based on error kind
      let errorMessage = 'An unexpected error occurred';
      
      if (error.kind === 'ValidationError') {
        errorMessage = error.message || 'Invalid service assignment data';
      } else if (error.kind === 'NotFoundError') {
        errorMessage = 'Service assignment not found';
      } else if (error.kind === 'ConflictError') {
        errorMessage = 'Service assignment already exists or conflicts with existing data';
      } else if (error.kind === 'PolicyViolationError') {
        errorMessage = 'Service assignment violates service policies';
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
