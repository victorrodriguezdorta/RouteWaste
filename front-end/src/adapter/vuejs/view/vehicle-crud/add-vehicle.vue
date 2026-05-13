<!--
  AddVehicle.vue
  View component for creating new vehicles
  Displays a form with validation for all vehicle properties
-->
<template>
  <v-container>
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
      :title="t('vehicle.add.cardTitle')"
      icon="mdi-truck"
      :show-go-back="true"
      :go-back="goBack"
    >
      <v-form ref="vehicleForm">
        <!-- Transport capacity (value, quantity unit, time unit), Cost and Type fields -->
        <VehicleFormFields 
          :vehicle="newVehicle"
          @update:vehicle="newVehicle = $event"
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
          :text="t('common.buttons.save')"
          :tooltip="t('common.buttons.save')"
          class="ml-2 mr-4"
        />
      </template>
    </CrudLayout>
  </v-container>
</template>

<script lang="ts" setup>
/**
 * AddVehicle Component
 * 
 * Composition API component for creating new vehicles.
 * Uses VehicleAdd DTO for data transfer and validation.
 * Integrates with Pinia store for state management.
 * Uses ull-tfg-vue components for form fields.
 */

import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { VehicleType } from '../../../../domain/enumerate/vehicle-type';
import CrudLayout from '../../components/common/CrudLayout.vue';
import VehicleFormFields from '../../components/vehicle/vehicle-form-fields.vue';
import { VehicleAdd } from '../../dto/vehicle/vehicle-add';
import router from '../../router/router';
import { useVehicleStore } from '../../stores/vehicle-store';

// Vue I18n composable for translations
const { t, locale } = useI18n();

// Store instance and reactive references
const vehicleStore = useVehicleStore();
const { vehicleNotification } = storeToRefs(vehicleStore);

// Form data - new vehicle to be created
const newVehicle = ref<VehicleAdd>(
  new VehicleAdd(
    'Vehicle',
    VehicleType.COLLECTION_TRUCK,
    0,
    0,
    0,
    'EUR'
  )
);

// Form reference for validation
const vehicleForm = ref();

/**
 * Initialize selector options when component mounts
 */
onMounted(() => {
});

// Re-apply translations when locale changes
watch(locale, () => {
});





/**
 * Register the new vehicle in the system
 * Converts DTO to domain entity and calls store method
 */
const registerVehicle = async () => {
  await vehicleStore.registerVehicle(
    VehicleAdd.toVehicle(newVehicle.value as VehicleAdd)
  );
};

/**
 * Validate form and register vehicle if valid
 * Displays error notification if validation fails
 */
const validate = async () => {
  const { valid } = await vehicleForm.value?.validate();
  
  if (valid) {
    await registerVehicle();
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
 * Watch notification changes to redirect after successful creation
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
