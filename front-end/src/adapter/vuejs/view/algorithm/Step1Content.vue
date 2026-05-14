<template>
  <v-card flat class="mt-4">
    <v-card-actions>
      <v-spacer />
      <v-btn
        variant="elevated"
        color="primary"
        append-icon="mdi-chevron-right"
        @click="emit('next')"
        :disabled="!isStep1Valid"
      >
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

      <!-- Tabla + selector de vehículos (columna izquierda) | Mapa (columna derecha) -->
      <div class="step1-facilities-layout">
        <div class="step1-facilities-layout__main">
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

            <!-- No data template -->
            <template v-slot:no-data>
              <v-alert type="info" variant="tonal" class="ma-4">
                {{ t('facility.list.table.noData') }}
              </v-alert>
            </template>
          </v-data-table-server>

          <!-- Vehículos del facility seleccionado: debajo de la tabla, mapa sigue a la derecha -->
          <v-card v-if="selectedFacilityForVehicles" class="step1-vehicle-card" elevation="2">
            <v-card-title class="text-h6 bg-primary text-surface">
              Seleccionar Vehículos - {{ selectedFacilityForVehicles?.name }}
            </v-card-title>

            <v-card-text class="pt-6">
              <!-- Filtros + Confirmar (misma fila: filtro a la izquierda, botón arriba a la derecha) -->
              <div class="step1-vehicle-toolbar">
                <div class="step1-vehicle-toolbar__filter">
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
                <v-btn
                  class="step1-vehicle-toolbar__confirm"
                  variant="elevated"
                  color="primary"
                  @click="confirmVehicleSelection"
                >
                  {{ t('common.buttons.confirm') }}
                </v-btn>
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
                  <div v-if="isVehicleSelectedInOtherFacility(item.id)" class="d-flex align-center">
                    <v-tooltip text="Este vehículo ya está asignado a otra instalación">
                      <template v-slot:activator="{ props }">
                        <v-icon size="small" color="warning" v-bind="props">mdi-lock</v-icon>
                      </template>
                    </v-tooltip>
                  </div>
                  <v-checkbox
                    v-else
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
          </v-card>
        </div>

        <div class="step1-facilities-layout__map">
          <LocationsListMap
            :locations="step1FacilityMapPins"
            detail-route-name="EditFacility"
          />
        </div>
      </div>

      <!-- Summary -->
      <div v-if="algorithmStore.facilitiesWithVehicles.length > 0" class="mt-6 pt-4 border-t">
        <p class="text-caption font-weight-bold">{{ t('algorithm.execute.step1.summary') }}</p>
        <p class="text-caption">
          {{ t('algorithm.execute.step1.selectedCount', {
            count: algorithmStore.facilitiesWithVehicles.length,
            vehicleCount: totalSelectedVehicles
          }) }}
        </p>

        <!-- JSON Preview -->
        <div class="mt-4">
          <p class="text-caption font-weight-bold mb-2">{{ t('algorithm.execute.step1.jsonPreview') }}</p>
          <v-card variant="outlined" class="bg-surface-soft">
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
import { facilityTypeColor } from '../../../../domain/enumerate/facility-type';
import { vehicleTypeColor } from '../../../../domain/enumerate/vehicle-type';
import LocationsListMap from '../../components/common/LocationsListMap.vue';
import { useAlgorithmExecution } from '../../composables/useAlgorithmExecution';

const { t } = useI18n();

const emit = defineEmits<{
  next: [];
}>();

const {
  // State
  tablePage,
  itemsPerPage,
  selectedFacilityTypeFilter,
  selectedFacilityStatusFilter,
  selectedLocationFilter,
  vehicleTablePage,
  vehicleItemsPerPage,
  selectedVehicleTypeFilterDialog,
  vehicleLoading,
  totalVehicles,
  tempSelectedVehicleIds,
  facilityLoading,
  totalFacilities,

  // Computed
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
  formattedCommandJson,
  selectedFacilityForVehicles,

  // Store reference
  algorithmStore,

  // Methods
  onFacilityTypeFilterChange,
  onFacilityStatusFilterChange,
  onLocationFilterChange,
  onTableOptionsUpdate,
  isFacilitySelected,
  toggleFacility,
  isVehicleSelectedInDialog,
  toggleVehicleInDialog,
  isVehicleSelectedInOtherFacility,
  confirmVehicleSelection,
  onVehicleTableOptionsUpdate,
  onVehicleTypeFilterDialogChange,
} = useAlgorithmExecution();
</script>

<style scoped>
.step1-facilities-layout {
  display: flex;
  flex-direction: row;
  gap: 24px;
  align-items: flex-start;
}

.step1-facilities-layout__main {
  flex: 2 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step1-vehicle-card {
  margin-top: 0;
}

.step1-vehicle-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  padding: 8px 8px 0;
  margin-bottom: 16px;
}

.step1-vehicle-toolbar__filter {
  width: 180px;
  flex: 0 0 auto;
}

.step1-vehicle-toolbar__confirm {
  margin-left: auto;
}

.step1-facilities-layout__map {
  flex: 1 1 0;
  min-width: 0;
  position: sticky;
  top: 12px;
}

.step1-facilities-layout__map :deep(.locations-list-map),
.step1-facilities-layout__map :deep(.locations-list-map--empty) {
  margin-top: 0;
}

.step1-facilities-layout__map :deep(.locations-list-map__map) {
  height: min(480px, 55vh);
}

@media (max-width: 960px) {
  .step1-facilities-layout {
    flex-direction: column;
  }

  .step1-facilities-layout__main {
    width: 100%;
  }

  .step1-facilities-layout__map {
    flex: 1 1 auto;
    width: 100%;
    position: static;
    max-width: none;
  }

  .step1-facilities-layout__map :deep(.locations-list-map__map) {
    height: 280px;
  }
}
</style>
