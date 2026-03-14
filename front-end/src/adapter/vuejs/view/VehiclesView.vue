<!--
  VehiclesView.vue
  Main view component for listing all vehicles
  Displays a data table with CRUD operations (Create, Read, Update, Delete)
-->
<template>
  <v-container fluid>
    <!-- Page title -->
    <v-row justify="center">
      <v-col cols="12" md="10" lg="10">
        <h2 class="text-h4 mb-4 text-center">
          <v-icon class="mr-2">mdi-truck-multiple</v-icon>
          {{ t('vehicle.list.title') }}
        </h2>
      </v-col>
    </v-row>

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

    <!-- Main content area -->
    <v-row justify="center">
      <v-col cols="12" md="10" lg="10">
        <!-- Add vehicle button -->
        <v-row class="mb-4">
          <v-col cols="12" class="d-flex justify-start">
            <v-btn
              color="primary"
              prepend-icon="mdi-plus"
              size="large"
              @click="addVehicle"
            >
              {{ t('vehicle.list.addButton') }}
            </v-btn>
          </v-col>
        </v-row>

        <!-- Vehicles data table -->
        <v-card>
          <v-data-table
            :headers="headers"
            :items="vehicleItems"
            :loading="loading"
            :items-per-page="itemsPerPage"
            :items-per-page-options="[
              { value: 5, title: '5' },
              { value: 10, title: '10' },
              { value: 25, title: '25' },
              { value: 50, title: '50' },
              { value: -1, title: t('vehicle.list.table.allItems') }
            ]"
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
              <v-tooltip :text="t('vehicle.list.table.tooltips.view')" location="top">
                <template v-slot:activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon="mdi-eye"
                    size="small"
                    variant="text"
                    color="info"
                    @click="showItem(item.id)"
                  ></v-btn>
                </template>
              </v-tooltip>

              <v-tooltip :text="t('vehicle.list.table.tooltips.edit')" location="top">
                <template v-slot:activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon="mdi-pencil"
                    size="small"
                    variant="text"
                    color="success"
                    @click="editItem(item.id)"
                  ></v-btn>
                </template>
              </v-tooltip>

              <v-tooltip :text="t('vehicle.list.table.tooltips.delete')" location="top">
                <template v-slot:activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon="mdi-delete"
                    size="small"
                    variant="text"
                    color="error"
                    @click="deleteItem(item.id)"
                  ></v-btn>
                </template>
              </v-tooltip>
            </template>

            <!-- Empty state when no vehicles exist -->
            <template v-slot:no-data>
              <v-alert type="info" variant="tonal" class="ma-4">
                {{ t('vehicle.list.table.noData') }}
                <v-btn 
                  color="primary" 
                  variant="text" 
                  @click="addVehicle"
                  class="ml-2"
                >
                  {{ t('vehicle.list.table.addFirst') }}
                </v-btn>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Delete confirmation dialog -->
    <v-dialog v-model="dialogDelete" max-width="500">
      <v-card>
        <v-card-title class="text-h5 bg-error text-white">
          <v-icon class="mr-2">mdi-alert</v-icon>
          {{ t('vehicle.list.deleteDialog.title') }}
        </v-card-title>
        <v-card-text class="pt-4">
          <v-alert type="warning" variant="tonal">
            {{ t('vehicle.list.deleteDialog.message') }}
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="dialogDelete = false"
          >
            {{ t('vehicle.list.deleteDialog.cancel') }}
          </v-btn>
          <v-btn
            color="error"
            variant="elevated"
            @click="confirmDelete"
          >
            {{ t('vehicle.list.deleteDialog.confirm') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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

import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { VehicleType } from '../../../domain/enumerate/vehicle-type';
import router from '../router/router';
import { useVehicleStore } from '../stores/vehicle-store';

// Vue I18n composable for translations
const { t } = useI18n();

// Store instance and reactive references
const vehicleStore = useVehicleStore();
const { vehicles, vehicleNotification, loading } = storeToRefs(vehicleStore);

// Local component state
const dialogDelete = ref(false);
const selectedIdVehicle = ref('');
const itemsPerPage = ref(10);

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
  await loadVehicles();
});

/**
 * Fetch vehicles from the server via store
 */
const loadVehicles = async () => {
  await vehicleStore.getVehicles();
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
  return t(`vehicle.list.timeUnits.${unit}`);
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
  await loadVehicles();
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
