import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';
import {
    CreateInfrastructurePlanService,
    DeleteInfrastructurePlanService,
    GetInfrastructurePlanService,
    ListInfrastructurePlansService,
    UpdateInfrastructurePlanService,
    ValidateInfrastructurePlanService
} from '../../../application/service/infrastructure-plan';
import type { InfrastructurePlan } from '../../../domain/entity/infrastructure-plan';
import { InfrastructurePlanHttpRepository } from '../../http/infrastructure-plan-http-repository';

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
    /** Array of all infrastructure plans retrieved from the backend */
    infrastructurePlans: [] as InfrastructurePlan[],
    
    /** Currently selected infrastructure plan (if any) */
    infrastructurePlan: undefined as InfrastructurePlan | undefined,
    
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
     * Retrieve all infrastructure plans from the backend with optional pagination
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     */
    async getInfrastructurePlans(page?: number, rowsPerPage?: number) {
      this.loading = true;
      this.infrastructurePlans = [];
      
      // Create service instance with repository
      const listService = new ListInfrastructurePlansService(this.infrastructurePlanRepository);
      
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
          // Success case: update state with infrastructure plans
          this.infrastructurePlans = data;
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
     * Register a new infrastructure plan in the system
     * 
     * @param plan The Infrastructure Plan entity to create
     */
    async registerInfrastructurePlan(plan: InfrastructurePlan) {
      // Create service instance with repository
      const createService = new CreateInfrastructurePlanService(this.infrastructurePlanRepository);
      
      // Execute the create operation with plan properties
      const result = await createService.execute({
        period: plan.getPeriod(),
        maxBudget: plan.getMaxBudget(),
        servicePolicies: plan.getServicePolicies()
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: add to local array and notify
          this.infrastructurePlans.push(data);
          this.setNotification(
            'Success', 
            'Infrastructure plan added successfully', 
            'mdi-check', 
            'success'
          );
        }
      );
    },

    /**
     * Update an existing infrastructure plan
     * 
     * @param id UUID string of the infrastructure plan to update
     * @param plan Updated Infrastructure Plan entity with new data
     */
    async updateInfrastructurePlan(id: string, plan: InfrastructurePlan) {
      // Create service instance with repository
      const updateService = new UpdateInfrastructurePlanService(this.infrastructurePlanRepository);
      
      // Convert string id to UllUUID
      const planId = new UllUUID(id);
      
      // Execute the update operation
      const result = await updateService.execute({ 
        planId, 
        updatedFields: {
          period: plan.getPeriod(),
          maxBudget: plan.getMaxBudget(),
          servicePolicies: plan.getServicePolicies()
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
          this.infrastructurePlans = this.infrastructurePlans.map(p => 
            p.getId().equals(data.getId()) ? data : p
          );
          this.setNotification(
            'Success', 
            'Infrastructure plan updated successfully', 
            'mdi-check', 
            'success'
          );
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
            this.infrastructurePlans = this.infrastructurePlans.filter(plan => 
              plan.getId().toString() !== id
            );
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
     * Validate an infrastructure plan against business rules
     * 
     * @param id UUID string of the infrastructure plan to validate
     */
    async validateInfrastructurePlan(id: string) {
      // Create service instance with repository
      const validateService = new ValidateInfrastructurePlanService(this.infrastructurePlanRepository);
      
      // Convert string id to UllUUID
      const planId = new UllUUID(id);
      
      // Execute the validation operation
      const result = await validateService.execute({ planId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        isValid => {
          // Success case: notify validation result
          if (isValid) {
            this.setNotification(
              'Success', 
              'Infrastructure plan is valid', 
              'mdi-check', 
              'success'
            );
          } else {
            this.setNotification(
              'Warning', 
              'Infrastructure plan validation failed', 
              'mdi-alert', 
              'warning'
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
      
      // Determine error message based on error kind
      let errorMessage = 'An unexpected error occurred';
      
      if (error.kind === 'ValidationError') {
        errorMessage = error.message || 'Invalid infrastructure plan data';
      } else if (error.kind === 'NotFoundError') {
        errorMessage = 'Infrastructure plan not found';
      } else if (error.kind === 'ConflictError') {
        errorMessage = 'Infrastructure plan already exists or conflicts with existing data';
      } else if (error.kind === 'BudgetExceededError') {
        errorMessage = 'Infrastructure plan exceeds maximum budget';
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
