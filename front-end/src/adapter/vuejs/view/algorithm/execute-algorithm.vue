<template>
  <CrudLayout :title="t('algorithm.execute.title')" icon="mdi-play">
    <div class="pa-4">
      <v-stepper 
        v-model="currentStep"
        :items="stepperItems"
        class="elevation-0"
        hide-actions
      >
        <template v-slot:default>
          <v-stepper-window>
            
            <v-stepper-window-item :value="1">
              <v-card flat class="mt-4" :title="t('algorithm.execute.step1.title')">
                <v-card-actions>
                  <v-spacer />
                  <v-btn variant="elevated" color="primary" @click="currentStep = 2" :disabled="!isStep1Valid">
                    {{ t('common.buttons.next') }}
                  </v-btn>
                </v-card-actions>
                <v-card-text>
                  <!-- Filtros -->
                  <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center; padding: 8px; margin-bottom: 16px;">
                    <!-- Filter by Facility Type -->
                    <div style="width: 180px;">
                      <v-select
                        v-model="selectedFacilityTypeFilter"
                        :items="facilityTypeFilterOptions"
                        :placeholder="t('facility.list.filterByType')"
                        item-title="title"
                        item-value="value"
                        clearable
                        density="compact"
                        hide-details
                        variant="outlined"
                        @update:model-value="onFacilityTypeFilterChange"
                      />
                    </div>

                    <!-- Filter by Facility Status -->
                    <div style="width: 180px;">
                      <v-select
                        v-model="selectedFacilityStatusFilter"
                        :items="facilityStatusFilterOptions"
                        :placeholder="t('facility.list.filterByStatus')"
                        item-title="title"
                        item-value="value"
                        clearable
                        density="compact"
                        hide-details
                        variant="outlined"
                        @update:model-value="onFacilityStatusFilterChange"
                      />
                    </div>

                    <!-- Filter by Location -->
                    <div style="width: 180px;">
                      <v-text-field
                        v-model="selectedLocationFilter"
                        :placeholder="t('facility.list.filterByLocation')"
                        clearable
                        density="compact"
                        hide-details
                        variant="outlined"
                        @update:model-value="onLocationFilterChange"
                      />
                    </div>
                  </div>

                  <!-- Data Table -->
                  <v-data-table-server
                    :headers="headers"
                    :items="facilityItems"
                    :loading="facilityLoading"
                    :items-length="totalFacilities"
                    v-model:page="tablePage"
                    :items-per-page="itemsPerPage"
                    :items-per-page-options="[
                      { value: 5, title: '5' },
                      { value: 10, title: '10' },
                      { value: 25, title: '25' },
                      { value: 50, title: '50' }
                    ]"
                    @update:options="onTableOptionsUpdate"
                    item-value="id"
                    hover
                    class="elevation-2"
                  >
                    <!-- Checkbox column -->
                    <template v-slot:item.select="{ item }">
                      <v-checkbox
                        :model-value="isFacilitySelected(item.id)"
                        @update:model-value="toggleFacility(item.id)"
                        class="mt-0"
                      />
                    </template>

                    <!-- Type column -->
                    <template v-slot:item.type="{ item }">
                      <v-chip :color="facilityTypeColor(item.rawFacilityType)" size="small">
                        {{ item.type }}
                      </v-chip>
                    </template>

                    <!-- Location column -->
                    <template v-slot:item.location="{ item }">
                      {{ item.location }}
                    </template>

                    <!-- Storage Capacity column -->
                    <template v-slot:item.storageCapacity="{ item }">
                      {{ item.storageCapacity }}
                    </template>

                    <!-- Processing Capacity column -->
                    <template v-slot:item.processingCapacity="{ item }">
                      {{ item.processingCapacity }}
                    </template>

                    <!-- Unloading Time column -->
                    <template v-slot:item.unloadingTime="{ item }">
                      {{ item.unloadingTime }}
                    </template>

                    <!-- Opening Cost column -->
                    <template v-slot:item.openingCost="{ item }">
                      {{ item.openingCost }}
                    </template>

                    <!-- Status column -->
                    <template v-slot:item.status="{ item }">
                      <v-chip
                        :color="facilityStatusColor(item.rawStatus)"
                        size="small"
                        variant="tonal"
                      >
                        {{ item.status }}
                      </v-chip>
                    </template>

                    <!-- No data template -->
                    <template v-slot:no-data>
                      <v-alert type="info" variant="tonal" class="ma-4">
                        {{ t('facility.list.table.noData') }}
                      </v-alert>
                    </template>
                  </v-data-table-server>

                  <!-- Vehicles selector for selected facility - Card -->
                  <v-card v-if="selectedFacilityForVehicles" class="mt-6" elevation="2">
                    <v-card-title class="text-h6 bg-primary text-white">
                      Seleccionar Vehículos - {{ selectedFacilityForVehicles?.location }}
                    </v-card-title>

                    <v-card-text class="pt-6">
                      <!-- Filtros de Vehículos -->
                      <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center; padding: 8px; margin-bottom: 16px;">
                        <div style="width: 180px;">
                          <v-select
                            v-model="selectedVehicleTypeFilterDialog"
                            :items="vehicleTypeFilterOptions"
                            :placeholder="t('vehicle.list.filterByType')"
                            item-title="title"
                            item-value="value"
                            clearable
                            density="compact"
                            hide-details
                            variant="outlined"
                            @update:model-value="onVehicleTypeFilterDialogChange"
                          />
                        </div>
                      </div>

                      <!-- Tabla de Vehículos -->
                      <v-data-table-server
                        :headers="vehicleHeaders"
                        :items="vehicleItems"
                        :loading="vehicleLoading"
                        :items-length="totalVehicles"
                        v-model:page="vehicleTablePage"
                        :items-per-page="vehicleItemsPerPage"
                        :items-per-page-options="[
                          { value: 5, title: '5' },
                          { value: 10, title: '10' },
                          { value: 25, title: '25' },
                          { value: 50, title: '50' }
                        ]"
                        @update:options="onVehicleTableOptionsUpdate"
                        item-value="id"
                        hover
                        class="elevation-1"
                      >
                        <!-- Checkbox column -->
                        <template v-slot:item.select="{ item }">
                          <v-checkbox
                            :model-value="isVehicleSelectedInDialog(item.id)"
                            @update:model-value="toggleVehicleInDialog(item.id)"
                            class="mt-0"
                          />
                        </template>

                        <!-- Type column -->
                        <template v-slot:item.type="{ item }">
                          <v-chip :color="vehicleTypeColor(item.rawType)" size="small">
                            {{ item.type }}
                          </v-chip>
                        </template>

                        <!-- Capacity Kg -->
                        <template v-slot:item.capacityKilograms="{ item }">
                          {{ item.capacityKilograms }} kg
                        </template>

                        <!-- Capacity L -->
                        <template v-slot:item.capacityLiters="{ item }">
                          {{ item.capacityLiters }} L
                        </template>

                        <!-- Cost -->
                        <template v-slot:item.cost="{ item }">
                          {{ item.cost }}
                        </template>

                        <!-- No data -->
                        <template v-slot:no-data>
                          <v-alert type="info" variant="tonal" class="ma-4">
                            {{ t('vehicle.list.table.noData') }}
                          </v-alert>
                        </template>
                      </v-data-table-server>

                      <!-- Resumen de vehículos seleccionados -->
                      <div class="mt-4 pt-2 border-t">
                        <p class="text-caption font-weight-bold">
                          Vehículos seleccionados: {{ tempSelectedVehicleIds.length }}
                        </p>
                      </div>
                    </v-card-text>

                    <v-card-actions class="pa-4 justify-end">
                      <v-btn variant="elevated" color="primary" @click="confirmVehicleSelection">
                        {{ t('common.buttons.confirm') }}
                      </v-btn>
                    </v-card-actions>
                  </v-card>

                  <!-- Summary -->
                  <div v-if="algorithmStore.facilitiesWithVehicles.length > 0" class="mt-6 pt-4 border-t">
                    <p class="text-caption font-weight-bold">Resumen de selección:</p>
                    <p class="text-caption">
                      {{ algorithmStore.facilitiesWithVehicles.length }} instalación(es) con 
                      {{ totalSelectedVehicles }} vehículo(s) seleccionado(s)
                    </p>

                    <!-- JSON Preview -->
                    <div class="mt-4">
                      <p class="text-caption font-weight-bold mb-2">JSON a enviar al backend:</p>
                      <v-card variant="outlined" class="bg-grey-lighten-5">
                        <v-card-text class="pa-3">
                          <pre class="text-caption" style="overflow-x: auto; font-size: 11px; line-height: 1.4;">{{ formattedCommandJson }}</pre>
                        </v-card-text>
                      </v-card>
                    </div>
                  </div>
                </v-card-text>
              </v-card>
            </v-stepper-window-item>

            <v-stepper-window-item :value="2">
              <v-card flat class="mt-4" :title="t('algorithm.execute.step2.title')">
                <v-card-actions>
                  <v-btn variant="outlined" @click="currentStep = 1">
                    {{ t('common.buttons.back') }}
                  </v-btn>
                  <v-spacer />
                  <v-btn variant="elevated" color="primary" @click="currentStep = 3">
                    {{ t('common.buttons.next') }}
                  </v-btn>
                </v-card-actions>
                <v-card-text>Contenido Paso 2</v-card-text>
              </v-card>
            </v-stepper-window-item>

            <v-stepper-window-item :value="3">
              <v-card flat class="mt-4" :title="t('algorithm.execute.step3.title')">
                <v-card-actions>
                  <v-btn variant="outlined" @click="currentStep = 2">
                    {{ t('common.buttons.back') }}
                  </v-btn>
                  <v-spacer />
                  <v-btn variant="elevated" color="success" @click="executeAlgorithm">
                    {{ t('algorithm.list.runButton') }}
                  </v-btn>
                </v-card-actions>
                <v-card-text>Contenido Paso 3</v-card-text>
              </v-card>
            </v-stepper-window-item>

          </v-stepper-window>
        </template>
      </v-stepper>
    </div>
  </CrudLayout>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { facilityStatusColor, facilityStatusToOptions } from '../../../../domain/enumerate/facility-status';
import { facilityTypeColor, facilityTypeToOptions } from '../../../../domain/enumerate/facility-type';
import { vehicleTypeColor, vehicleTypeToOptions } from '../../../../domain/enumerate/vehicle-type';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { useAlgorithmStore } from '../../stores/algorithm-store';
import { useFacilityStore } from '../../stores/facility-store';
import { useVehicleStore } from '../../stores/vehicle-store';

const { t } = useI18n();

// Get stores
const algorithmStore = useAlgorithmStore();
const facilityStore = useFacilityStore();
const vehicleStore = useVehicleStore();

// Get reactive data from stores
const { facilities: allFacilities, loading: facilityLoading, totalFacilities, currentPage, rowsPerPage } = storeToRefs(facilityStore);
const { vehicles: allVehicles } = storeToRefs(vehicleStore);

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

// Compute stepper items from translations
const stepperItems = computed(() => [
  t('algorithm.execute.step1.label'),
  t('algorithm.execute.step2.label'),
  t('algorithm.execute.step3.label'),
]);

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
  {
    title: t('facility.list.table.headers.status'),
    align: 'center' as const,
    sortable: true,
    key: 'status',
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
      rawFacilityType: facility.getFacilityType(),
      rawStatus: facility.getStatus(),
      type: t(`facility.add.facilityTypes.${facility.getFacilityType()}`),
      location: `${location.postalAddress} (${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)})`,
      storageCapacity: `${storageCapacity.toFixed(2)} kg`,
      processingCapacity: `${processingCapacity.toFixed(2)} kg/día`,
      unloadingTime: `${unloadingTime} min`,
      openingCost: `${openingCost.getAmount().toFixed(2)} ${openingCost.getCurrency().getCode()}`,
      status: t(`facility.add.statuses.${facility.getStatus()}`),
    };
  });
});

// Format vehicles for table display in dialog
const vehicleItems = computed(() => {
  return allVehicles.value.map((vehicle) => ({
    id: vehicle.getId().toString(),
    rawType: vehicle.getVehicleType(),
    type: t(`vehicle.add.vehicleTypes.${vehicle.getVehicleType()}`),
    capacityKilograms: vehicle.getCapacityKilograms().getKilograms(),
    capacityLiters: vehicle.getCapacityLiters().getLiters(),
    cost: `${vehicle.getCostPerKilometer().getAmount().toFixed(2)} ${vehicle.getCostPerKilometer().getCurrency().getCode()}`,
  }));
});

/**
 * Validate that step 1 has at least one facility with vehicles selected
 */
const isStep1Valid = computed(() => {
  return algorithmStore.facilitiesWithVehicles.length > 0 &&
         algorithmStore.facilitiesWithVehicles.some(f => f.selectedVehicleIds.length > 0);
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
 * Generate the command JSON that will be sent to the backend
 */
const formattedCommandJson = computed(() => {
  const command = {
    facilitiesWithVehicles: algorithmStore.facilitiesWithVehicles.map(f => ({
      facilityId: f.facilityId,
      selectedVehicleIds: f.selectedVehicleIds,
    })),
    selectedContainerIds: algorithmStore.selectedContainerIds,
    numberOfDays: algorithmStore.extraData.numberOfDays,
    averagePickupTimeMinutes: algorithmStore.extraData.averagePickupTimeMinutes,
  };
  
  return JSON.stringify(command, null, 2);
});

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
onMounted(async () => {
  await loadFacilities(currentPage.value, rowsPerPage.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
  
  // Load all vehicles
  await vehicleStore.getVehicles(0, 1000);
});

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
 * Handle location filter change
 */
const onLocationFilterChange = async (newLocation: string | null) => {
  const location = newLocation ?? undefined;
  await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value, selectedFacilityStatusFilter.value, location);
  tablePage.value = currentPage.value + 1;
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
 * Get the currently selected facility for vehicle selection
 */
const selectedFacilityForVehicles = computed(() => {
  if (!selectedFacilityForVehiclesId.value) return null;
  return facilityItems.value.find(f => f.id === selectedFacilityForVehiclesId.value);
});

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
 * Get vehicles for a specific facility
 */
const getVehiclesForFacility = (facilityId: string): any[] => {
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
  
  // Update the store with selected vehicles
  toggleVehicle(selectedFacilityForVehiclesId.value, ''); // Clear first - we'll rebuild
  
  // Find the facility in store
  const facilityIndex = algorithmStore.facilitiesWithVehicles.findIndex(
    f => f.facilityId === selectedFacilityForVehiclesId.value
  );
  
  if (facilityIndex !== -1) {
    algorithmStore.facilitiesWithVehicles[facilityIndex].selectedVehicleIds = [...tempSelectedVehicleIds.value];
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
    tempSelectedVehicleIds.value.splice(index, 1);
  } else {
    tempSelectedVehicleIds.value.push(vehicleId);
  }
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
  await algorithmStore.executeAlgorithm();
};

/**
 * Reset the form to initial state
 */
const resetForm = () => {
  algorithmStore.resetForm();
  currentStep.value = 1;
};
</script>