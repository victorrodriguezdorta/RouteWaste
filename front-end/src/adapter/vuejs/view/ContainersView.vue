<!--
  ContainersView.vue
  Main view component for listing all containers
  Displays a data table with creation and delete operations
-->
<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="12" md="10" lg="10">
        <h2 class="text-h4 mb-4 text-center">
          <v-icon class="mr-2">mdi-delete-empty</v-icon>
          {{ t('container.list.title') }}
        </h2>
      </v-col>
    </v-row>

    <v-snackbar
      v-model="containerNotification.flag"
      :color="containerNotification.color"
      :timeout="containerNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ containerNotification.icon }}</v-icon>
      <strong>{{ containerNotification.title }}</strong>
      <p class="mb-0">{{ containerNotification.msg }}</p>
    </v-snackbar>

    <v-row justify="center">
      <v-col cols="12" md="10" lg="10">
        <v-row class="mb-4" align="center">
          <v-col cols="12" sm="auto">
            <v-btn
              color="primary"
              prepend-icon="mdi-plus"
              size="large"
              @click="addContainer"
            >
              {{ t('container.list.addButton') }}
            </v-btn>
          </v-col>
          <v-spacer />
          <v-col cols="12" sm="4" md="3">
            <v-select
              v-model="selectedWasteTypeFilter"
              :items="wasteTypeFilterOptions"
              :label="t('container.list.filterByWasteType')"
              item-title="title"
              item-value="value"
              clearable
              density="compact"
              hide-details
              @update:model-value="onWasteTypeFilterChange"
            />
          </v-col>
        </v-row>

        <v-card>
          <v-data-table
            :headers="headers"
            :items="containerItems"
            :loading="loading"
            item-value="id"
            hover
            class="elevation-2"
          >
            <template v-slot:item.wasteType="{ item }">
              <v-chip color="green" size="small">
                {{ item.wasteType }}
              </v-chip>
            </template>

            <template v-slot:item.actions="{ item }">
              <v-tooltip :text="t('container.list.table.tooltips.delete')" location="top">
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

            <template v-slot:no-data>
              <v-alert type="info" variant="tonal" class="ma-4">
                {{ t('container.list.table.noData') }}
                <v-btn
                  color="primary"
                  variant="text"
                  @click="addContainer"
                  class="ml-2"
                >
                  {{ t('container.list.table.addFirst') }}
                </v-btn>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="dialogDelete" max-width="500">
      <v-card>
        <v-card-title class="text-h5 bg-error text-white">
          <v-icon class="mr-2">mdi-alert</v-icon>
          {{ t('container.list.deleteDialog.title') }}
        </v-card-title>
        <v-card-text class="pt-4">
          <v-alert type="warning" variant="tonal">
            {{ t('container.list.deleteDialog.message') }}
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="dialogDelete = false"
          >
            {{ t('container.list.deleteDialog.cancel') }}
          </v-btn>
          <v-btn
            color="error"
            variant="elevated"
            @click="confirmDelete"
          >
            {{ t('container.list.deleteDialog.confirm') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { WasteType } from '../../../domain/enumerate/waste-type';
import router from '../router/router';
import { useContainerStore } from '../stores/container-store';

const { t } = useI18n();

const containerStore = useContainerStore();
const { containers, containerNotification, loading } = storeToRefs(containerStore);

const dialogDelete = ref(false);
const selectedIdContainer = ref('');
const selectedWasteTypeFilter = ref<string | undefined>(undefined);

const headers = [
  {
    title: t('container.list.table.headers.wasteType'),
    align: 'start' as const,
    sortable: true,
    key: 'wasteType',
  },
  {
    title: t('container.list.table.headers.wasteDemand'),
    align: 'center' as const,
    sortable: true,
    key: 'wasteDemand',
  },
  {
    title: t('container.list.table.headers.serviceZone'),
    align: 'center' as const,
    sortable: true,
    key: 'serviceZone',
  },
  {
    title: t('container.list.table.headers.location'),
    align: 'center' as const,
    sortable: true,
    key: 'location',
  },
  {
    title: t('container.list.table.headers.actions'),
    align: 'center' as const,
    sortable: false,
    key: 'actions',
  },
];

/**
 * Options for the waste type filter select
 */
const wasteTypeFilterOptions = computed(() =>
  Object.values(WasteType).map(type => ({
    value: type as string,
    title: t(`container.add.wasteTypes.${type}`),
  }))
);

const containerItems = computed(() => {
  let filteredContainers = containers.value;

  // Apply waste type filter if selected
  if (selectedWasteTypeFilter.value) {
    filteredContainers = filteredContainers.filter(
      (container) => container.getWasteType() === selectedWasteTypeFilter.value
    );
  }

  return filteredContainers.map((container) => ({
    id: container.getId().toString(),
    wasteType: formatWasteType(container.getWasteType()),
    wasteDemand: `${container.getWasteDemand().getValue()} ${container.getWasteDemand().getQuantityUnit().getValue()}/${formatTimeUnitShort(container.getWasteDemand().getTimeUnit())}`,
    serviceZone: formatServiceZone(container.getServiceZone()),
    location: container.getLocation().postalAddress,
  }));
});

onMounted(async () => {
  await loadContainers();
});

const loadContainers = async () => {
  await containerStore.getContainers();
};

const formatWasteType = (wasteType: string): string => {
  return t(`container.add.wasteTypes.${wasteType}`);
};

const formatServiceZone = (serviceZone: string | null): string => {
  if (!serviceZone) {
    return t('container.list.table.notAssigned');
  }
  return t(`container.add.serviceZones.${serviceZone}`);
};

/**
 * Format time unit to short display string using i18n
 * @param unit - Time unit string (e.g., 'DAY', 'WEEK')
 * @returns Short formatted time unit
 */
const formatTimeUnitShort = (unit: string): string => {
  return t(`container.list.timeUnits.${unit}`);
};

/**
 * Handle waste type filter changes — updates the displayed items.
 */
const onWasteTypeFilterChange = (newType: string | null) => {
  selectedWasteTypeFilter.value = newType ?? undefined;
};

const addContainer = () => {
  router.push({ name: 'AddContainer' });
};

const deleteItem = (itemId: string) => {
  selectedIdContainer.value = itemId;
  dialogDelete.value = true;
};

const confirmDelete = async () => {
  await containerStore.deleteContainer(selectedIdContainer.value);
  dialogDelete.value = false;
  selectedIdContainer.value = '';
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
