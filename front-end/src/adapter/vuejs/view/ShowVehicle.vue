<!--
  ShowVehicle.vue
  View component for displaying vehicle details in read-only mode
  Shows all vehicle information with disabled inputs
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
        <p class="mt-4">{{ t('vehicle.show.loading') }}</p>
      </v-col>
    </v-row>

    <!-- Main content (shown when not loading and vehicle is loaded) -->
    <template v-else-if="vehicleInfo">
      <!-- Page title with vehicle type -->
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <h2 class="text-h4 mb-4 text-center">
            <v-icon class="mr-2">mdi-eye</v-icon>
            {{ title }}
          </h2>
        </v-col>
      </v-row>

      <!-- Vehicle information display (read-only) -->
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <v-card>
            <v-card-title>
              <v-icon class="mr-2">mdi-truck</v-icon>
              {{ t('vehicle.show.cardTitle') }}
            </v-card-title>
            <v-card-text>
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

                <!-- Transport capacity (read-only) -->
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      :model-value="vehicleInfo.capacityValue"
                      :label="t('vehicle.show.fields.capacityValue')"
                      color="primary"
                      type="number"
                      prepend-icon="mdi-package-variant"
                      disabled
                      readonly
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      :model-value="vehicleInfo.capacityQuantityUnit"
                      :label="t('vehicle.show.fields.capacityUnit')"
                      color="primary"
                      prepend-icon="mdi-weight"
                      disabled
                      readonly
                    ></v-text-field>
                  </v-col>
                </v-row>

                <!-- Time unit (read-only) -->
                <v-text-field
                  :model-value="formatTimeUnit(vehicleInfo.capacityTimeUnit as TimeUnit)"
                  :label="t('vehicle.show.fields.timeUnit')"
                  color="primary"
                  prepend-icon="mdi-clock-outline"
                  disabled
                  readonly
                ></v-text-field>

                <!-- Cost per kilometer (read-only) -->
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      :model-value="vehicleInfo.costPerKilometer"
                      :label="t('vehicle.show.fields.costPerKilometer')"
                      color="primary"
                      type="number"
                      prepend-icon="mdi-currency-eur"
                      disabled
                      readonly
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      :model-value="vehicleInfo.currencyCode"
                      :label="t('vehicle.show.fields.currencyCode')"
                      color="primary"
                      prepend-icon="mdi-cash"
                      disabled
                      readonly
                    ></v-text-field>
                  </v-col>
                </v-row>

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
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                @click="goBack"
                prepend-icon="mdi-arrow-left"
                variant="text"
                color="grey"
              >
                {{ t('vehicle.show.buttons.back') }}
              </v-btn>
              <v-btn
                @click="goToEdit"
                prepend-icon="mdi-pencil"
                variant="elevated"
                color="primary"
              >
                {{ t('vehicle.show.buttons.edit') }}
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
 * ShowVehicle Component
 * 
 * Composition API component for displaying vehicle details.
 * Shows all vehicle information in read-only mode.
 * Uses VehicleInfo DTO for data display.
 */

import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { TimeUnit } from '../../../domain/enumerate/time-unit';
import { VehicleType } from '../../../domain/enumerate/vehicle-type';
import { VehicleInfo } from '../dto/vehicle/vehicle-info';
import router from '../router/router';
import { useVehicleStore } from '../stores/vehicle-store';

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
  return t(`vehicle.add.timeUnits.${unit}`);
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
