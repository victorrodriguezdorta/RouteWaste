<!--
  ShowVehicle.vue
  View component for displaying vehicle details in read-only mode
  Shows all vehicle information with disabled inputs
-->
<template>
  <v-container>
    <!-- Loading state with LoaderDialog -->
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      title="Loading..."
      :message="t('vehicle.show.loading')"
      color="primary"
      persistent
    />

    <!-- Main content (shown when not loading and vehicle is loaded) -->
    <template v-else-if="vehicleInfo">
      <CrudLayout
        :title="title"
        icon="mdi-eye"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-form>
          <!-- Vehicle type (read-only) -->
          <v-text-field
            :model-value="formatVehicleType(vehicleInfo.vehicleType as VehicleType)"
            :label="t('vehicle.show.fields.vehicleType')"
            color="primary"
            prepend-icon="mdi-truck-cargo-container"
            disabled
            readonly
          ></v-text-field>

          <!-- Transport capacity (read-only) and Cost fields (read-only) -->
          <VehicleFormFields 
            v-if="vehicleInfo"
            :vehicle="vehicleInfo"
            :readonly="true"
          />

          <!-- Time unit (read-only) -->
          <v-text-field
            :model-value="formatTimeUnit(vehicleInfo.capacityTimeUnit as TimeUnit)"
            :label="t('vehicle.show.fields.timeUnit')"
            color="primary"
            prepend-icon="mdi-clock-outline"
            disabled
            readonly
          ></v-text-field>

          <!-- Vehicle UUID (read-only) -->
          <v-text-field
            :model-value="vehicleInfo.id"
            :label="t('vehicle.show.fields.vehicleId')"
            color="primary"
            prepend-icon="mdi-identifier"
            disabled
            readonly
            variant="outlined"
            class="mt-4"
          ></v-text-field>
        </v-form>

        <template #toolbar-append>
          <ButtonTooltip
            @click="goToEdit"
            icon="mdi-pencil"
            variant="elevated"
            color="white"
            :text="t('common.buttons.edit')"
            :tooltip="t('common.buttons.edit')"
            class="ml-2 mr-4"
          />
        </template>
      </CrudLayout>
    </template>
  </v-container>
</template>

<script lang="ts" setup>
/**
 * ShowVehicle Component
 * 
 * Composition API component for displaying vehicle details.
 * Shows all vehicle information in read-only mode.
 * Uses VehicleInfo DTO for data display.
 */

import { ButtonTooltip, LoaderDialog } from '@ull-tfg/ull-tfg-vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { TimeUnit } from '../../../../domain/enumerate/time-unit';
import { VehicleType } from '../../../../domain/enumerate/vehicle-type';
import VehicleFormFields from '../../components/vehicle/vehicle-form-fields.vue';
import { VehicleInfo } from '../../dto/vehicle/vehicle-info';
import router from '../../router/router';
import { useVehicleStore } from '../../stores/vehicle-store';

// Vue I18n composable for translations
const { t, locale } = useI18n();

// Router and store instances
const route = useRoute();
const vehicleStore = useVehicleStore();
const { vehicle } = storeToRefs(vehicleStore);

// Loading state and component data
const loading = ref(true);
const title = ref(t('vehicle.show.title'));
const vehicleId = ref(route.params.id.toString());
const vehicleInfo = ref<VehicleInfo>();

/**
 * Initialize view when component mounts
 * Fetches vehicle data from the server
 */
onMounted(async () => {
  loading.value = true;
  await vehicleStore.getVehicleById(vehicleId.value);
  setVehicle();
  loading.value = false;
});

// Re-apply translations when locale changes
watch(locale, () => {
  if (vehicle.value) {
    setVehicle();
  } else {
    title.value = t('vehicle.show.title');
  }
});

/**
 * Set up vehicle information from store
 * Converts domain entity to VehicleInfo DTO for display
 * Updates page title with vehicle type
 */
const setVehicle = () => {
  if (vehicle.value) {
    vehicleInfo.value = new VehicleInfo(
      vehicle.value.getId().toString(),
      vehicle.value.getVehicleType(),
      vehicle.value.getTransportCapacity().getValue(),
      vehicle.value.getTransportCapacity().getQuantityUnit().getValue(),
      vehicle.value.getTransportCapacity().getTimeUnit(),
      vehicle.value.getCostPerKilometer().getAmount(),
      vehicle.value.getCostPerKilometer().getCurrency().getCode()
    );

    // Update title with vehicle type
    title.value = t('vehicle.show.titleWithType', { type: formatVehicleType(vehicle.value.getVehicleType()) });
  }
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
 * Format time unit enum to display string using i18n
 * @param unit - TimeUnit enum value
 * @returns Formatted time unit label
 */
const formatTimeUnit = (unit: TimeUnit): string => {
  return t(`common.timeUnits.${unit}`);
};

/**
 * Navigate to edit view for this vehicle
 */
const goToEdit = () => {
  router.push({ name: 'EditVehicle', params: { id: vehicleId.value } });
};

/**
 * Navigate back to vehicles list
 */
const goBack = () => {
  router.push({ name: 'Vehicles' });
};
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}
</style>
