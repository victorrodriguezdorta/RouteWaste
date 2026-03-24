<!--
  VehiclesView.vue
  Main view component for listing all vehicles
  Displays a data table with CRUD operations (Create, Read, Update, Delete)
-->
<template>
  <v-container fluid>
    <!-- Notification snackbar for user feedback -->
    <v-snackbar
      v-model="vehicleNotification.flag"
      :color="vehicleNotification.color"
      :timeout="vehicleNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ vehicleNotification.icon }}</v-icon>
      <strong>{{ vehicleNotification.title }}</strong>
      <p class="mb-0">{{ vehicleNotification.msg }}</p>
    </v-snackbar>

    <CrudLayout
      :title="t('vehicle.list.title')"
      icon="mdi-truck-multiple"
    >
      <!-- Add vehicle button and type filter -->
      <v-row class="mb-4" align="center">
        <v-spacer />
        <v-col cols="12" sm="4" md="3">
          <v-select
            v-model="selectedVehicleTypeFilter"
            :items="vehicleTypeFilterOptions"
            :label="t('vehicle.list.filterByType')"
            item-title="title"
            item-value="value"
            clearable
            density="compact"
            hide-details
            @update:model-value="onVehicleTypeFilterChange"
          />
        </v-col>
      </v-row>

      <!-- Vehicles data table -->
      <v-data-table-server
        :headers="headers"
        :items="vehicleItems"
        :loading="loading"
        :items-length="totalVehicles"
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
        <!-- Vehicle type column (with colored chip) -->
        <template v-slot:item.type="{ item }">
          <v-chip :color="getVehicleTypeColor(item.type)" size="small">
            {{ item.type }}
          </v-chip>
        </template>

        <!-- Capacity column -->
        <template v-slot:item.capacity="{ item }">
          {{ item.capacity }}
        </template>

        <!-- Cost column -->
        <template v-slot:item.cost="{ item }">
          {{ item.cost }}
        </template>

        <!-- Actions column (View, Edit, Delete buttons) -->
        <template v-slot:item.actions="{ item }">
          <ButtonTooltip
            text=""
            icon="mdi-eye"
            :tooltip="t('vehicle.list.table.tooltips.view')"
            color="info"
            size="small"
            variant="text"
            @click="showItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-pencil"
            :tooltip="t('vehicle.list.table.tooltips.edit')"
            color="success"
            size="small"
            variant="text"
            @click="editItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-delete"
            :tooltip="t('vehicle.list.table.tooltips.delete')"
            color="error"
            size="small"
            variant="text"
            @click="deleteItem(item.id)"
          />
        </template>

        <!-- Empty state when no vehicles exist -->
        <template v-slot:no-data>
          <v-alert type="info" variant="tonal" class="ma-4">
            {{ t('vehicle.list.table.noData') }}
            <ButtonTooltip 
              color="primary" 
              variant="text" 
              icon="mdi-plus"
              @click="addVehicle"
              class="ml-2"
              :text="t('vehicle.list.table.addFirst')"
              :tooltip="t('vehicle.list.table.addFirst')"
            />
          </v-alert>
        </template>
      </v-data-table-server>

      <template #toolbar-append>
        <ButtonTooltip
          color="white"
          icon="mdi-plus"
          size="small"
          variant="outlined"
          @click="addVehicle"
          :text="t('vehicle.list.addButton')"
          :tooltip="t('vehicle.list.addButton')"
          class="ml-2 mr-4"
        />
      </template>
    </CrudLayout>

    <!-- Delete confirmation dialog -->
    <ErrorMessage
      :model-value="dialogDelete"
      :title="t('vehicle.list.deleteDialog.title')"
      :error-message="t('vehicle.list.deleteDialog.message')"
      :reason="''"
      :cancel-text="t('common.buttons.cancel')"
      :retry-text="t('common.buttons.delete')"
      @update:model-value="dialogDelete = $event"
      @cancel="dialogDelete = false"
      @retry="confirmDelete"
    />
  </v-container>
</template>

<script lang="ts" setup>
/**
 * VehiclesView Component
 * 
 * Composition API component for displaying all vehicles in a table.
 * Provides CRUD operations: Create, Read, Update, Delete.
 * Uses Vuetify data table with pagination and action buttons.
 */

import { ButtonTooltip, ErrorMessage } from '@ull-tfg/ull-tfg-vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { VehicleType } from '../../../../domain/enumerate/vehicle-type';
import router from '../../router/router';
import { useVehicleStore } from '../../stores/vehicle-store';

// Vue I18n composable for translations
const { t } = useI18n();

// Store instance and reactive references
const vehicleStore = useVehicleStore();
const { vehicles, vehicleNotification, loading, totalVehicles, currentPage, rowsPerPage } = storeToRefs(vehicleStore);

// Local component state
const dialogDelete = ref(false);
const selectedIdVehicle = ref('');
const tablePage = ref(1);
const itemsPerPage = ref(10);
const selectedVehicleTypeFilter = ref<string | undefined>(undefined);
const currentSortBy = ref<string | undefined>(undefined);
const currentSortOrder = ref<'asc' | 'desc'>('asc');

// Table column headers configuration
const headers = [
  {
    title: t('vehicle.list.table.headers.type'),
    align: 'start' as const,
    sortable: true,
    key: 'type',
  },
  {
    title: t('vehicle.list.table.headers.capacity'),
    align: 'center' as const,
    sortable: true,
    key: 'capacity',
  },
  {
    title: t('vehicle.list.table.headers.cost'),
    align: 'center' as const,
    sortable: true,
    key: 'cost',
  },
  {
    title: t('vehicle.list.table.headers.actions'),
    align: 'center' as const,
    sortable: false,
    key: 'actions',
  },
];

/**
 * Options for the vehicle type filter select
 */
const vehicleTypeFilterOptions = computed(() =>
  Object.values(VehicleType).map(type => ({
    value: type as string,
    title: t(`vehicle.add.vehicleTypes.${type}`),
  }))
);

/**
 * Computed table items with formatted vehicle data
 * Transforms domain entities into display-friendly objects
 * @returns Array of formatted vehicle objects for the table
 */
const vehicleItems = computed(() => {
  return vehicles.value.map((vehicle) => ({
    id: vehicle.getId().toString(),
    type: formatVehicleType(vehicle.getVehicleType()),
    capacity: `${vehicle.getTransportCapacity().getValue()} ${vehicle.getTransportCapacity().getQuantityUnit().getValue()}/${formatTimeUnitShort(vehicle.getTransportCapacity().getTimeUnit())}`,
    cost: `${vehicle.getCostPerKilometer().getAmount().toFixed(2)} ${vehicle.getCostPerKilometer().getCurrency().getCode()}`,
  }));
});

/**
 * Load vehicles when component mounts
 */
onMounted(async () => {
  await loadVehicles(currentPage.value, rowsPerPage.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
});

/**
 * Fetch vehicles from the server via store
 */
const loadVehicles = async (page: number, size: number, sortBy?: string, sortOrder?: 'asc' | 'desc', vehicleType?: string) => {
  currentSortBy.value = sortBy;
  currentSortOrder.value = sortOrder ?? 'asc';
  await vehicleStore.getVehicles(page, size, sortBy, sortOrder, vehicleType);
};

/**
 * Handle pagination and sort changes coming from Vuetify table.
 * Vuetify pages are 1-based, backend pages are 0-based.
 */
const onTableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {
  const requestedSize = options.itemsPerPage;
  if (requestedSize <= 0) {
    return;
  }

  const requestedPage = Math.max(options.page - 1, 0);
  const newSortBy = options.sortBy[0]?.key;
  const newSortOrder = options.sortBy[0]?.order ?? 'asc';

  await loadVehicles(requestedPage, requestedSize, newSortBy, newSortOrder, selectedVehicleTypeFilter.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
};

/**
 * Handle vehicle type filter changes — resets to first page.
 */
const onVehicleTypeFilterChange = async (newType: string | null) => {
  const vehicleType = newType ?? undefined;
  await loadVehicles(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, vehicleType);
  tablePage.value = currentPage.value + 1;
};

/**
 * Format vehicle type enum to display string using i18n
 * @param type - VehicleType enum value
 * @returns Formatted vehicle type label
 */
const formatVehicleType = (type: VehicleType): string => {
  return t(`vehicle.add.vehicleTypes.${type}`);
};

/**
 * Format time unit to short display string using i18n
 * @param unit - Time unit string (e.g., 'DAY', 'WEEK')
 * @returns Short formatted time unit
 */
const formatTimeUnitShort = (unit: string): string => {
  return t(`common.timeUnitsLowercase.${unit}`);
};

/**
 * Get color for vehicle type chip based on type
 * @param type - Formatted vehicle type string
 * @returns Color name for the chip
 */
const getVehicleTypeColor = (type: string): string => {
  // Match by checking if the translated string contains key words
  const lowerType = type.toLowerCase();
  if (lowerType.includes('collection') || lowerType.includes('recolección')) return 'blue';
  if (lowerType.includes('transfer') || lowerType.includes('transferencia')) return 'green';
  if (lowerType.includes('support') || lowerType.includes('soporte')) return 'orange';
  return 'grey';
};

/**
 * Navigate to add vehicle view
 */
const addVehicle = () => {
  router.push({ name: 'AddVehicle' });
};

/**
 * Navigate to vehicle details view
 * @param itemId - Vehicle UUID
 */
const showItem = (itemId: string) => {
  router.push({ name: 'ShowVehicle', params: { id: itemId } });
};

/**
 * Navigate to vehicle edit view
 * @param itemId - Vehicle UUID
 */
const editItem = (itemId: string) => {
  router.push({ name: 'EditVehicle', params: { id: itemId } });
};

/**
 * Open delete confirmation dialog
 * @param itemId - Vehicle UUID to be deleted
 */
const deleteItem = (itemId: string) => {
  selectedIdVehicle.value = itemId;
  dialogDelete.value = true;
};

/**
 * Confirm and execute vehicle deletion
 * Reloads vehicle list after successful deletion
 */
const confirmDelete = async () => {
  await vehicleStore.deleteVehicle(selectedIdVehicle.value);

  await loadVehicles(currentPage.value, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedVehicleTypeFilter.value);
  if (vehicles.value.length === 0 && currentPage.value > 0) {
    await loadVehicles(currentPage.value - 1, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedVehicleTypeFilter.value);
  }

  tablePage.value = currentPage.value + 1;
  dialogDelete.value = false;
  selectedIdVehicle.value = '';
};
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}

.v-data-table {
  border-radius: 8px;
}
</style>
