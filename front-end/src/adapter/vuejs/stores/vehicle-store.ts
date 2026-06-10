import type { BulkImportResult } from '@/adapter/http/dto/common/bulk-import-result';
import { VehicleHttpRepository } from '@/adapter/http/vehicle-http-repository';
import { resolveStoreSuccessNotification } from '@/adapter/vuejs/utils/store-notification-messages';
import { resolveBackendError } from '@/adapter/vuejs/utils/translate-backend-error';
import { CreateVehicleService } from '@/application/service/vehicle/create-vehicle-service';
import { DeleteVehicleService } from '@/application/service/vehicle/delete-vehicle-service';
import { GetVehicleService } from '@/application/service/vehicle/get-vehicle-service';
import { ListVehiclesService } from '@/application/service/vehicle/list-vehicles-service';
import { UpdateVehicleService } from '@/application/service/vehicle/update-vehicle-service';
import type { Vehicle } from '@/domain/entity/vehicle';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';

/**
 * Vehicle Store
 * 
 * Manages the state of Vehicle entities in the front-end application.
 * This store is responsible for:
 * - Storing and managing vehicle data (list and individual vehicle)
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform CRUD operations
 * 
 * The store uses separate service instances for each operation following
 * the Single Responsibility Principle and maintaining clear separation of concerns.
 */
export const useVehicleStore = defineStore('Vehicle', {
  /**
   * State definition
   * 
   * Defines the reactive state properties that will be managed by this store.
   */
  state: () => ({
    /** Array of all vehicles retrieved from the backend */
    vehicles: [] as Vehicle[],

    /** Total amount of vehicles available in backend */
    totalVehicles: 0,

    /** Global statistics from last list response (unfiltered totals by type) */
    vehicleStatistics: undefined as EntityTypeStatistics | undefined,

    /** Current zero-based page index loaded from backend */
    currentPage: 0,

    /** Current requested page size */
    rowsPerPage: 10,
    
    /** Currently selected vehicle (if any) */
    vehicle: undefined as Vehicle | undefined,
    
    /** Repository instance for HTTP communication */
    vehicleRepository: new VehicleHttpRepository(),
    
    /** Loading indicator for async operations */
    loading: false,
    
    /** Notification object for user feedback */
    vehicleNotification: {
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
     * Get the total number of vehicles in the store
     * @param state Current store state
     * @returns Count of vehicles in the vehicles array
     */
    getNumberOfVehicles: (state) => state.vehicles.length,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform business operations.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Retrieve all vehicles from the backend with optional pagination, sort and filter
     *
     * @param page         Optional page number (0-based)
     * @param rowsPerPage  Optional number of items per page
     * @param sortBy       Optional sort field: type, vehicleType, capacityKilograms, CapacityLiters, capacityLiters, cost, costPerKilometer, etc.
     * @param sortOrder    Optional sort direction ('asc' or 'desc')
     * @param vehicleType  Optional vehicle type enum name to filter by
     * @param name         Optional name substring to filter by
     * 
     * Note: sortBy values are mapped by backend VehicleFieldMapper to MongoDB field paths
     */
    async getVehicles(page?: number, rowsPerPage?: number, sortBy?: string, sortOrder?: 'asc' | 'desc', vehicleType?: string, name?: string) {
      this.loading = true;
      this.vehicles = [];

      const requestedPage = page ?? this.currentPage;
      const requestedRowsPerPage = rowsPerPage ?? this.rowsPerPage;
      
      // Create service instance with repository
      const listService = new ListVehiclesService(this.vehicleRepository);
      
      // Execute the list operation
      const result = await listService.execute({ page: requestedPage, pageSize: requestedRowsPerPage, sortBy, sortOrder, vehicleType, name });
      
      // Handle the result using Either pattern
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
        },
        data => {
          // Success case: update state with vehicles
          this.vehicles = data.items;
          this.totalVehicles = data.totalElements;
          this.currentPage = data.page;
          this.rowsPerPage = data.size;
          this.vehicleStatistics = data.statistics;
          this.loading = false;
        }
      );
    },

    /**
     * Retrieve a specific vehicle by its identifier
     * 
     * @param id UUID string of the vehicle to retrieve
     */
    async getVehicleById(id: string) {
      this.vehicle = undefined;
      
      // Create service instance with repository
      const getService = new GetVehicleService(this.vehicleRepository);
      
      // Convert string id to UllUUID
      const vehicleId = new UllUUID(id);
      
      // Execute the get operation
      const result = await getService.execute({ vehicleId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: set the retrieved vehicle
          this.vehicle = data;
        }
      );
    },

    /**
     * Register a new vehicle in the system
     * 
     * @param vehicle The Vehicle entity to create
     */
    async registerVehicle(vehicle: Vehicle) {
      // Create service instance with repository
      const createService = new CreateVehicleService(this.vehicleRepository);
      
      // Execute the create operation with vehicle properties
      const result = await createService.execute({
        name: vehicle.getName(),
        vehicleType: vehicle.getVehicleType(),
        capacityKilograms: vehicle.getCapacityKilograms(),
        capacityLiters: vehicle.getCapacityLiters(),
        costPerKilometer: vehicle.getCostPerKilometer()
      });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        data => {
          // Success case: add to local array and notify
          this.vehicles.push(data);
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.vehicleAdded'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Update an existing vehicle
     * 
     * @param id UUID string of the vehicle to update
     * @param vehicle Updated Vehicle entity with new data
     */
    async updateVehicle(id: string, vehicle: Vehicle) {
      // Create service instance with repository
      const updateService = new UpdateVehicleService(this.vehicleRepository);
      
      // Convert string id to UllUUID
      const vehicleId = new UllUUID(id);
      
      // Execute the update operation
      const result = await updateService.execute({ 
        vehicleId, 
        updatedFields: {
          name: vehicle.getName(),
          vehicleType: vehicle.getVehicleType(),
          capacityKilograms: vehicle.getCapacityKilograms(),
          capacityLiters: vehicle.getCapacityLiters(),
          costPerKilometer: vehicle.getCostPerKilometer()
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
          this.vehicles = this.vehicles.map(v => 
            v.getId().equals(data.getId()) ? data : v
          );
          {
            const { title, message } = resolveStoreSuccessNotification(
              'common.notifications.vehicleUpdated'
            );
            this.setNotification(title, message, 'mdi-check', 'success');
          }
        }
      );
    },

    /**
     * Delete a vehicle from the system
     * 
     * @param id UUID string of the vehicle to delete
     */
    async importVehiclesFromFile(file: File): Promise<BulkImportResult | null> {
      this.loading = true;
      try {
        const result = await this.vehicleRepository.importFromFile(file);
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

    async deleteVehicle(id: string) {
      // Create service instance with repository
      const deleteService = new DeleteVehicleService(this.vehicleRepository);
      
      // Convert string id to UllUUID
      const vehicleId = new UllUUID(id);
      
      // Execute the delete operation
      const result = await deleteService.execute({ vehicleId });
      
      // Handle the result
      result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
        },
        success => {
          // Success case: remove from local array and notify
          if (success) {
            this.vehicles = this.vehicles.filter(vehicle => 
              vehicle.getId().toString() !== id
            );
            {
              const { title, message } = resolveStoreSuccessNotification(
                'common.notifications.vehicleDeleted'
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
      this.vehicleNotification = {
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
      console.error('Vehicle store error:', error);
      const { title, message } = resolveBackendError(error, 'vehicle');
      this.setNotification(title, message, 'mdi-alert', 'error');
    },
  },
});
