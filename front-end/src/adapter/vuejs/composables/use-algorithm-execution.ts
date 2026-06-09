import { useAlgorithmStore } from '@/adapter/vuejs/stores/algorithm-store';
import { useContainerStore } from '@/adapter/vuejs/stores/container-store';
import { useFacilityStore } from '@/adapter/vuejs/stores/facility-store';
import { useVehicleStore } from '@/adapter/vuejs/stores/vehicle-store';
import { facilityStatusLabel, facilityStatusToOptions } from '@/domain/enumerate/facility-status';
import { facilityTypeLabel, facilityTypeToOptions } from '@/domain/enumerate/facility-type';
import { serviceZoneLabel, serviceZoneToOptions } from '@/domain/enumerate/service-zone';
import { vehicleTypeLabel, vehicleTypeToOptions } from '@/domain/enumerate/vehicle-type';
import { wasteTypeLabel, wasteTypeToOptions } from '@/domain/enumerate/waste-type';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

export function useAlgorithmExecution() {
  const { t } = useI18n();

  // Get stores
  const algorithmStore = useAlgorithmStore();
  const facilityStore = useFacilityStore();
  const vehicleStore = useVehicleStore();

  // Get reactive data from stores
  const { facilities: allFacilities, loading: facilityLoading, totalFacilities, currentPage, rowsPerPage } = storeToRefs(facilityStore);
  const { vehicles: allVehicles } = storeToRefs(vehicleStore);

  // Step control
  const currentStep = ref(1);

  // Pagination and filtering state for Step 1
  const tablePage = ref(1);
  const itemsPerPage = ref(10);
  const currentSortBy = ref<string | undefined>(undefined);
  const currentSortOrder = ref<'asc' | 'desc'>('asc');
  const selectedFacilityTypeFilter = ref<string | undefined>(undefined);
  const selectedFacilityStatusFilter = ref<string | undefined>(undefined);
  const selectedLocationFilter = ref<string | undefined>(undefined);
  const selectedFacilityForVehiclesId = ref<string | null>(null);

  // Vehicle selection state
  const vehicleTablePage = ref(1);
  const vehicleItemsPerPage = ref(10);
  const vehicleSortBy = ref<string | undefined>(undefined);
  const vehicleSortOrder = ref<'asc' | 'desc'>('asc');
  const selectedVehicleTypeFilterDialog = ref<string | undefined>(undefined);
  const vehicleLoading = ref(false);
  const totalVehicles = ref(0);
  const vehicleCurrentPage = ref(0);
  const tempSelectedVehicleIds = ref<string[]>([]);

  // Alias for easier access
  const facilities = computed(() => allFacilities.value);

  const currentStepTitle = computed(() => {
    switch (currentStep.value) {
      case 1:
        return t('algorithm.execute.step1.title');
      case 2:
        return t('algorithm.execute.step2.title');
      case 3:
        return t('algorithm.execute.step3.title');
      default:
        return '';
    }
  });

  // Table headers
  const headers = computed(() => [
    {
      title: '',
      align: 'center' as const,
      sortable: false,
      key: 'select',
      width: '50px',
    },
    {
      title: t('facility.list.table.headers.name'),
      align: 'start' as const,
      sortable: false,
      key: 'name',
    },
    {
      title: t('facility.list.table.headers.type'),
      align: 'start' as const,
      sortable: true,
      key: 'type',
    },
    {
      title: t('facility.list.table.headers.location'),
      align: 'start' as const,
      sortable: true,
      key: 'location',
    },
    {
      title: t('facility.list.table.headers.storageCapacity'),
      align: 'center' as const,
      sortable: true,
      key: 'storageCapacity',
    },
    {
      title: t('facility.list.table.headers.processingCapacity'),
      align: 'center' as const,
      sortable: true,
      key: 'processingCapacity',
    },
    {
      title: t('facility.list.table.headers.unloadingTime'),
      align: 'center' as const,
      sortable: true,
      key: 'unloadingTime',
    },
    {
      title: t('facility.list.table.headers.openingCost'),
      align: 'center' as const,
      sortable: true,
      key: 'openingCost',
    },
  ]);

  // Filter options
  const facilityTypeFilterOptions = computed(() => facilityTypeToOptions(t));
  const facilityStatusFilterOptions = computed(() => facilityStatusToOptions(t));
  const vehicleTypeFilterOptions = computed(() => vehicleTypeToOptions(t));

  // Vehicle table headers for dialog
  const vehicleHeaders = computed(() => [
    {
      title: '',
      align: 'center' as const,
      sortable: false,
      key: 'select',
      width: '50px',
    },
    {
      title: t('vehicle.list.table.headers.name'),
      align: 'start' as const,
      sortable: false,
      key: 'name',
    },
    {
      title: t('vehicle.list.table.headers.type'),
      align: 'start' as const,
      sortable: true,
      key: 'type',
    },
    {
      title: t('vehicle.list.table.headers.capacityKilograms'),
      align: 'center' as const,
      sortable: true,
      key: 'capacityKilograms',
    },
    {
      title: t('vehicle.list.table.headers.capacityLiters'),
      align: 'center' as const,
      sortable: true,
      key: 'capacityLiters',
    },
    {
      title: t('vehicle.list.table.headers.cost'),
      align: 'center' as const,
      sortable: true,
      key: 'cost',
    },
  ]);

  // Format facilities for table display
  const facilityItems = computed(() => {
    return facilities.value.map((facility) => {
      const location = facility.getLocation();
      const storageCapacity = facility.getStorageCapacity().getKilograms();
      const processingCapacity = facility.getProcessingCapacity().getKilogramsPerDay();
      const unloadingTime = facility.getUnloadingTime().getMinutes();
      const openingCost = facility.getOpeningFixedCost();

      return {
        id: facility.getId().toString(),
        name: facility.getName().getValue(),
        rawFacilityType: facility.getFacilityType(),
        rawStatus: facility.getStatus(),
        type: facilityTypeLabel(t, facility.getFacilityType()),
        location: location.postalAddress,
        storageCapacity: storageCapacity.toFixed(2),
        processingCapacity: processingCapacity.toFixed(2),
        unloadingTime,
        openingCost: openingCost.getAmount().toFixed(2),
        status: facilityStatusLabel(t, facility.getStatus()),
      };
    });
  });

  /** Pins for Step 1 map: current facility page, gray vs primary by algorithm selection. */
  const step1FacilityMapPins = computed(() =>
    facilities.value.map((facility) => {
      const location = facility.getLocation();
      const id = facility.getId().toString();
      const selected = algorithmStore.facilitiesWithVehicles.some((f) => f.facilityId === id);
      return {
        id,
        latitude: location.latitude,
        longitude: location.longitude,
        label: facility.getName().getValue(),
        markerTone: selected ? ('primary' as const) : ('muted' as const),
      };
    }),
  );

  // Format vehicles for table display in dialog
  const vehicleItems = computed(() => {
    return allVehicles.value.map((vehicle) => ({
      id: vehicle.getId().toString(),
      name: vehicle.getName().getValue(),
      rawType: vehicle.getVehicleType(),
      type: vehicleTypeLabel(t, vehicle.getVehicleType()),
      capacityKilograms: vehicle.getCapacityKilograms().getKilograms(),
      capacityLiters: vehicle.getCapacityLiters().getLiters(),
      cost: vehicle.getCostPerKilometer().getAmount().toFixed(2),
    }));
  });

  /**
   * Validate that step 1 has at least one facility with vehicles selected
   */
  const isStep1Valid = computed(() => {
    return algorithmStore.facilitiesWithVehicles.length > 0 &&
           algorithmStore.facilitiesWithVehicles.every(f => f.selectedVehicleIds.length > 0);
  });

  /**
   * Calculate total number of selected vehicles
   */
  const totalSelectedVehicles = computed(() => {
    return algorithmStore.facilitiesWithVehicles.reduce(
      (total, f) => total + f.selectedVehicleIds.length,
      0
    );
  });

  /**
   * Get the currently selected facility for vehicle selection
   */
  const selectedFacilityForVehicles = computed(() => {
    if (!selectedFacilityForVehiclesId.value) return null;
    return facilityItems.value.find(f => f.id === selectedFacilityForVehiclesId.value);
  });

  // ========== FUNCTIONS ==========

  /**
   * Load facilities from backend
   */
  const loadFacilities = async (
    page: number,
    size: number,
    sortBy?: string,
    sortOrder?: 'asc' | 'desc',
    facilityType?: string,
    status?: string,
    location?: string
  ) => {
    currentSortBy.value = sortBy;
    currentSortOrder.value = sortOrder ?? 'asc';
    await facilityStore.getFacilities(page, size, sortBy, sortOrder, facilityType, status, location);
  };

  /**
   * Load all facilities and vehicles on component mount
   */
  const initializeData = async () => {
    await loadFacilities(currentPage.value, rowsPerPage.value);
    tablePage.value = currentPage.value + 1;
    itemsPerPage.value = rowsPerPage.value;
    
    // Load all vehicles
    await vehicleStore.getVehicles(0, 1000);
  };

  /**
   * Handle facility type filter change
   */
  const onFacilityTypeFilterChange = async (newType: string | null) => {
    const facilityType = newType ?? undefined;
    await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, facilityType, selectedFacilityStatusFilter.value, selectedLocationFilter.value);
    tablePage.value = currentPage.value + 1;
  };

  /**
   * Handle facility status filter change
   */
  const onFacilityStatusFilterChange = async (newStatus: string | null) => {
    const status = newStatus ?? undefined;
    await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value, status, selectedLocationFilter.value);
    tablePage.value = currentPage.value + 1;
  };

  /**
   * Handle location filter change - used for both Step 1 and Step 2
   */
  const onLocationFilterChange = async (newLocation: string | null) => {
    const location = newLocation ?? undefined;
    // Determine which step and apply filter accordingly
    if (currentStep.value === 1) {
      await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value, selectedFacilityStatusFilter.value, location);
      tablePage.value = currentPage.value + 1;
    } else if (currentStep.value === 2) {
      await loadContainers(0, step2ItemsPerPage.value, step2CurrentSortBy.value, step2CurrentSortOrder.value, selectedWasteTypeFilter.value, selectedServiceZoneFilter.value, location);
      step2TablePage.value = containerCurrentPage.value + 1;
    }
  };

  /**
   * Handle table options change (pagination, sorting)
   */
  const onTableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {
    const requestedSize = options.itemsPerPage;
    if (requestedSize <= 0) {
      return;
    }

    const requestedPage = Math.max(options.page - 1, 0);
    const newSortBy = options.sortBy[0]?.key;
    const newSortOrder = options.sortBy[0]?.order ?? 'asc';

    await loadFacilities(requestedPage, requestedSize, newSortBy, newSortOrder, selectedFacilityTypeFilter.value, selectedFacilityStatusFilter.value, selectedLocationFilter.value);
    tablePage.value = currentPage.value + 1;
    itemsPerPage.value = rowsPerPage.value;
  };

  /**
   * Check if a facility is selected
   */
  const isFacilitySelected = (facilityId: string): boolean => {
    return algorithmStore.facilitiesWithVehicles.some(f => f.facilityId === facilityId);
  };

  /**
   * Check if a vehicle is selected for a facility
   */
  const isVehicleSelected = (facilityId: string, vehicleId: string): boolean => {
    const facility = algorithmStore.facilitiesWithVehicles.find(f => f.facilityId === facilityId);
    return facility ? facility.selectedVehicleIds.includes(vehicleId) : false;
  };

  /**
   * Toggle facility selection
   */
  const toggleFacility = (facilityId: string) => {
    if (isFacilitySelected(facilityId)) {
      // Remove facility
      algorithmStore.removeFacilityWithVehicles(facilityId);
      selectedFacilityForVehiclesId.value = null;
    } else {
      // Add facility with empty vehicle list
      algorithmStore.addFacilityWithVehicles({
        facilityId,
        selectedVehicleIds: [],
      });
      // Set as selected facility for vehicle selection and open dialog
      selectedFacilityForVehiclesId.value = facilityId;
      openVehicleDialog(facilityId);
    }
  };

  /**
   * Toggle vehicle selection for a facility
   */
  const toggleVehicle = (facilityId: string, vehicleId: string) => {
    const facilityIndex = algorithmStore.facilitiesWithVehicles.findIndex(f => f.facilityId === facilityId);
    
    if (facilityIndex !== -1) {
      const facility = algorithmStore.facilitiesWithVehicles[facilityIndex];
      
      if (facility.selectedVehicleIds.includes(vehicleId)) {
        // Remove vehicle
        facility.selectedVehicleIds = facility.selectedVehicleIds.filter(v => v !== vehicleId);
      } else {
        // Add vehicle
        facility.selectedVehicleIds.push(vehicleId);
      }
      
      // Trigger reactivity by reassigning the array
      algorithmStore.facilitiesWithVehicles[facilityIndex] = { ...facility };
    }
  };

  /**
   * Get vehicles for a specific facility (currently unused, but kept for potential future use)
   */
  const getVehiclesForFacility = () => {
    return allVehicles.value;
  };

  /**
   * Load vehicles from backend
   */
  const loadVehiclesForDialog = async (
    page: number,
    size: number,
    sortBy?: string,
    sortOrder?: 'asc' | 'desc',
    vehicleType?: string
  ) => {
    vehicleLoading.value = true;
    try {
      await vehicleStore.getVehicles(page, size, sortBy, sortOrder, vehicleType);
      totalVehicles.value = vehicleStore.totalVehicles;
      vehicleCurrentPage.value = vehicleStore.currentPage;
    } finally {
      vehicleLoading.value = false;
    }
  };

  /**
   * Open vehicle selection card
   */
  const openVehicleDialog = async (facilityId: string) => {
    // Initialize temp selected vehicles with current selection for this facility
    const facility = algorithmStore.facilitiesWithVehicles.find(f => f.facilityId === facilityId);
    tempSelectedVehicleIds.value = facility?.selectedVehicleIds ?? [];
    
    // Load vehicles
    vehicleTablePage.value = 1;
    vehicleItemsPerPage.value = 10;
    vehicleSortBy.value = undefined;
    vehicleSortOrder.value = 'asc';
    selectedVehicleTypeFilterDialog.value = undefined;
    
    await loadVehiclesForDialog(0, 10);
    // Card will show automatically because selectedFacilityForVehicles is now set
  };

  /**
   * Open the vehicle panel for a facility that is already part of the execution selection,
   * without toggling the facility checkbox (row click vs checkbox).
   */
  const openFacilityVehiclesPanel = async (facilityId: string) => {
    if (!isFacilitySelected(facilityId)) {
      return;
    }
    selectedFacilityForVehiclesId.value = facilityId;
    await openVehicleDialog(facilityId);
  };

  /**
   * Close vehicle selection card without saving
   */
  const closeVehicleDialog = () => {
    // Deselect facility to hide card
    selectedFacilityForVehiclesId.value = null;
    tempSelectedVehicleIds.value = [];
  };

  /**
   * Confirm vehicle selection and save to store
   */
  const confirmVehicleSelection = () => {
    if (!selectedFacilityForVehiclesId.value) return;
    
    // Find the facility in store
    const facilityIndex = algorithmStore.facilitiesWithVehicles.findIndex(
      f => f.facilityId === selectedFacilityForVehiclesId.value
    );
    
    if (facilityIndex !== -1) {
      if (tempSelectedVehicleIds.value.length === 0) {
        algorithmStore.removeFacilityWithVehicles(selectedFacilityForVehiclesId.value);
      } else {
        algorithmStore.facilitiesWithVehicles[facilityIndex].selectedVehicleIds = [...tempSelectedVehicleIds.value];
      }
    }
    
    closeVehicleDialog();
  };

  /**
   * Check if a vehicle is selected in the dialog
   */
  const isVehicleSelectedInDialog = (vehicleId: string): boolean => {
    return tempSelectedVehicleIds.value.includes(vehicleId);
  };

  /**
   * Toggle vehicle selection in dialog
   */
  const toggleVehicleInDialog = (vehicleId: string) => {
    const index = tempSelectedVehicleIds.value.indexOf(vehicleId);
    
    if (index !== -1) {
      // Remove vehicle
      tempSelectedVehicleIds.value.splice(index, 1);
    } else {
      // Check if vehicle is already assigned to another facility
      if (isVehicleSelectedInOtherFacility(vehicleId)) {
        // Don't allow adding - vehicle is already in another facility
        return;
      }
      // Add vehicle
      tempSelectedVehicleIds.value.push(vehicleId);
    }
  };

  /**
   * Check if a vehicle is already selected in another facility
   */
  const isVehicleSelectedInOtherFacility = (vehicleId: string): boolean => {
    if (!selectedFacilityForVehiclesId.value) return false;
    
    // Check all facilities except the current one
    return algorithmStore.facilitiesWithVehicles.some(facility => 
      facility.facilityId !== selectedFacilityForVehiclesId.value &&
      facility.selectedVehicleIds.includes(vehicleId)
    );
  };

  /**
   * Handle vehicle table options change (pagination, sorting)
   */
  const onVehicleTableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {
    const requestedSize = options.itemsPerPage;
    if (requestedSize <= 0) {
      return;
    }

    const requestedPage = Math.max(options.page - 1, 0);
    const newSortBy = options.sortBy[0]?.key;
    const newSortOrder = options.sortBy[0]?.order ?? 'asc';

    await loadVehiclesForDialog(requestedPage, requestedSize, newSortBy, newSortOrder, selectedVehicleTypeFilterDialog.value);
    vehicleTablePage.value = vehicleCurrentPage.value + 1;
    vehicleItemsPerPage.value = vehicleStore.rowsPerPage;
  };

  /**
   * Handle vehicle type filter change
   */
  const onVehicleTypeFilterDialogChange = async (newType: string | null) => {
    const vehicleType = newType ?? undefined;
    await loadVehiclesForDialog(0, vehicleItemsPerPage.value, vehicleSortBy.value, vehicleSortOrder.value, vehicleType);
    vehicleTablePage.value = vehicleCurrentPage.value + 1;
  };

  /**
   * Execute the algorithm by sending all collected data to the backend
   */
  const executeAlgorithm = async () => {
    return await algorithmStore.executeAlgorithm();
  };

  /**
   * Reset the form to initial state
   */
  const resetForm = () => {
    algorithmStore.resetForm();
    currentStep.value = 1;
  };

  // ========== STEP 2: CONTAINERS SELECTION ==========

  const containerStore = useContainerStore();
  const { containers: allContainers, loading: containerLoading, totalContainers: step2TotalContainers, currentPage: containerCurrentPage, rowsPerPage: containerRowsPerPage } = storeToRefs(containerStore);

  // Pagination and filtering state for Step 2
  const step2TablePage = ref(1);
  const step2ItemsPerPage = ref(10);
  const step2CurrentSortBy = ref<string | undefined>(undefined);
  const step2CurrentSortOrder = ref<'asc' | 'desc'>('asc');
  const selectedWasteTypeFilter = ref<string | undefined>(undefined);
  const selectedServiceZoneFilter = ref<string | undefined>(undefined);
  const selectedContainerLocationFilter = ref<string | undefined>(undefined);

  const totalContainers = computed(() => step2TotalContainers.value);

  const step2Headers = computed(() => [
    {
      title: '',
      align: 'center' as const,
      sortable: false,
      key: 'select',
      width: '50px',
    },
    {
      title: t('container.list.table.headers.name'),
      align: 'start' as const,
      sortable: false,
      key: 'name',
    },
    {
      title: t('container.list.table.headers.wasteType'),
      align: 'start' as const,
      sortable: true,
      key: 'wasteType',
    },
    {
      title: t('container.list.table.headers.location'),
      align: 'start' as const,
      sortable: true,
      key: 'location',
    },
    {
      title: t('container.list.table.headers.capacity'),
      align: 'center' as const,
      sortable: true,
      key: 'capacityLiters',
    },
    {
      title: t('container.list.table.headers.demand'),
      align: 'center' as const,
      sortable: true,
      key: 'demand',
    },
    {
      title: t('container.list.table.headers.serviceZone'),
      align: 'center' as const,
      sortable: true,
      key: 'serviceZone',
    },
  ]);

  const wasteTypeFilterOptions = computed(() => wasteTypeToOptions(t));
  const serviceZoneFilterOptions = computed(() => serviceZoneToOptions(t));

  const step2ContainerItems = computed(() => {
    return allContainers.value.map((container) => {
      const location = container.getLocation();
      const capacity = container.getCapacityLiters();
      const dailyDemand = container.getDailyDemandLitersPerDay();
      const serviceZone = container.getServiceZone();

      return {
        id: container.getId().toString(),
        name: container.getName().getValue(),
        rawWasteType: container.getWasteType(),
        rawServiceZone: serviceZone,
        wasteType: wasteTypeLabel(t, container.getWasteType()),
        location: location.postalAddress,
        capacityLiters: capacity.getLiters(),
        demand: dailyDemand.getLitersPerDay(),
        serviceZone: serviceZone
          ? serviceZoneLabel(t, serviceZone)
          : t('container.list.notAssigned'),
      };
    });
  });

  /** Pins for Step 2 map: current container page, primary vs muted by algorithm selection. */
  const step2ContainerMapPins = computed(() =>
    allContainers.value.map((container) => {
      const location = container.getLocation();
      const id = container.getId().toString();
      const selected = algorithmStore.selectedContainerIds.includes(id);
      return {
        id,
        latitude: location.latitude,
        longitude: location.longitude,
        label: container.getName().getValue(),
        markerTone: selected ? ('primary' as const) : ('muted' as const),
      };
    }),
  );

  /**
   * Validate that step 2 has at least one container selected
   */
  const isStep2Valid = computed(() => {
    return algorithmStore.selectedContainerIds.length > 0;
  });

  // ========== STEP 3: EXTRA DATA (DAYS AND PICKUP TIME) ==========

  /**
   * Writable references to extra data from store
   */
  const numberOfDaysRef = computed({
    get: () => algorithmStore.extraData.numberOfDays,
    set: (value: number) => {
      algorithmStore.setExtraData(value, algorithmStore.extraData.averagePickupTimeMinutes);
    }
  });

  const averagePickupTimeMinutesRef = computed({
    get: () => algorithmStore.extraData.averagePickupTimeMinutes,
    set: (value: number) => {
      algorithmStore.setExtraData(algorithmStore.extraData.numberOfDays, value);
    }
  });

  const maxBudgetAmountRef = computed({
    get: () => algorithmStore.extraData.maxBudgetAmount,
    set: (value: number) => {
      algorithmStore.setMaxBudgetAmount(value);
    }
  });

  /**
   * Validate that step 3 has valid extra data
   */
  const isStep3Valid = computed(() => {
    return algorithmStore.extraData.numberOfDays > 0 && 
           algorithmStore.extraData.averagePickupTimeMinutes > 0;
  });

  /**
   * Update extra data in store
   */
  const setExtraData = (numberOfDays: number, averagePickupTimeMinutes: number) => {
    algorithmStore.setExtraData(numberOfDays, averagePickupTimeMinutes);
  };

  /**
   * Load containers from backend
   */
  const loadContainers = async (
    page: number,
    size: number,
    sortBy?: string,
    sortOrder?: 'asc' | 'desc',
    wasteType?: string,
    serviceZone?: string,
    location?: string
  ) => {
    step2CurrentSortBy.value = sortBy;
    step2CurrentSortOrder.value = sortOrder ?? 'asc';
    await containerStore.getContainers(page, size, sortBy, sortOrder, wasteType, serviceZone, location);
  };

  /**
   * Initialize containers on component mount
   */
  const initializeContainers = async () => {
    await loadContainers(containerCurrentPage.value, containerRowsPerPage.value);
    step2TablePage.value = containerCurrentPage.value + 1;
    step2ItemsPerPage.value = containerRowsPerPage.value;
  };

  /**
   * Handle waste type filter change
   */
  const onWasteTypeFilterChange = async (newType: string | null) => {
    const wasteType = newType ?? undefined;
    await loadContainers(0, step2ItemsPerPage.value, step2CurrentSortBy.value, step2CurrentSortOrder.value, wasteType, selectedServiceZoneFilter.value, selectedContainerLocationFilter.value);
    step2TablePage.value = containerCurrentPage.value + 1;
  };

  /**
   * Handle service zone filter change
   */
  const onServiceZoneFilterChange = async (newZone: string | null) => {
    const serviceZone = newZone ?? undefined;
    await loadContainers(0, step2ItemsPerPage.value, step2CurrentSortBy.value, step2CurrentSortOrder.value, selectedWasteTypeFilter.value, serviceZone, selectedContainerLocationFilter.value);
    step2TablePage.value = containerCurrentPage.value + 1;
  };

  /**
   * Handle container location filter change
   */
  const onContainerLocationFilterChange = async (newLocation: string | null) => {
    const location = newLocation ?? undefined;
    await loadContainers(0, step2ItemsPerPage.value, step2CurrentSortBy.value, step2CurrentSortOrder.value, selectedWasteTypeFilter.value, selectedServiceZoneFilter.value, location);
    step2TablePage.value = containerCurrentPage.value + 1;
  };

  /**
   * Handle table options change (pagination, sorting)
   */
  const onStep2TableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {
    const requestedSize = options.itemsPerPage;
    if (requestedSize <= 0) {
      return;
    }

    const requestedPage = Math.max(options.page - 1, 0);
    const newSortBy = options.sortBy[0]?.key;
    const newSortOrder = options.sortBy[0]?.order ?? 'asc';

    await loadContainers(requestedPage, requestedSize, newSortBy, newSortOrder, selectedWasteTypeFilter.value, selectedServiceZoneFilter.value, selectedContainerLocationFilter.value);
    step2TablePage.value = containerCurrentPage.value + 1;
    step2ItemsPerPage.value = containerRowsPerPage.value;
  };

  /**
   * Check if a container is selected
   */
  const isContainerSelected = (containerId: string): boolean => {
    return algorithmStore.selectedContainerIds.includes(containerId);
  };

  /**
   * Check whether all containers currently visible in the step 2 table are selected.
   */
  const areAllVisibleContainersSelected = computed(() => {
    const visibleContainerIds = step2ContainerItems.value.map((container) => container.id);
    return visibleContainerIds.length > 0 && visibleContainerIds.every((containerId) => isContainerSelected(containerId));
  });

  /**
   * Toggle container selection
   */
  const toggleContainer = (containerId: string) => {
    if (isContainerSelected(containerId)) {
      algorithmStore.removeSelectedContainer(containerId);
    } else {
      algorithmStore.addSelectedContainer(containerId);
    }
  };

  /**
   * Toggle the bulk selection of all containers currently visible in the step 2 table.
   */
  const toggleVisibleContainersSelection = () => {
    const visibleContainerIds = step2ContainerItems.value.map((container) => container.id);

    if (areAllVisibleContainersSelected.value) {
      const visibleContainerIdSet = new Set(visibleContainerIds);
      algorithmStore.setSelectedContainers(
        algorithmStore.selectedContainerIds.filter((containerId) => !visibleContainerIdSet.has(containerId))
      );
      return;
    }

    const nextSelectedContainerIds = Array.from(
      new Set([...algorithmStore.selectedContainerIds, ...visibleContainerIds])
    );

    algorithmStore.setSelectedContainers(nextSelectedContainerIds);
  };

  return {
    // State
    currentStep,
    tablePage,
    itemsPerPage,
    currentSortBy,
    currentSortOrder,
    selectedFacilityTypeFilter,
    selectedFacilityStatusFilter,
    selectedLocationFilter,
    selectedFacilityForVehiclesId,
    vehicleTablePage,
    vehicleItemsPerPage,
    vehicleSortBy,
    vehicleSortOrder,
    selectedVehicleTypeFilterDialog,
    vehicleLoading,
    totalVehicles,
    vehicleCurrentPage,
    tempSelectedVehicleIds,
    facilityLoading,
    totalFacilities,
    
    // Computed
    facilities,
    currentStepTitle,
    headers,
    facilityTypeFilterOptions,
    facilityStatusFilterOptions,
    vehicleTypeFilterOptions,
    vehicleHeaders,
    facilityItems,
    step1FacilityMapPins,
    vehicleItems,
    isStep1Valid,
    totalSelectedVehicles,
    selectedFacilityForVehicles,
    
    // Store references
    algorithmStore,
    facilityStore,
    vehicleStore,
    
    // Methods
    loadFacilities,
    initializeData,
    onFacilityTypeFilterChange,
    onFacilityStatusFilterChange,
    onLocationFilterChange,
    onTableOptionsUpdate,
    isFacilitySelected,
    isVehicleSelected,
    toggleFacility,
    toggleVehicle,
    getVehiclesForFacility,
    loadVehiclesForDialog,
    openVehicleDialog,
    openFacilityVehiclesPanel,
    closeVehicleDialog,
    confirmVehicleSelection,
    isVehicleSelectedInDialog,
    toggleVehicleInDialog,
    isVehicleSelectedInOtherFacility,
    onVehicleTableOptionsUpdate,
    onVehicleTypeFilterDialogChange,
    executeAlgorithm,
    resetForm,
    
    // State - Step 2
    step2TablePage,
    step2ItemsPerPage,
    selectedWasteTypeFilter,
    selectedServiceZoneFilter,
    selectedContainerLocationFilter,
    containerLoading,
    totalContainers,
    
    // Computed - Step 2
    step2Headers,
    wasteTypeFilterOptions,
    serviceZoneFilterOptions,
    step2ContainerItems,
    step2ContainerMapPins,
    isStep2Valid,
    
    // Computed - Step 3
    numberOfDaysRef,
    averagePickupTimeMinutesRef,
    maxBudgetAmountRef,
    isStep3Valid,
    
    // Methods - Step 2
    loadContainers,
    initializeContainers,
    onWasteTypeFilterChange,
    onServiceZoneFilterChange,
    onContainerLocationFilterChange,
    onStep2TableOptionsUpdate,
    isContainerSelected,
    areAllVisibleContainersSelected,
    toggleContainer,
    toggleVisibleContainersSelection,
    
    // Methods - Step 3
    setExtraData,
    
    // Store reference - Step 2
    containerStore,
  };
}
