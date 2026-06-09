<template>
  <v-card flat>
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
            v-model="selectedContainerLocationFilter"
            :placeholder="t('container.list.filterByLocation')"
            clearable
            density="compact"
            hide-details
            variant="outlined"
            @update:model-value="onLocationFilterChange"
          />
        </div>
      </div>

      <!-- Tabla (izquierda) | Mapa (derecha), mismo criterio que Step 1 -->
      <div class="step2-containers-layout">
        <div class="step2-containers-layout__main">
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
        </div>

        <div class="step2-containers-layout__map">
          <LocationsListMap
            :locations="step2ContainerMapPins"
            detail-route-name="EditContainer"
          />
        </div>
      </div>

      <!-- Summary -->
      <div v-if="algorithmStore.selectedContainerIds.length > 0" class="mt-6 pt-4 border-t">
        <p class="text-caption font-weight-bold">{{ t('algorithm.execute.step2.summary') }}</p>
        <p class="text-caption">
          {{ t('algorithm.execute.step2.selectedCount', {
            count: algorithmStore.selectedContainerIds.length
          }) }}
        </p>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { serviceZoneColor } from '../../../../domain/enumerate/service-zone';
import { wasteTypeColor } from '../../../../domain/enumerate/waste-type';
import LocationsListMap from '../../components/common/LocationsListMap.vue';
import { useAlgorithmExecution } from '@/adapter/vuejs/composables/use-algorithm-execution';

const { t } = useI18n();

const {
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
  // Store reference
  algorithmStore,

  // Methods - Step 2
  onWasteTypeFilterChange,
  onServiceZoneFilterChange,
  onContainerLocationFilterChange,
  onStep2TableOptionsUpdate,
  isContainerSelected,
  toggleContainer,
} = useAlgorithmExecution();

// Handler wrapper for location filter (Step 2 version)
const onLocationFilterChange = (newValue: string | null) => {
  onContainerLocationFilterChange(newValue);
};
</script>

<style scoped>
.step2-containers-layout {
  display: flex;
  flex-direction: row;
  gap: 24px;
  align-items: flex-start;
}

.step2-containers-layout__main {
  flex: 2 1 0;
  min-width: 0;
}

.step2-containers-layout__map {
  flex: 1 1 0;
  min-width: 0;
  position: sticky;
  top: 12px;
}

.step2-containers-layout__map :deep(.locations-list-map),
.step2-containers-layout__map :deep(.locations-list-map--empty) {
  margin-top: 0;
}

.step2-containers-layout__map :deep(.locations-list-map__map) {
  height: min(480px, 55vh);
}

@media (max-width: 960px) {
  .step2-containers-layout {
    flex-direction: column;
  }

  .step2-containers-layout__main {
    width: 100%;
  }

  .step2-containers-layout__map {
    flex: 1 1 auto;
    width: 100%;
    position: static;
    max-width: none;
  }

  .step2-containers-layout__map :deep(.locations-list-map__map) {
    height: 280px;
  }
}
</style>
