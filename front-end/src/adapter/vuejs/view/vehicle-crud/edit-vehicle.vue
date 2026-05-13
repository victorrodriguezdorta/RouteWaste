<!--
  EditVehicle.vue
  View component for editing existing vehicles
  Pre-populates form with current vehicle data and allows updates
-->
<template>
  <v-container>
    <!-- Loading state with LoaderDialog -->
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="t('common.loading')"
      :message="t('vehicle.edit.loading')"
      color="primary"
      persistent
    />

    <!-- Main content (shown when not loading) -->
    <template v-else>
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

      <CrudLayout
        :title="title"
        icon="mdi-pencil"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-form ref="vehicleForm">
          <!-- Transport capacity (value, quantity unit, time unit), Cost and Type fields -->
          <VehicleFormFields 
            v-if="editVehicle"
            :vehicle="editVehicle"
            @update:vehicle="editVehicle = $event"
          />

          <v-alert type="info" variant="tonal" class="mt-4">
            {{ t('common.alerts.requiredFields') }}
          </v-alert>
        </v-form>

        <template #toolbar-append>
          <ButtonTooltip
            :eventclick="validate"
            icon="mdi-content-save"
            variant="elevated"
            color="success"
            :text="t('common.buttons.update')"
            :tooltip="t('common.buttons.update')"
            class="ml-2 mr-4"
          />
        </template>
      </CrudLayout>
    </template>
  </v-container>
</template>

<script lang="ts" setup>
/**
 * EditVehicle Component
 * 
 * Composition API component for editing existing vehicles.
 * Fetches vehicle data by ID and pre-populates the form.
 * Uses VehicleEdit DTO for data transfer and validation.
 */

import { ButtonTooltip, LoaderDialog } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import CrudLayout from '../../components/common/CrudLayout.vue';
import VehicleFormFields from '../../components/vehicle/vehicle-form-fields.vue';
import { VehicleEdit } from '../../dto/vehicle/vehicle-edit';
import router from '../../router/router';
import { useVehicleStore } from '../../stores/vehicle-store';

// Vue I18n composable for translations
const { t, locale } = useI18n();

// Router and store instances
const route = useRoute();
const vehicleStore = useVehicleStore();
const { vehicle, vehicleNotification } = storeToRefs(vehicleStore);

// Loading state and component data
const loading = ref(true);
const title = ref(t('vehicle.edit.title'));
const vehicleId = ref(route.params.id.toString());
const vehicleForm = ref();
const editVehicle = ref<VehicleEdit>();

/**
 * Initialize view when component mounts
 * Fetches vehicle data from the server and populates form
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
    title.value = t('vehicle.edit.title');
  }
});

/**
 * Set up vehicle data from store into editable form
 * Converts domain entity to VehicleEdit DTO
 * Updates page title with vehicle type
 */
const setVehicle = () => {
  if (vehicle.value) {
    editVehicle.value = VehicleEdit.fromVehicle(vehicle.value);

    // Update title
    title.value = t('vehicle.edit.title');
  }
};


/**
 * Update vehicle in the system
 * Converts DTO to domain entity and calls store method
 */
const updateVehicle = async () => {
  if (!editVehicle.value) return;
  
  await vehicleStore.updateVehicle(
    vehicleId.value,
    VehicleEdit.toVehicle(editVehicle.value)
  );
};

/**
 * Validate form and update vehicle if valid
 * Displays error notification if validation fails
 */
const validate = async () => {
  const { valid } = await vehicleForm.value?.validate();
  
  if (valid) {
    await updateVehicle();
  } else {
    vehicleStore.setNotification(
      t('common.validationMessages.validationError'),
      t('common.validationMessages.checkFormFields'),
      'mdi-alert-circle',
      'error'
    );
  }
};

/**
 * Navigate back to vehicles list
 */
const goBack = () => {
  router.push({ name: 'Vehicles' });
};

/**
 * Watch notification changes to redirect after successful update
 * Redirects to vehicles list after 1 second when operation succeeds
 */
watch(
  () => vehicleNotification.value,
  (newValue) => {
    if (newValue.color === 'success') {
      setTimeout(() => {
        router.push({ name: 'Vehicles' });
      }, 1000);
    }
  }
);
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}
</style>
