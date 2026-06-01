<!--
  VehicleFormFields.vue
  Reusable component that encapsulates the vehicle-specific form fields.
  Used in AddVehicle, EditVehicle, and ShowVehicle views.
  Supports both editable and read-only modes.
  Uses InputTooltip consistently for all form fields from ull-tfg-vue.
-->
<template>
  <div>
    <v-row>
      <v-col cols="12">
        <InputTooltip
          :data="vehicle.name"
          input-type="text"
          :text="t('vehicle.add.fields.name')"
          :tooltip="t('vehicle.add.fields.name')"
          counter="120"
          :rules="[
            (value: string) =>
              VehicleAdd.externalValidateName(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateName(String(v ?? ''))"
        />
      </v-col>
    </v-row>

    <!-- Vehicle type selector (editable) or text field (read-only) -->
    <v-row>
      <v-col cols="12">
        <v-select
          v-if="!readonly"
          :model-value="vehicle.vehicleType"
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
          @update:model-value="updateVehicleType"
        ></v-select>
        <v-text-field
          v-else
          :model-value="translatedVehicleType"
          :label="t('vehicle.add.fields.vehicleType')"
          color="primary"
          prepend-icon="mdi-truck-cargo-container"
          readonly
          disabled
        ></v-text-field>
      </v-col>
    </v-row>

    <!-- Capacity in kilograms -->
    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.capacityKilograms.toString()"
          input-type="text"
          :text="t('vehicle.add.fields.capacityKilograms')"
          :tooltip="t('vehicle.add.fields.capacityKilograms')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) =>
              VehicleAdd.externalValidateCapacityKilograms(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateCapacityKilograms(Number(v) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.capacityLiters.toString()"
          input-type="text"
          :text="t('vehicle.add.fields.capacityLiters')"
          :tooltip="t('vehicle.add.fields.capacityLiters')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) =>
              VehicleAdd.externalValidateCapacityLiters(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateCapacityLiters(Number(v) || 0)"
        />
      </v-col>
    </v-row>

    <!-- Cost per kilometer and currency -->
    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.costPerKilometer.toString()"
          input-type="text"
          :text="t('vehicle.add.fields.costPerKilometer')"
          :tooltip="t('vehicle.add.fields.costPerKilometer')"
          counter="100"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => validateCostPerKilometer(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateCostPerKilometer(Number(v) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.currencyCode"
          input-type="text"
          :text="t('vehicle.add.fields.currencyCode')"
          :tooltip="t('vehicle.add.fields.currencyCode')"
          counter="10"
          :rules="[
            (value: string) =>
              VehicleAdd.externalValidateCurrencyCode(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateCurrencyCode"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts" setup>
/**
 * VehicleFormFields Component
 * 
 * Encapsulates the vehicle-specific form fields.
 * Provides a single component that can be used in Add, Edit, and Show views.
 * Supports read-only mode for displaying vehicle details.
 * 
 * Uses InputTooltip consistently for all form fields from ull-tfg-vue library.
 * This ensures visual consistency and includes tooltips for better UX.
 */

import { InputTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { VehicleType, vehicleTypeLabel, vehicleTypeToOptions } from '../../../../domain/enumerate/vehicle-type';
import { VehicleAdd } from '../../dto/vehicle/vehicle-add';

// Vue I18n composable for translations
const { t } = useI18n();

/**
 * Component props
 * @param vehicle - Vehicle data object (VehicleAdd, VehicleEdit, or VehicleInfo)
 * @param readonly - If true, all fields will be disabled and read-only
 */
const props = defineProps<{
  vehicle: any; // VehicleAdd | VehicleEdit | VehicleInfo
  readonly?: boolean;
}>();

/**
 * Events emitted by the component
 */
const emit = defineEmits<{
  (e: 'update:vehicle', value: any): void;
}>();

/**
 * Options for the vehicle type selector (derived from domain)
 */
const vehicleTypeOptions = computed(() => vehicleTypeToOptions(t));

/**
 * Translated vehicle type for read-only display
 */
const translatedVehicleType = computed(() => {
  if (!props.vehicle.vehicleType) return '';
  return vehicleTypeLabel(t, props.vehicle.vehicleType);
});

const updateName = (value: string) => {
  emit('update:vehicle', { ...props.vehicle, name: value });
};

/**
 * Update vehicle type and emit change event
 * @param value - New vehicle type
 */
const updateVehicleType = (value: VehicleType) => {
  const updated = { ...props.vehicle, vehicleType: value };
  emit('update:vehicle', updated);
};

/**
 * Update capacity in kilograms and emit change event
 * @param value - New capacity in kilograms as number
 */
const updateCapacityKilograms = (value: number) => {
  const updated = { ...props.vehicle, capacityKilograms: value };
  emit('update:vehicle', updated);
};

/**
 * Update capacity in liters and emit change event
 * @param value - New capacity in liters as number
 */
const updateCapacityLiters = (value: number) => {
  const updated = { ...props.vehicle, capacityLiters: value };
  emit('update:vehicle', updated);
};

/**
 * Update cost per kilometer and emit change event
 * @param value - New cost per kilometer as number
 */
const updateCostPerKilometer = (value: number) => {
  const updated = { ...props.vehicle, costPerKilometer: value };
  emit('update:vehicle', updated);
};

/**
 * Update currency code and emit change event
 * @param value - New currency code as string
 */
const updateCurrencyCode = (value: string) => {
  const updated = { ...props.vehicle, currencyCode: value };
  emit('update:vehicle', updated);
};

/**
 * Validate that input has maximum specified decimal places
 * @param value - String value to validate
 * @param maxDecimals - Maximum number of decimal places allowed
 * @returns true if valid, error message if invalid
 */
const validateMaxDecimals = (value: string, maxDecimals: number): boolean | string => {
  if (!value || value === '') return true;
  
  // Check if value has decimal point
  if (value.includes('.')) {
    const decimalPart = value.split('.')[1];
    if (decimalPart.length > maxDecimals) {
      return t('common.validationMessages.maxDecimals', { maxDecimals });
    }
  }
  
  return true;
};

/**
 * Validate cost per kilometer with translated messages.
 * Business rule for the form requires a value greater than 0.
 */
const validateCostPerKilometer = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue <= 0) {
    return t('common.validationMessages.costPerKilometerNotZero');
  }

  return VehicleAdd.externalValidateCostPerKilometer(numericValue);
};
</script>

<style scoped>
/* Additional styling can be added here if needed */
</style>
