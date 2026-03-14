import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';
import {
  CreateVehicleService,
  DeleteVehicleService,
  GetVehicleService,
  ListVehiclesService,
  UpdateVehicleService
} from '../../../application/service/vehicle';
import type { Vehicle } from '../../../domain/entity/vehicle';
import { VehicleHttpRepository } from '../../http/vehicle-http-repository';

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
     * @param sortBy       Optional sort column key ('capacity', 'cost' or 'type')
     * @param sortOrder    Optional sort direction ('asc' or 'desc')
     * @param vehicleType  Optional vehicle type enum name to filter by
     */
    async getVehicles(page?: number, rowsPerPage?: number, sortBy?: string, sortOrder?: 'asc' | 'desc', vehicleType?: string) {
      this.loading = true;
      this.vehicles = [];

      const requestedPage = page ?? this.currentPage;
      const requestedRowsPerPage = rowsPerPage ?? this.rowsPerPage;
      
      // Create service instance with repository
      const listService = new ListVehiclesService(this.vehicleRepository);
      
      // Execute the list operation
      const result = await listService.execute({ page: requestedPage, pageSize: requestedRowsPerPage, sortBy, sortOrder, vehicleType });
      
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
        vehicleType: vehicle.getVehicleType(),
        transportCapacity: vehicle.getTransportCapacity(),
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
          this.setNotification(
            'Success', 
            'Vehicle added successfully', 
            'mdi-check', 
            'success'
          );
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
          vehicleType: vehicle.getVehicleType(),
          transportCapacity: vehicle.getTransportCapacity(),
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
          this.setNotification(
            'Success', 
            'Vehicle updated successfully', 
            'mdi-check', 
            'success'
          );
        }
      );
    },

    /**
     * Delete a vehicle from the system
     * 
     * @param id UUID string of the vehicle to delete
     */
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
            this.setNotification(
              'Success', 
              'Vehicle deleted successfully', 
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
      
      // Determine error message based on error kind
      let errorMessage = 'An unexpected error occurred';
      
      if (error.kind === 'ApiError' || error.error === 'ValidationError') {
        // If the error has validation details, format them
        if (error.details && error.details.length > 0) {
          const detailMessages = error.details.map((detail: any) => 
            `${detail.field}: ${detail.issue || detail.message}`
          ).join('; ');
          errorMessage = detailMessages;
        } else if (error.message) {
          errorMessage = error.message;
        } else {
          errorMessage = 'Invalid vehicle data';
        }
      } else if (error.kind === 'ValidationError') {
        errorMessage = error.message || 'Invalid vehicle data';
      } else if (error.kind === 'NotFoundError') {
        errorMessage = 'Vehicle not found';
      } else if (error.kind === 'ConflictError') {
        errorMessage = 'Vehicle already exists or conflicts with existing data';
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
