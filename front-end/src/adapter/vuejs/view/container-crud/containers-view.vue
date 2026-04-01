<template>
  <v-container fluid>
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

    <CrudLayout
      :title="t('container.list.title')"
      icon="mdi-delete"
    >
      <template #title-actions>
        <ButtonTooltip
          color="primary"
          icon="mdi-plus"
          size="default"
          variant="elevated"
          :eventclick="addContainer"
          :text="t('container.list.addButton')"
          :tooltip="t('container.list.addButton')"
          class="ml-2"
        />
      </template>

      <v-data-table-server
        :headers="headers"
        :items="containerItems"
        :loading="loading"
        :items-length="totalContainers"
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
        <template v-slot:item.wasteType="{ item }">
          <v-chip :color="wasteTypeColor(item.rawWasteType)" size="small">
            {{ item.wasteType }}
          </v-chip>
        </template>

        <template v-slot:item.location="{ item }">
          {{ item.location }}
        </template>

        <template v-slot:item.demand="{ item }">
          {{ item.demand }}
        </template>

        <template v-slot:item.serviceZone="{ item }">
          <v-chip
            v-if="item.rawServiceZone"
            :color="serviceZoneColor(item.rawServiceZone)"
            size="small"
            variant="tonal"
          >
            {{ item.serviceZone }}
          </v-chip>
          <span v-else>{{ item.serviceZone }}</span>
        </template>

        <template v-slot:item.actions="{ item }">
          <ButtonTooltip
            text=""
            icon="mdi-eye"
            :tooltip="t('container.list.table.tooltips.view')"
            color="info"
            size="small"
            variant="text"
            :eventclick="() => showItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-pencil"
            :tooltip="t('container.list.table.tooltips.edit')"
            color="success"
            size="small"
            variant="text"
            :eventclick="() => editItem(item.id)"
          />
          <ButtonTooltip
            text=""
            icon="mdi-delete"
            :tooltip="t('container.list.table.tooltips.delete')"
            color="error"
            size="small"
            variant="text"
            :eventclick="() => deleteItem(item.id)"
          />
        </template>

        <template v-slot:no-data>
          <v-alert type="info" variant="tonal" class="ma-4">
            {{ t('container.list.table.noData') }}
            <ButtonTooltip
              color="primary"
              variant="text"
              icon="mdi-plus"
              :eventclick="addContainer"
              class="ml-2"
              :text="t('container.list.table.addFirst')"
              :tooltip="t('container.list.table.addFirst')"
            />
          </v-alert>
        </template>
      </v-data-table-server>

      <template #toolbar-append>
        <div style="width: 250px;">
          <v-select
            v-model="selectedWasteTypeFilter"
            :items="wasteTypeFilterOptions"
            :placeholder="t('container.list.filterByWasteType')"
            item-title="title"
            item-value="value"
            clearable
            density="compact"
            hide-details
            variant="outlined"
            @update:model-value="onWasteTypeFilterChange"
          />
        </div>
      </template>
    </CrudLayout>

    <ErrorMessage
      :model-value="dialogDelete"
      :title="t('container.list.deleteDialog.title')"
      :error-message="t('container.list.deleteDialog.message')"
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
import CrudLayout from '../../components/common/CrudLayout.vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { serviceZoneColor } from '../../../../domain/enumerate/service-zone';
import { wasteTypeColor, wasteTypeToOptions } from '../../../../domain/enumerate/waste-type';
import router from '../../router/router';
import { useContainerStore } from '../../stores/container-store';

const { t } = useI18n();

const containerStore = useContainerStore();
const { containers, containerNotification, loading, totalContainers, currentPage, rowsPerPage } = storeToRefs(containerStore);

const dialogDelete = ref(false);
const selectedContainerId = ref('');
const selectedWasteTypeFilter = ref<string | undefined>(undefined);
const tablePage = ref(1);
const itemsPerPage = ref(10);
const currentSortBy = ref<string | undefined>(undefined);
const currentSortOrder = ref<'asc' | 'desc'>('asc');

const headers = computed(() => [
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
  {
    title: t('container.list.table.headers.actions'),
    align: 'center' as const,
    sortable: false,
    key: 'actions',
  },
]);

const wasteTypeFilterOptions = computed(() => wasteTypeToOptions(t));

const containerItems = computed(() => {
  return containers.value.map((container) => {
    const location = container.getLocation();
    const demand = container.getWasteDemand();
    const serviceZone = container.getServiceZone();

    return {
      id: container.getId().toString(),
      rawWasteType: container.getWasteType(),
      rawServiceZone: serviceZone,
      wasteType: t(`container.add.wasteTypes.${container.getWasteType()}`),
      location: `${location.postalAddress} (${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)})`,
      demand: `${demand.getValue()} ${demand.getQuantityUnit().getValue()}/${formatTimeUnitShort(demand.getTimeUnit())}`,
      serviceZone: serviceZone
        ? t(`container.add.serviceZones.${serviceZone}`)
        : t('container.list.notAssigned'),
    };
  });
});

onMounted(async () => {
  await loadContainers(currentPage.value, rowsPerPage.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
});

const loadContainers = async (
  page: number,
  size: number,
  sortBy?: string,
  sortOrder?: 'asc' | 'desc',
  wasteType?: string
) => {
  currentSortBy.value = sortBy;
  currentSortOrder.value = sortOrder ?? 'asc';
  await containerStore.getContainers(page, size, sortBy, sortOrder, wasteType);
};

const onWasteTypeFilterChange = async (newType: string | null) => {
  const wasteType = newType ?? undefined;
  await loadContainers(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, wasteType);
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

  await loadContainers(requestedPage, requestedSize, newSortBy, newSortOrder, selectedWasteTypeFilter.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
};

const formatTimeUnitShort = (unit: string): string => {
  return t(`common.timeUnitsLowercase.${unit}`);
};

const addContainer = () => {
  router.push({ name: 'AddContainer' });
};

const showItem = (itemId: string) => {
  router.push({ name: 'ShowContainer', params: { id: itemId } });
};

const editItem = (itemId: string) => {
  router.push({ name: 'EditContainer', params: { id: itemId } });
};

const deleteItem = (itemId: string) => {
  selectedContainerId.value = itemId;
  dialogDelete.value = true;
};

const confirmDelete = async () => {
  await containerStore.deleteContainer(selectedContainerId.value);
  await loadContainers(currentPage.value, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedWasteTypeFilter.value);
  if (containers.value.length === 0 && currentPage.value > 0) {
    await loadContainers(currentPage.value - 1, rowsPerPage.value, currentSortBy.value, currentSortOrder.value, selectedWasteTypeFilter.value);
  }
  tablePage.value = currentPage.value + 1;
  dialogDelete.value = false;
  selectedContainerId.value = '';
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
