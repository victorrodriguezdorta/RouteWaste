<!--
  AddVehicle.vue
  View component for creating new vehicles
  Displays a form with validation for all vehicle properties
-->
<template>
  <v-container>
    <!-- Page title -->
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <h2 class="text-h4 mb-4 text-center">
          <v-icon class="mr-2">mdi-plus-circle</v-icon>
          {{ t('vehicle.add.title') }}
        </h2>
      </v-col>
    </v-row>

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

    <!-- Main form for vehicle creation -->
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-truck</v-icon>
            {{ t('vehicle.add.cardTitle') }}
          </v-card-title>
          <v-card-text>
            <v-form ref="vehicleForm">
              <!-- Vehicle type selector -->
              <v-select
                v-model="newVehicle.vehicleType"
                :items="vehicleTypeOptions"
                item-title="title"
                item-value="value"
                :rules="[
                  (value: string) =>
                    VehicleAdd.externalValidateVehicleType(value),
                ]"
                :label="t('vehicle.add.fields.vehicleType')"
                color="primary"
                prepend-icon="mdi-truck-cargo-container"
                required
              ></v-select>

              <!-- Transport capacity (value and quantity unit) -->
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="newVehicle.capacityValue"
                    :rules="[
                      (value: number) =>
                        VehicleAdd.externalValidateCapacityValue(value),
                    ]"
                    :label="t('vehicle.add.fields.capacityValue')"
                    color="primary"
                    type="number"
                    prepend-icon="mdi-package-variant"
                    min="0"
                    step="0.1"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newVehicle.capacityQuantityUnit"
                    :rules="[
                      (value: string) =>
                        VehicleAdd.externalValidateCapacityQuantityUnit(value),
                    ]"
                    :label="t('vehicle.add.fields.capacityUnit')"
                    color="primary"
                    prepend-icon="mdi-weight"
                    required
                  ></v-text-field>
                </v-col>
              </v-row>

              <!-- Time unit for capacity -->
              <v-select
                v-model="newVehicle.capacityTimeUnit"
                :items="timeUnitOptions"
                item-title="title"
                item-value="value"
                :rules="[
                  (value: string) =>
                    VehicleAdd.externalValidateCapacityTimeUnit(value),
                ]"
                :label="t('vehicle.add.fields.timeUnit')"
                color="primary"
                prepend-icon="mdi-clock-outline"
                required
              ></v-select>

              <!-- Cost per kilometer and currency code -->
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="newVehicle.costPerKilometer"
                    :rules="[
                      (value: number) =>
                        VehicleAdd.externalValidateCostPerKilometer(value),
                    ]"
                    :label="t('vehicle.add.fields.costPerKilometer')"
                    color="primary"
                    type="number"
                    prepend-icon="mdi-currency-eur"
                    min="0"
                    step="0.01"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newVehicle.currencyCode"
                    :rules="[
                      (value: string) =>
                        VehicleAdd.externalValidateCurrencyCode(value),
                    ]"
                    :label="t('vehicle.add.fields.currencyCode')"
                    color="primary"
                    prepend-icon="mdi-cash"
                    required
                  ></v-text-field>
                </v-col>
              </v-row>

              <v-alert type="info" variant="tonal" class="mt-4">
                {{ t('vehicle.add.alerts.requiredFields') }}
              </v-alert>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              @click="goBack"
              prepend-icon="mdi-arrow-left"
              variant="text"
              color="grey"
            >
              {{ t('vehicle.add.buttons.cancel') }}
            </v-btn>
            <v-btn
              @click="validate"
              prepend-icon="mdi-content-save"
              variant="elevated"
              color="success"
              :loading="loading"
            >
              {{ t('vehicle.add.buttons.save') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
/**
 * AddVehicle Component
 * 
 * Composition API component for creating new vehicles.
 * Uses VehicleAdd DTO for data transfer and validation.
 * Integrates with Pinia store for state management.
 */

import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { TimeUnit, timeUnitValues } from '../../../domain/enumerate/time-unit';
import { VehicleType, vehicleTypeValues } from '../../../domain/enumerate/vehicle-type';
import { VehicleAdd } from '../dto/vehicle/vehicle-add';
import router from '../router/router';
import { useVehicleStore } from '../stores/vehicle-store';

// Vue I18n composable for translations
const { t, locale } = useI18n();

// Store instance and reactive references
const vehicleStore = useVehicleStore();
const { vehicleNotification, loading } = storeToRefs(vehicleStore);

// Form data - new vehicle to be created
const newVehicle = ref<VehicleAdd>(
  new VehicleAdd(
    VehicleType.COLLECTION_TRUCK,
    0,
    'tons',
    TimeUnit.DAY,
    0,
    'EUR'
  )
);

// Form reference for validation
const vehicleForm = ref();

// Options for dropdown selectors
const vehicleTypeOptions = ref<{ title: string; value: string }[]>([]);
const timeUnitOptions = ref<{ title: string; value: string }[]>([]);

/**
 * Initialize selector options when component mounts
 */
onMounted(() => {
  setVehicleTypeOptions();
  setTimeUnitOptions();
});

// Re-apply translations when locale changes
watch(locale, () => {
  setVehicleTypeOptions();
  setTimeUnitOptions();
});

/**
 * Set up vehicle type options for the dropdown
 * Maps enum values to human-readable labels
 */
const setVehicleTypeOptions = () => {
  const types = vehicleTypeValues();
  vehicleTypeOptions.value = types.map((type) => ({
    title: formatVehicleType(type),
    value: type,
  }));
};

/**
 * Set up time unit options for the dropdown
 * Maps enum values to human-readable labels
 */
const setTimeUnitOptions = () => {
  const units = timeUnitValues();
  timeUnitOptions.value = units.map((unit) => ({
    title: formatTimeUnit(unit),
    value: unit,
  }));
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
  return t(`vehicle.add.timeUnits.${unit}`);
};

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
      'Error de Validación',
      'Por favor revisa los campos del formulario',
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
