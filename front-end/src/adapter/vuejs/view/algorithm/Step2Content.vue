<template>
  <v-card flat class="mt-4" :title="t('algorithm.execute.step2.title')">
    <v-card-actions>
      <v-btn variant="outlined" @click="emit('back')">
        {{ t('common.buttons.back') }}
      </v-btn>
      <v-spacer />
      <v-btn variant="elevated" color="primary" @click="emit('next')" :disabled="!isStep2Valid">
        {{ t('common.buttons.next') }}
      </v-btn>
    </v-card-actions>
    <v-card-text>
      <!-- Filtros -->
      <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center; padding: 8px; margin-bottom: 16px;">
        <!-- Filter by Waste Type -->
        <div style="width: 180px;">
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

        <!-- Filter by Service Zone -->
        <div style="width: 180px;">
          <v-select
            v-model="selectedServiceZoneFilter"
            :items="serviceZoneFilterOptions"
            :placeholder="t('container.list.filterByServiceZone')"
            item-title="title"
            item-value="value"
            clearable
            density="compact"
            hide-details
            variant="outlined"
            @update:model-value="onServiceZoneFilterChange"
          />
        </div>

        <!-- Filter by Location -->
        <div style="width: 180px;">
          <v-text-field
            v-model="selectedLocationFilter"
            :placeholder="t('container.list.filterByLocation')"
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
        :headers="step2Headers"
        :items="step2ContainerItems"
        :loading="containerLoading"
        :items-length="totalContainers"
        v-model:page="step2TablePage"
        :items-per-page="step2ItemsPerPage"
        :items-per-page-options="[
          { value: 5, title: '5' },
          { value: 10, title: '10' },
          { value: 25, title: '25' },
          { value: 50, title: '50' }
        ]"
        @update:options="onStep2TableOptionsUpdate"
        item-value="id"
        hover
        class="elevation-2"
      >
        <!-- Checkbox column -->
        <template v-slot:item.select="{ item }">
          <v-checkbox
            :model-value="isContainerSelected(item.id)"
            @update:model-value="toggleContainer(item.id)"
            class="mt-0"
          />
        </template>

        <!-- Waste Type column -->
        <template v-slot:item.wasteType="{ item }">
          <v-chip :color="wasteTypeColor(item.rawWasteType)" size="small">
            {{ item.wasteType }}
          </v-chip>
        </template>

        <!-- Location column -->
        <template v-slot:item.location="{ item }">
          {{ item.location }}
        </template>

        <!-- Capacity column -->
        <template v-slot:item.capacityLiters="{ item }">
          {{ item.capacityLiters }}
        </template>

        <!-- Demand column -->
        <template v-slot:item.demand="{ item }">
          {{ item.demand }}
        </template>

        <!-- Service Zone column -->
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

        <!-- No data template -->
        <template v-slot:no-data>
          <v-alert type="info" variant="tonal" class="ma-4">
            {{ t('container.list.table.noData') }}
          </v-alert>
        </template>
      </v-data-table-server>

      <!-- Summary -->
      <div v-if="algorithmStore.selectedContainerIds.length > 0" class="mt-6 pt-4 border-t">
        <p class="text-caption font-weight-bold">{{ t('algorithm.execute.step2.summary') }}</p>
        <p class="text-caption">
          {{ t('algorithm.execute.step2.selectedCount', {
            count: algorithmStore.selectedContainerIds.length
          }) }}
        </p>

        <!-- JSON Preview -->
        <div class="mt-4">
          <p class="text-caption font-weight-bold mb-2">{{ t('algorithm.execute.step2.jsonPreview') }}</p>
          <v-card variant="outlined" class="bg-grey-lighten-5">
            <v-card-text class="pa-3">
              <pre class="text-caption" style="overflow-x: auto; font-size: 11px; line-height: 1.4;">{{ formattedCommandJson }}</pre>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { serviceZoneColor } from '../../../../domain/enumerate/service-zone';
import { wasteTypeColor } from '../../../../domain/enumerate/waste-type';
import { useAlgorithmExecution } from '../../composables/useAlgorithmExecution';

const { t } = useI18n();

const emit = defineEmits<{
  back: [];
  next: [];
}>();

const {
  // State - Step 2
  step2TablePage,
  step2ItemsPerPage,
  selectedWasteTypeFilter,
  selectedServiceZoneFilter,
  selectedLocationFilter,
  containerLoading,
  totalContainers,

  // Computed - Step 2
  step2Headers,
  wasteTypeFilterOptions,
  serviceZoneFilterOptions,
  step2ContainerItems,
  formattedCommandJson,
  isStep2Valid,

  // Store reference
  algorithmStore,

  // Methods - Step 2
  onWasteTypeFilterChange,
  onServiceZoneFilterChange,
  onLocationFilterChange: onLocationFilterChangeContainer,
  onStep2TableOptionsUpdate,
  isContainerSelected,
  toggleContainer,
  initializeContainers,
} = useAlgorithmExecution();

// Initialize on component mount
initializeContainers();

// Handler wrapper for location filter (Step 2 version)
const onLocationFilterChange = (newValue: string | null) => {
  onLocationFilterChangeContainer(newValue);
};
</script>
