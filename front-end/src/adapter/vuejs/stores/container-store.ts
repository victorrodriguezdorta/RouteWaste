import { ContainerHttpRepository } from '@/adapter/http/repository/container-http-repository';
import type { BulkImportResult } from '@/adapter/http/response/common/bulk-import-result';
import { resolveStoreSuccessNotification } from '@/adapter/vuejs/utils/store-notification-messages';
import { resolveBackendError } from '@/adapter/vuejs/utils/translate-backend-error';
import { CreateContainerService } from '@/application/service/container/create-container-service';
import { DeleteContainerService } from '@/application/service/container/delete-container-service';
import { GetContainerService } from '@/application/service/container/get-container-service';
import { ListContainersService } from '@/application/service/container/list-containers-service';
import { UpdateContainerService } from '@/application/service/container/update-container-service';
import type { Container } from '@/domain/entity/container';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';

/**
 * Container Store
 * 
 * Manages the state of Container entities in the front-end application.
 * This store is responsible for:
 * - Storing and managing container data (list and individual container)
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform CRUD operations
 * 
 * The store uses separate service instances for each operation following
 * the Single Responsibility Principle and maintaining clear separation of concerns.
 */
export const useContainerStore = defineStore('Container', {
  /**
   * State definition
   * 
   * Defines the reactive state properties that will be managed by this store.
   */
  state: () => ({
    /** Array of all containers retrieved from the backend */
    containers: [] as Container[],

    /** Total amount of containers available in backend */
    totalContainers: 0,

    /** Global statistics from last list response (unfiltered totals by type) */
    containerStatistics: undefined as EntityTypeStatistics | undefined,

    /** Current zero-based page index loaded from backend */
    currentPage: 0,

    /** Current requested page size */
    rowsPerPage: 10,
    
    /** Currently selected container (if any) */
    container: undefined as Container | undefined,
    
    /** Repository instance for HTTP communication */
    containerRepository: new ContainerHttpRepository(),
    
    /** Loading indicator for async operations */
    loading: false,
    
    /** Notification object for user feedback */
    containerNotification: {
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
     * Get the total number of containers in the store
     * @param state Current store state
     * @returns Count of containers in the containers array
     */
    getNumberOfContainers: (state) => state.containers.length,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform business operations.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Retrieve all containers from the backend with optional pagination, sort and filtering
     * 
     * @param page Optional page number for pagination
     * @param rowsPerPage Optional number of items per page
     * @param sortBy Optional sort column key
     * @param sortOrder Optional sort direction
     * @param wasteType Optional waste type filter
     * @param serviceZone Optional service zone filter
     * @param location Optional location filter
     * @param name Optional name filter
     */
    async getContainers(
      page?: number, 
      rowsPerPage?: number, 
      sortBy?: string, 
      sortOrder?: 'asc' | 'desc', 
      wasteType?: string,
      serviceZone?: string,
      location?: string,
      name?: string
    ) {
      this.loading = true;
      this.containers = [];
      const requestedPage = page ?? this.currentPage;
      const requestedRowsPerPage = rowsPerPage ?? this.rowsPerPage;
      
      // Create service instance with repository
      const listService = new ListContainersService(this.containerRepository);
      
      // Execute the list operation with all available filters
      const result = await listService.execute({
        page: requestedPage,
        pageSize: requestedRowsPerPage,
        sortBy,
        sortOrder,
        wasteType,
        serviceZone,
        location,
        name
      });
      
      // Handle the result using Either pattern
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with containers
          this.containers = data.items;
          this.totalContainers = data.totalElements;
          this.currentPage = data.page;
          this.rowsPerPage = data.size;
          this.containerStatistics = data.statistics;
          this.loading = false;
        }
      );
    },

    /**
     * Retrieve a specific container by its identifier
     * 
     * @param id UUID string of the container to retrieve
     */
    async getContainerById(id: string) {
      this.container = undefined;
      
      // Create service instance with repository
      const getService = new GetContainerService(this.containerRepository);
      
      // Convert string id to UllUUID
      const containerId = new UllUUID(id);
      
      // Execute the get operation
      const result = await getService.execute({ containerId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: set the retrieved container
          this.container = data;
        }
      );
    },

    /**
     * Register a new container in the system
     * 
     * @param container The Container entity to create
     */
    async registerContainer(container: Container) {
      // Create service instance with repository
      const createService = new CreateContainerService(this.containerRepository);
      
      // Execute the create operation with container properties
      const result = await createService.execute({
        name: container.getName(),
        location: container.getLocation(),
        wasteType: container.getWasteType(),
        capacityLiters: container.getCapacityLiters(),
        dailyDemandLitersPerDay: container.getDailyDemandLitersPerDay(),
        serviceZone: container.getServiceZone()
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: add to local array and notify
          this.containers.push(data);
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.containerAdded'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Update an existing container
     * 
     * @param id UUID string of the container to update
     * @param container Updated Container entity with new data
     */
    async updateContainer(id: string, container: Container) {
      // Create service instance with repository
      const updateService = new UpdateContainerService(this.containerRepository);
      
      // Convert string id to UllUUID
      const containerId = new UllUUID(id);
      
      // Execute the update operation
      const result = await updateService.execute({ 
        containerId, 
        updatedFields: {
          name: container.getName(),
          location: container.getLocation(),
          wasteType: container.getWasteType(),
          capacityLiters: container.getCapacityLiters(),
          dailyDemandLitersPerDay: container.getDailyDemandLitersPerDay(),
          serviceZone: container.getServiceZone()
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
          this.containers = this.containers.map(c => 
            c.getId().equals(data.getId()) ? data : c
          );
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.containerUpdated'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Delete a container from the system
     * 
     * @param id UUID string of the container to delete
     */
    /**
     * Import containers from a JSON file via the bulk import API.
     *
     * @param file JSON file selected by the user
     * @returns import statistics when the request completes, or null on transport/API error
     */
    async importContainersFromFile(file: File): Promise<BulkImportResult | null> {
      this.loading = true;
      try {
        const result = await this.containerRepository.importFromFile(file);
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

    async deleteContainer(id: string) {
      // Create service instance with repository
      const deleteService = new DeleteContainerService(this.containerRepository);
      
      // Convert string id to UllUUID
      const containerId = new UllUUID(id);
      
      // Execute the delete operation
      const result = await deleteService.execute({ containerId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        success => {
          // Success case: remove from local array and notify
          if (success) {
            this.containers = this.containers.filter(container => 
              container.getId().toString() !== id
            );
            {
              const { title, message } = resolveStoreSuccessNotification(
                'common.notifications.containerDeleted'
              );
              this.setNotification(title, message, 'mdi-check', 'success');
            }
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
      this.containerNotification = {
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
      console.error('Container store error:', error);
      const { title, message } = resolveBackendError(error, 'container');
      this.setNotification(title, message, 'mdi-alert', 'error');
    },
  },
});
