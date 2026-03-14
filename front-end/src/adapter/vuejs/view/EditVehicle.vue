<!--
  EditVehicle.vue
  View component for editing existing vehicles
  Pre-populates form with current vehicle data and allows updates
-->
<template>
  <v-container>
    <!-- Loading state with spinner -->
    <v-row v-if="loading" justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular
          indeterminate
          color="primary"
          size="64"
        ></v-progress-circular>
        <p class="mt-4">{{ t('vehicle.edit.loading') }}</p>
      </v-col>
    </v-row>

    <!-- Main content (shown when not loading) -->
    <template v-else>
      <!-- Page title with dynamic vehicle type -->
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <h2 class="text-h4 mb-4 text-center">
            <v-icon class="mr-2">mdi-pencil</v-icon>
            {{ title }}
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

      <!-- Edit form pre-populated with vehicle data -->
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <v-card>
            <v-card-title>
              <v-icon class="mr-2">mdi-truck</v-icon>
              {{ t('vehicle.edit.cardTitle') }}
            </v-card-title>
            <v-card-text>
              <v-form ref="vehicleForm">
                <!-- Vehicle type selector -->
                <v-select
                  v-model="editVehicle!.vehicleType"
                  :items="vehicleTypeOptions"
                  item-title="title"
                  item-value="value"
                  :rules="[
                    (value: string) =>
                      VehicleEdit.externalValidateVehicleType(value),
                  ]"
                  :label="t('vehicle.edit.fields.vehicleType')"
                  color="primary"
                  prepend-icon="mdi-truck-cargo-container"
                  required
                ></v-select>

                <!-- Transport capacity (value and quantity unit) -->
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editVehicle!.capacityValue"
                      :rules="[
                        (value: number) =>
                          VehicleEdit.externalValidateCapacityValue(value),
                      ]"
                      :label="t('vehicle.edit.fields.capacityValue')"
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
                      v-model="editVehicle!.capacityQuantityUnit"
                      :rules="[
                        (value: string) =>
                          VehicleEdit.externalValidateCapacityQuantityUnit(value),
                      ]"
                      :label="t('vehicle.edit.fields.capacityUnit')"
                      color="primary"
                      prepend-icon="mdi-weight"
                      required
                    ></v-text-field>
                  </v-col>
                </v-row>

                <!-- Time unit for capacity -->
                <v-select
                  v-model="editVehicle!.capacityTimeUnit"
                  :items="timeUnitOptions"
                  item-title="title"
                  item-value="value"
                  :rules="[
                    (value: string) =>
                      VehicleEdit.externalValidateCapacityTimeUnit(value),
                  ]"
                  :label="t('vehicle.edit.fields.timeUnit')"
                  color="primary"
                  prepend-icon="mdi-clock-outline"
                  required
                ></v-select>

                <!-- Cost per kilometer and currency code -->
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editVehicle!.costPerKilometer"
                      :rules="[
                        (value: number) =>
                          VehicleEdit.externalValidateCostPerKilometer(value),
                      ]"
                      :label="t('vehicle.edit.fields.costPerKilometer')"
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
                      v-model="editVehicle!.currencyCode"
                      :rules="[
                        (value: string) =>
                          VehicleEdit.externalValidateCurrencyCode(value),
                      ]"
                      :label="t('vehicle.edit.fields.currencyCode')"
                      color="primary"
                      prepend-icon="mdi-cash"
                      required
                    ></v-text-field>
                  </v-col>
                </v-row>

                <v-alert type="info" variant="tonal" class="mt-4">
                  {{ t('vehicle.edit.alerts.requiredFields') }}
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
                {{ t('vehicle.edit.buttons.cancel') }}
              </v-btn>
              <v-btn
                @click="validate"
                prepend-icon="mdi-content-save"
                variant="elevated"
                color="success"
              >
                {{ t('vehicle.edit.buttons.update') }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
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

import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { TimeUnit, timeUnitValues } from '../../../domain/enumerate/time-unit';
import { VehicleType, vehicleTypeValues } from '../../../domain/enumerate/vehicle-type';
import { VehicleEdit } from '../dto/vehicle/vehicle-edit';
import router from '../router/router';
import { useVehicleStore } from '../stores/vehicle-store';

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

// Options for dropdown selectors
const vehicleTypeOptions = ref<{ title: string; value: string }[]>([]);
const timeUnitOptions = ref<{ title: string; value: string }[]>([]);

/**
 * Initialize view when component mounts
 * Fetches vehicle data from the server and populates form
 */
onMounted(async () => {
  loading.value = true;
  setVehicleTypeOptions();
  setTimeUnitOptions();
  await vehicleStore.getVehicleById(vehicleId.value);
  setVehicle();
  loading.value = false;
});

// Re-apply translations when locale changes
watch(locale, () => {
  setVehicleTypeOptions();
  setTimeUnitOptions();
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
    editVehicle.value = new VehicleEdit(
      vehicle.value.getId().toString(),
      vehicle.value.getVehicleType(),
      vehicle.value.getTransportCapacity().getValue(),
      vehicle.value.getTransportCapacity().getQuantityUnit().getValue(),
      vehicle.value.getTransportCapacity().getTimeUnit(),
      vehicle.value.getCostPerKilometer().getAmount(),
      vehicle.value.getCostPerKilometer().getCurrency().getCode()
    );

    // Update title with vehicle type
    title.value = t('vehicle.edit.titleWithType', { type: formatVehicleType(vehicle.value.getVehicleType()) });
  }
};

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
