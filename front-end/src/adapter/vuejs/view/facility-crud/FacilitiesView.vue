<template>
  <v-container fluid>
    <v-snackbar
      v-model="facilityNotification.flag"
      :color="facilityNotification.color"
      :timeout="facilityNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ facilityNotification.icon }}</v-icon>
      <strong>{{ facilityNotification.title }}</strong>
      <p class="mb-0">{{ facilityNotification.msg }}</p>
    </v-snackbar>

    <CrudLayout
      :title="listTitleWithTotal"
      icon="mdi-factory"
    >
      <template #title-actions>
        <ButtonTooltip
          color="primary"
          icon="mdi-plus"
          size="default"
          variant="elevated"
          :eventclick="addFacility"
          :text="t('facility.list.addButton')"
          :tooltip="t('facility.list.addButton')"
        />
        <BulkImportFileButton
          :text="t('facility.list.importButton')"
          :tooltip="t('facility.list.importButton')"
          :importing="loading"
          @import="onImportFacilitiesFile"
        />
      </template>

      <div class="list-with-map-layout">
        <div class="list-with-map-layout__main">
          <v-data-table-server
            :headers="headers"
            :items="facilityItems"
            :loading="loading"
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
        <template v-slot:item.type="{ item }">
          <v-chip :color="facilityTypeColor(item.rawFacilityType)" size="small">
            {{ item.type }}
          </v-chip>
        </template>

        <template v-slot:item.location="{ item }">
          {{ item.location }}
        </template>

        <template v-slot:item.storageCapacity="{ item }">
          {{ item.storageCapacity }}
        </template>

        <template v-slot:item.processingCapacity="{ item }">
          {{ item.processingCapacity }}
        </template>

        <template v-slot:item.unloadingTime="{ item }">
          {{ item.unloadingTime }}
        </template>

        <template v-slot:item.openingCost="{ item }">
          {{ item.openingCost }}
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip
            :color="facilityStatusColor(item.rawStatus)"
            size="small"
            variant="tonal"
          >
            {{ item.status }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <ButtonTooltip
            text=""
            icon="mdi-eye"
            :tooltip="t('facility.list.table.tooltips.view')"
            color="info"
            size="small"
            variant="text"
            :eventclick="() => showItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-pencil"
            :tooltip="t('facility.list.table.tooltips.edit')"
            color="success"
            size="small"
            variant="text"
            :eventclick="() => editItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-delete"
            :tooltip="t('facility.list.table.tooltips.delete')"
            color="error"
            size="small"
            variant="text"
            :eventclick="() => deleteItem(item.id)"
          />
        </template>

        <template v-slot:no-data>
          <v-alert type="info" variant="tonal" class="ma-4">
            {{ t('facility.list.table.noData') }}
            <ButtonTooltip
              color="primary"
              variant="text"
              icon="mdi-plus"
              :eventclick="addFacility"
              class="ml-2"
              :text="t('facility.list.table.addFirst')"
              :tooltip="t('facility.list.table.addFirst')"
            />
          </v-alert>
        </template>
          </v-data-table-server>
        </div>

        <div class="list-with-map-layout__map">
          <LocationsListMap
            :locations="facilityMapPins"
            detail-route-name="EditFacility"
          />
          <EntityTypeStatisticsChart
            class="list-with-map-layout__chart"
            :statistics="facilityStatistics"
            :chart-id="12"
            :chart-title="t('facility.list.statistics.byType')"
            :empty-message="t('facility.list.statistics.noData')"
            :translate-type="translateFacilityTypeKey"
            :chart-width="280"
            :chart-height="240"
          />
        </div>
      </div>

      <template #toolbar-append>
        <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center; padding: 8px;">
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
      </template>
    </CrudLayout>

    <ErrorMessage
      :model-value="dialogDelete"
      :title="t('facility.list.deleteDialog.title')"
      :error-message="t('facility.list.deleteDialog.message')"
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
import { ButtonTooltip, ErrorMessage } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { facilityStatusColor, facilityStatusLocaleKey, facilityStatusToOptions } from '../../../../domain/enumerate/facility-status';
import { facilityTypeColor, facilityTypeLocaleKey, facilityTypeToOptions } from '../../../../domain/enumerate/facility-type';
import BulkImportFileButton from '../../components/common/BulkImportFileButton.vue';
import EntityTypeStatisticsChart from '../../components/common/EntityTypeStatisticsChart.vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import LocationsListMap from '../../components/common/LocationsListMap.vue';
import router from '../../router/router';
import { useFacilityStore } from '../../stores/facility-store';

const { t } = useI18n();

const facilityStore = useFacilityStore();
const { facilities, facilityNotification, loading, totalFacilities, facilityStatistics, currentPage, rowsPerPage } = storeToRefs(facilityStore);

const listTitleWithTotal = computed(() => {
  const total = facilityStatistics.value?.total;
  const base = t('facility.list.title');
  return total != null ? `${base} (${total})` : base;
});

const translateFacilityTypeKey = (typeKey: string): string => {
  const key = `facility.add.facilityTypes.${facilityTypeLocaleKey(typeKey)}`;
  const translated = t(key);
  return translated === key ? typeKey : translated;
};

const dialogDelete = ref(false);
const selectedFacilityId = ref('');
const selectedFacilityTypeFilter = ref<string | undefined>(undefined);
const selectedFacilityStatusFilter = ref<string | undefined>(undefined);
const selectedLocationFilter = ref<string | undefined>(undefined);
const tablePage = ref(1);
const itemsPerPage = ref(10);
const currentSortBy = ref<string | undefined>(undefined);
const currentSortOrder = ref<'asc' | 'desc'>('asc');

const headers = computed(() => [
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
  {
    title: t('facility.list.table.headers.status'),
    align: 'center' as const,
    sortable: true,
    key: 'status',
  },
  {
    title: t('facility.list.table.headers.actions'),
    align: 'center' as const,
    sortable: false,
    key: 'actions',
  },
]);

const facilityTypeFilterOptions = computed(() => facilityTypeToOptions(t));

const facilityStatusFilterOptions = computed(() => facilityStatusToOptions(t));

const facilityMapPins = computed(() =>
  facilities.value.map((facility) => {
    const location = facility.getLocation();
    return {
      id: facility.getId().toString(),
      latitude: location.latitude,
      longitude: location.longitude,
      label: facility.getName().getValue(),
    };
  }),
);

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
      type: t(`facility.add.facilityTypes.${facilityTypeLocaleKey(facility.getFacilityType())}`),
      location: location.postalAddress,
      storageCapacity: storageCapacity.toFixed(2),
      processingCapacity: processingCapacity.toFixed(2),
      unloadingTime,
      openingCost: openingCost.getAmount().toFixed(2),
      status: t(`facility.add.statuses.${facilityStatusLocaleKey(facility.getStatus())}`),
    };
  });
});

onMounted(async () => {
  await loadFacilities(currentPage.value, rowsPerPage.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
});

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

const onFacilityTypeFilterChange = async (newType: string | null) => {
  const facilityType = newType ?? undefined;
  await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, facilityType, selectedFacilityStatusFilter.value, selectedLocationFilter.value);
  tablePage.value = currentPage.value + 1;
};

const onFacilityStatusFilterChange = async (newStatus: string | null) => {
  const status = newStatus ?? undefined;
  await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value, status, selectedLocationFilter.value);
  tablePage.value = currentPage.value + 1;
};

const onLocationFilterChange = async (newLocation: string | null) => {
  const location = newLocation ?? undefined;
  await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value, selectedFacilityStatusFilter.value, location);
  tablePage.value = currentPage.value + 1;
};

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

const addFacility = () => {
  router.push({ name: 'AddFacility' });
};

const onImportFacilitiesFile = async (file: File) => {
  const result = await facilityStore.importFacilitiesFromFile(file);
  if (!result) {
    return;
  }

  if (result.success) {
    facilityStore.setNotification(
      t('common.bulkImport.successTitle'),
      t('common.bulkImport.successMessage', {
        created: result.createdCount,
        total: result.totalRequested,
      }),
      'mdi-check',
      'success',
    );
  } else {
    facilityStore.setNotification(
      t('common.bulkImport.partialTitle'),
      t('common.bulkImport.partialMessage', {
        created: result.createdCount,
        total: result.totalRequested,
        failed: result.failedCount,
      }),
      'mdi-alert',
      'warning',
    );
  }

  await loadFacilities(
    currentPage.value,
    rowsPerPage.value,
    currentSortBy.value,
    currentSortOrder.value,
    selectedFacilityTypeFilter.value,
    selectedFacilityStatusFilter.value,
    selectedLocationFilter.value,
  );
  tablePage.value = currentPage.value + 1;
};

const showItem = (itemId: string) => {
  router.push({ name: 'ShowFacility', params: { id: itemId } });
};

const editItem = (itemId: string) => {
  router.push({ name: 'EditFacility', params: { id: itemId } });
};

const deleteItem = (itemId: string) => {
  selectedFacilityId.value = itemId;
  dialogDelete.value = true;
};

const confirmDelete = async () => {
  await facilityStore.deleteFacility(selectedFacilityId.value);
  await loadFacilities(currentPage.value, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value);
  if (facilities.value.length === 0 && currentPage.value > 0) {
    await loadFacilities(currentPage.value - 1, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedFacilityTypeFilter.value);
  }
  tablePage.value = currentPage.value + 1;
  dialogDelete.value = false;
  selectedFacilityId.value = '';
};
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}

.v-data-table {
  border-radius: 8px;
}

.list-with-map-layout {
  display: flex;
  flex-direction: row;
  gap: 24px;
  align-items: flex-start;
}

.list-with-map-layout__main {
  flex: 2 1 0;
  min-width: 0;
}

.list-with-map-layout__map {
  flex: 1 1 0;
  min-width: 0;
  position: sticky;
  top: 12px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.list-with-map-layout__chart {
  margin-bottom: 0;
}

.list-with-map-layout__map :deep(.locations-list-map),
.list-with-map-layout__map :deep(.locations-list-map--empty) {
  margin-top: 0;
}

.list-with-map-layout__map :deep(.locations-list-map__map) {
  height: min(480px, 55vh);
}

@media (max-width: 960px) {
  .list-with-map-layout {
    flex-direction: column;
  }

  .list-with-map-layout__main {
    width: 100%;
  }

  .list-with-map-layout__map {
    flex: 1 1 auto;
    width: 100%;
    position: static;
    max-width: none;
  }

  .list-with-map-layout__map :deep(.locations-list-map__map) {
    height: 280px;
  }
}
</style>
