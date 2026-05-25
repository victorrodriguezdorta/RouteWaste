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
      :title="t('common.loading')"
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
          <!-- All vehicle fields (read-only) -->
          <VehicleFormFields 
            v-if="vehicleInfo"
            :vehicle="vehicleInfo"
            :readonly="true"
          />

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
            :eventclick="goToEdit"
            icon="mdi-pencil"
            variant="elevated"
            color="primary"
            :text="t('common.buttons.edit')"
            :tooltip="t('common.buttons.edit')"
            class="ml-2"
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
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import CrudLayout from '../../components/common/CrudLayout.vue';
import VehicleFormFields from '../../components/vehicle/VehicleFormFields.vue';
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
    vehicleInfo.value = VehicleInfo.fromVehicle(vehicle.value);
    title.value = t('vehicle.show.title');
  }
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
