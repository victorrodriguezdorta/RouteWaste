import type { InfrastructurePlanSummaryJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-summary-json-response';
import { InfrastructurePlanHttpRepository } from '@/adapter/http/infrastructure-plan-http-repository';
import { resolveBackendError } from '@/adapter/vuejs/utils/translate-backend-error';
import { DeleteInfrastructurePlanService } from '@/application/service/infrastructure-plan/delete-infrastructure-plan-service';
import { GetInfrastructurePlanService } from '@/application/service/infrastructure-plan/get-infrastructure-plan-service';
import { ListInfrastructurePlansService } from '@/application/service/infrastructure-plan/list-infrastructure-plans-service';
import type { InfrastructurePlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';

/**
 * Infrastructure Plan Store
 * 
 * Manages the state of Infrastructure Plan entities in the front-end application.
 * This store is responsible for:
 * - Storing and managing infrastructure plan data (list and individual plan)
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform CRUD operations
 * - Validating infrastructure plans against business rules
 * 
 * The store uses separate service instances for each operation following
 * the Single Responsibility Principle and maintaining clear separation of concerns.
 */
export const useInfrastructurePlanStore = defineStore('InfrastructurePlan', {
  /**
   * State definition
   * 
   * Defines the reactive state properties that will be managed by this store.
   */
  state: () => ({
    /** Array of infrastructure plan summaries retrieved from the backend */
    infrastructurePlans: [] as InfrastructurePlanSummaryJsonResponse[],

    /** Total number of infrastructure plans available on the server */
    totalInfrastructurePlans: 0,

    /** Current zero-based page index loaded from backend */
    currentPage: 0,

    /** Current requested page size */
    rowsPerPage: 10,

    /** Last list sort field requested from the API */
    lastSortBy: 'executedAt' as string | undefined,

    /** Last list sort order requested from the API */
    lastSortOrder: 'desc' as 'asc' | 'desc',
    
    /** Currently selected infrastructure plan detail (if any) */
    infrastructurePlan: undefined as InfrastructurePlanDetail | undefined,
    
    /** Repository instance for HTTP communication */
    infrastructurePlanRepository: new InfrastructurePlanHttpRepository(),
    
    /** Loading indicator for async operations */
    loading: false,
    
    /** Notification object for user feedback */
    infrastructurePlanNotification: {
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
     * Get the total number of infrastructure plans in the store
     * @param state Current store state
     * @returns Count of infrastructure plans in the infrastructurePlans array
     */
    getNumberOfPlans: (state) => state.infrastructurePlans.length,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform business operations.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Retrieve all infrastructure plans from the backend with optional pagination and sorting
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     * @param sortBy Optional field to sort by
     * @param sortOrder Optional sort order ('asc' or 'desc')
     */
    async getInfrastructurePlans(page?: number, rowsPerPage?: number, sortBy?: string, sortOrder?: 'asc' | 'desc') {
      this.loading = true;
      this.infrastructurePlans = [];
      if (sortBy !== undefined) {
        this.lastSortBy = sortBy;
      }
      if (sortOrder !== undefined) {
        this.lastSortOrder = sortOrder;
      }
      
      // Create service instance with repository
      const listService = new ListInfrastructurePlansService(this.infrastructurePlanRepository);
      
      // Execute the list operation
      const result = await listService.execute({ page, pageSize: rowsPerPage, sortBy, sortOrder });
      
      // Handle the result using Either pattern
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with infrastructure plans
          this.infrastructurePlans = data.content;
          this.totalInfrastructurePlans = data.totalElements;
          this.currentPage = data.page;
          this.rowsPerPage = data.size;
          this.loading = false;
        }
      );
    },

    /**
     * Retrieve a specific infrastructure plan by its identifier
     * 
     * @param id UUID string of the infrastructure plan to retrieve
     */
    async getInfrastructurePlanById(id: string) {
      this.infrastructurePlan = undefined;
      
      // Create service instance with repository
      const getService = new GetInfrastructurePlanService(this.infrastructurePlanRepository);
      
      // Convert string id to UllUUID
      const planId = new UllUUID(id);
      
      // Execute the get operation
      const result = await getService.execute({ planId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: set the retrieved infrastructure plan
          this.infrastructurePlan = data;
        }
      );
    },

    /**
     * Delete an infrastructure plan from the system
     * 
     * @param id UUID string of the infrastructure plan to delete
     */
    async deleteInfrastructurePlan(id: string) {
      // Create service instance with repository
      const deleteService = new DeleteInfrastructurePlanService(this.infrastructurePlanRepository);
      
      // Convert string id to UllUUID
      const planId = new UllUUID(id);
      
      // Execute the delete operation
      const result = await deleteService.execute({ planId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        success => {
          // Success case: remove from local array and notify
          if (success) {
            this.infrastructurePlans = this.infrastructurePlans.filter(plan => plan.id !== id);
            this.setNotification(
              'Success', 
              'Infrastructure plan deleted successfully', 
              'mdi-check', 
              'success'
            );
          }
        }
      );
    },

    /**
     * Reloads the current list page using the last sort parameters.
     */
    async refreshCurrentPlans() {
      await this.getInfrastructurePlans(
        this.currentPage,
        this.rowsPerPage,
        this.lastSortBy,
        this.lastSortOrder,
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
      this.infrastructurePlanNotification = {
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
      console.error('Infrastructure plan store error:', error);
      const { title, message } = resolveBackendError(error, 'infrastructurePlan');
      this.setNotification(title, message, 'mdi-alert', 'error');
    },
  },
});
