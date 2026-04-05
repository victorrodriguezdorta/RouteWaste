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
      :title="t('facility.list.title')"
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
          class="ml-2"
        />
      </template>

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

        <template v-slot:item.capacity="{ item }">
          {{ item.capacity }}
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

      <template #toolbar-append>
        <div style="width: 250px;">
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
import { facilityStatusColor } from '../../../../domain/enumerate/facility-status';
import { facilityTypeColor, facilityTypeToOptions } from '../../../../domain/enumerate/facility-type';
import CrudLayout from '../../components/common/CrudLayout.vue';
import router from '../../router/router';
import { useFacilityStore } from '../../stores/facility-store';

const { t } = useI18n();

const facilityStore = useFacilityStore();
const { facilities, facilityNotification, loading, totalFacilities, currentPage, rowsPerPage } = storeToRefs(facilityStore);

const dialogDelete = ref(false);
const selectedFacilityId = ref('');
const selectedFacilityTypeFilter = ref<string | undefined>(undefined);
const tablePage = ref(1);
const itemsPerPage = ref(10);
const currentSortBy = ref<string | undefined>(undefined);
const currentSortOrder = ref<'asc' | 'desc'>('asc');

const headers = computed(() => [
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
    title: t('facility.list.table.headers.capacity'),
    align: 'center' as const,
    sortable: true,
    key: 'capacity',
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

const facilityItems = computed(() => {
  return facilities.value.map((facility) => {
    const location = facility.getLocation();
    const capacity = facility.getCapacity();

    return {
      id: facility.getId().toString(),
      rawFacilityType: facility.getFacilityType(),
      rawStatus: facility.getStatus(),
      type: t(`facility.add.facilityTypes.${facility.getFacilityType()}`),
      location: `${location.postalAddress} (${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)})`,
      capacity: `${capacity.getValue()} ${capacity.getQuantityUnit().getValue()}/${formatTimeUnitShort(capacity.getTimeUnit())}`,
      status: t(`facility.add.statuses.${facility.getStatus()}`),
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
  facilityType?: string
) => {
  currentSortBy.value = sortBy;
  currentSortOrder.value = sortOrder ?? 'asc';
  await facilityStore.getFacilities(page, size, sortBy, sortOrder, facilityType);
};

const onFacilityTypeFilterChange = async (newType: string | null) => {
  const facilityType = newType ?? undefined;
  await loadFacilities(0, itemsPerPage.value, currentSortBy.value, currentSortOrder.value, facilityType);
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

  await loadFacilities(requestedPage, requestedSize, newSortBy, newSortOrder, selectedFacilityTypeFilter.value);
  tablePage.value = currentPage.value + 1;
  itemsPerPage.value = rowsPerPage.value;
};

const formatTimeUnitShort = (unit: string): string => {
  return t(`common.timeUnitsLowercase.${unit}`);
};

const addFacility = () => {
  router.push({ name: 'AddFacility' });
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
</style>
