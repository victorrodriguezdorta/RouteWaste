import { AlgorithmHttpRepository } from '@/adapter/http/algorithm-http-repository';
import { CreateAlgorithmService } from '@/application/service/algorithm/create-algorithm-service';
import type { CreateAlgorithmCommand, CreateAlgorithmResult } from '@/application/usecase/algorithm-management/create-algorithm/create-algorithm-use-case';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { defineStore } from 'pinia';
import { i18n } from '../i18n';
import { resolveBackendError, translateBackendMessage } from '../utils/translate-backend-error';

/**
 * FacilityVehicleSelection
 * 
 * Represents a facility with its selected vehicles for the algorithm execution.
 */
export interface FacilityVehicleSelection {
  facilityId: string;
  selectedVehicleIds: string[];
}

/**
 * Algorithm Store
 * 
 * Manages the state and execution of the route optimization algorithm in the front-end application.
 * This store is responsible for:
 * - Storing temporary algorithm execution parameters (facilities, vehicles, containers, extra data)
 * - Executing the algorithm and sending data to the backend
 * - Loading states for asynchronous operations
 * - User notifications for success and error cases
 * - Coordinating with services to perform algorithm execution
 * 
 * Unlike CRUD stores, this store manages a complex business operation that requires
 * collecting data across multiple steps and then sending it all at once to the backend.
 */
export const useAlgorithmStore = defineStore('Algorithm', {
  /**
   * State definition
   * 
   * Defines the reactive state properties for algorithm execution parameters and status.
   */
  state: () => ({
    /**
     * Step 1: Facilities and Vehicles Selection
     * Array of facilities with their respective selected vehicles for the algorithm
     */
    facilitiesWithVehicles: [] as FacilityVehicleSelection[],

    /**
     * Step 2: Containers Selection
     * Array of container IDs to include in the algorithm execution
     */
    selectedContainerIds: [] as string[],

    /**
     * Step 3: Extra Data
     * Additional parameters for algorithm execution
     */
    extraData: {
      numberOfDays: 7,
      averagePickupTimeMinutes: 15,
      maxBudgetAmount: 100,
    } as {
      numberOfDays: number;
      averagePickupTimeMinutes: number;
      maxBudgetAmount: number;
    },

    /**
     * Algorithm execution result from the backend
     */
    executionResult: undefined as CreateAlgorithmResult | undefined,

    /**
     * Repository instance for HTTP communication
     */
    algorithmRepository: new AlgorithmHttpRepository(),

    /**
     * Loading indicator for async operations
     */
    loading: false,

    /**
     * Notification object for user feedback
     */
    algorithmNotification: {
      flag: false,          // Whether to show the notification
      title: '',            // Notification title
      msg: '',              // Notification message content
      icon: '',             // Icon identifier (e.g., 'mdi-check')
      timeout: 4000,        // Auto-hide duration in milliseconds
      color: '',            // Color theme (e.g., 'success', 'error')
    },

    /**
     * When set, execute-algorithm view will apply this JSON (same shape as the execute API payload)
     * after reset + initial data load, then clear it.
     */
    pendingExecutionRequestJson: null as string | null,
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
     * Check if all required data has been provided
     * @param state Current store state
     * @returns Boolean indicating if the form is ready to submit
     */
    isFormValid: (state) => {
      return (
        state.facilitiesWithVehicles.length > 0 &&
        state.facilitiesWithVehicles.every(f => f.selectedVehicleIds.length > 0) &&
        state.selectedContainerIds.length > 0 &&
        state.extraData.numberOfDays > 0 &&
        state.extraData.averagePickupTimeMinutes > 0
      );
    },

    /**
     * Get the execution result
     * @param state Current store state
     * @returns The algorithm execution result or undefined
     */
    getExecutionResult: (state) => state.executionResult,
  },

  /**
   * Actions
   * 
   * Methods that modify the state and interact with services to perform algorithm execution.
   * All async operations handle errors and provide user notifications.
   */
  actions: {
    /**
     * Set the facilities with their selected vehicles for Step 1
     * 
     * @param facilities Array of facilities with selected vehicles
     */
    setFacilitiesWithVehicles(facilities: FacilityVehicleSelection[]) {
      this.facilitiesWithVehicles = facilities;
    },

    /**
     * Add a facility with selected vehicles
     * 
     * @param facility Facility with selected vehicles to add
     */
    addFacilityWithVehicles(facility: FacilityVehicleSelection) {
      const exists = this.facilitiesWithVehicles.some(f => f.facilityId === facility.facilityId);
      if (!exists) {
        this.facilitiesWithVehicles.push(facility);
      }
    },

    /**
     * Remove a facility from the selection
     * 
     * @param facilityId UUID string of the facility to remove
     */
    removeFacilityWithVehicles(facilityId: string) {
      this.facilitiesWithVehicles = this.facilitiesWithVehicles.filter(
        f => f.facilityId !== facilityId
      );
    },

    /**
     * Remove all facilities that do not have selected vehicles.
     * This keeps the payload aligned with the backend validation rules.
     */
    removeFacilitiesWithoutVehicles() {
      this.facilitiesWithVehicles = this.facilitiesWithVehicles.filter(
        f => f.selectedVehicleIds.length > 0
      );
    },

    /**
     * Set the selected containers for Step 2
     * 
     * @param containerIds Array of container ID strings
     */
    setSelectedContainers(containerIds: string[]) {
      this.selectedContainerIds = containerIds;
    },

    /**
     * Add a container to the selection
     * 
     * @param containerId UUID string of the container
     */
    addSelectedContainer(containerId: string) {
      if (!this.selectedContainerIds.includes(containerId)) {
        this.selectedContainerIds.push(containerId);
      }
    },

    /**
     * Remove a container from the selection
     * 
     * @param containerId UUID string of the container to remove
     */
    removeSelectedContainer(containerId: string) {
      this.selectedContainerIds = this.selectedContainerIds.filter(id => id !== containerId);
    },

    /**
     * Set the extra data parameters for Step 3
     * 
     * @param numberOfDays Number of days for the algorithm to plan
     * @param averagePickupTimeMinutes Average pickup time in minutes
     */
    setExtraData(numberOfDays: number, averagePickupTimeMinutes: number) {
      this.extraData = {
        numberOfDays,
        averagePickupTimeMinutes,
        maxBudgetAmount: this.extraData?.maxBudgetAmount ?? 100,
      };
    },

    setMaxBudgetAmount(amount: number) {
      // Preserve other extraData fields when updating only the budget
      this.extraData = {
        numberOfDays: this.extraData.numberOfDays,
        averagePickupTimeMinutes: this.extraData.averagePickupTimeMinutes,
        maxBudgetAmount: amount,
      };
    },

    /**
     * Execute the algorithm by sending all collected data to the backend
     * 
     * Validates that all required data is present before execution.
     * Uses the CreateAlgorithmService to handle the business logic.
     */
    async executeAlgorithm(): Promise<CreateAlgorithmResult | undefined> {
      this.removeFacilitiesWithoutVehicles();

      // Validate that all required data is present
      if (!this.isFormValid) {
        this.setNotification(
          i18n.global.t('common.errors.titles.validation'),
          i18n.global.t('common.errors.store.selectVehiclePerFacility'),
          'mdi-alert',
          'warning'
        );
        return undefined;
      }

      this.loading = true;

      // Create service instance with repository
      const createService = new CreateAlgorithmService(this.algorithmRepository);

      // Build the command from the collected data
      const command: CreateAlgorithmCommand = {
        facilitiesWithVehicles: this.facilitiesWithVehicles.map(f => ({
          facilityId: new UllUUID(f.facilityId),
          selectedVehicleIds: f.selectedVehicleIds.map(v => new UllUUID(v)),
        })),
        selectedContainerIds: this.selectedContainerIds.map(c => new UllUUID(c)),
        numberOfDays: this.extraData.numberOfDays,
        averagePickupTimeMinutes: this.extraData.averagePickupTimeMinutes,
        maxBudget: {
          amount: this.extraData.maxBudgetAmount,
          currency: 'EUR',
        },
      };

      // Execute the algorithm service
      const result = await createService.execute(command);

      // Handle the result using Either pattern
      return result.fold(
        error => {
          // Error case: handle and notify user
          this.handleError(error);
          this.loading = false;
          return undefined;
        },
        data => {
          // Success case: store result and notify user based on backend status
          this.executionResult = data;
          this.loading = false;
          if (data.status === 'success') {
            this.setNotification(
              i18n.global.t('common.errors.titles.success'),
              translateBackendMessage(data.message),
              'mdi-check-circle',
              'success'
            );
          } else {
            const translatedMessage = translateBackendMessage(data.message);
            const translatedDetails = translateBackendMessage(data.details);
            const message = translatedDetails
              ? `${translatedMessage}. ${translatedDetails}`
              : translatedMessage;

            this.setNotification(
              i18n.global.t('common.errors.titles.error'),
              message,
              'mdi-alert-circle',
              'error'
            );
          }

          return data;
        }
      );
    },

    /**
     * Reset all form data to initial state
     * 
     * Clears all selected facilities, vehicles, containers, and extra data.
     * Use this after a successful algorithm execution or when starting a new execution.
     */
    resetForm() {
      this.facilitiesWithVehicles = [];
      this.selectedContainerIds = [];
      this.extraData = {
        numberOfDays: 7,
        averagePickupTimeMinutes: 15,
        maxBudgetAmount: 100,
      };
      this.executionResult = undefined;
    },

    /**
     * Queue a replay of a previous run's client payload (e.g. from an infrastructure plan detail).
     */
    queueExecutionRequestReplay(json: string) {
      this.pendingExecutionRequestJson = json.trim().length > 0 ? json : null;
    },

    /**
     * Remove and return queued execution JSON.
     */
    dequeuePendingExecutionRequestJson(): string | null {
      const value = this.pendingExecutionRequestJson;
      this.pendingExecutionRequestJson = null;
      return value;
    },

    /**
     * Parses execution request JSON and writes facilities, containers, and extra parameters.
     * Expects the same structure as {@link CreateAlgorithmCommand} serialization.
     *
     * @returns true when the payload was applied and passes {@link isFormValid}
     */
    applyExecutionRequestFromJson(json: string): boolean {
      let parsed: unknown;
      try {
        parsed = JSON.parse(json) as unknown;
      } catch {
        return false;
      }
      if (!parsed || typeof parsed !== 'object') {
        return false;
      }
      const root = parsed as Record<string, unknown>;

      const rawFacilities = root.facilitiesWithVehicles;
      if (!Array.isArray(rawFacilities)) {
        return false;
      }
      const facilities: FacilityVehicleSelection[] = [];
      for (const entry of rawFacilities) {
        if (!entry || typeof entry !== 'object') {
          continue;
        }
        const row = entry as Record<string, unknown>;
        const facilityId = typeof row.facilityId === 'string' ? row.facilityId : null;
        const vehicleIdsRaw = row.selectedVehicleIds;
        if (!facilityId || !Array.isArray(vehicleIdsRaw)) {
          continue;
        }
        const selectedVehicleIds = vehicleIdsRaw.filter((id): id is string => typeof id === 'string');
        facilities.push({ facilityId, selectedVehicleIds });
      }

      const rawContainers = root.selectedContainerIds;
      const selectedContainerIds = Array.isArray(rawContainers)
        ? rawContainers.filter((id): id is string => typeof id === 'string')
        : [];

      const numberOfDays =
        typeof root.numberOfDays === 'number' && Number.isFinite(root.numberOfDays) && root.numberOfDays > 0
          ? Math.floor(root.numberOfDays)
          : 7;
      const averagePickupTimeMinutes =
        typeof root.averagePickupTimeMinutes === 'number' &&
        Number.isFinite(root.averagePickupTimeMinutes) &&
        root.averagePickupTimeMinutes > 0
          ? Math.floor(root.averagePickupTimeMinutes)
          : 15;

      this.setFacilitiesWithVehicles(facilities);
      this.setSelectedContainers(selectedContainerIds);
      this.setExtraData(numberOfDays, averagePickupTimeMinutes);

      const maxBudget = root.maxBudget;
      if (maxBudget && typeof maxBudget === 'object') {
        const amount = (maxBudget as Record<string, unknown>).amount;
        if (typeof amount === 'number' && Number.isFinite(amount) && amount >= 0) {
          this.setMaxBudgetAmount(amount);
        }
      }

      return this.isFormValid;
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
      this.algorithmNotification = {
        flag: true,
        title,
        msg,
        icon,
        timeout: 4000,
        color,
      };
    },

    /**
     * Handle errors from algorithm execution
     * 
     * Logs the error to console and displays a user-friendly error notification.
     * Different error types can be handled differently based on the error kind.
     * 
     * @param error DataError object containing error information
     */
    handleError(error: any) {
      console.error('Algorithm store error:', error);
      const { title, message } = resolveBackendError(error, 'algorithm');
      this.setNotification(title, message, 'mdi-alert', 'error');
    },
  },
});
