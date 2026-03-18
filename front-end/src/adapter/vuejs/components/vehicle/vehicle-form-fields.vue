<!--
  VehicleFormFields.vue
  Reusable component that encapsulates the 6 vehicle-specific form fields.
  Used in AddVehicle, EditVehicle, and ShowVehicle views.
  Supports both editable and read-only modes.
  Uses InputTooltip consistently for all form fields from ull-tfg-vue.
-->
<template>
  <div>
    <!-- Transport capacity (value and quantity unit) -->
    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.capacityValue.toString()"
          input-type="text"
          :text="t('vehicle.add.fields.capacityValue')"
          :tooltip="t('vehicle.add.fields.capacityValue')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) =>
              VehicleAdd.externalValidateCapacityValue(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateCapacityValue(Number(v) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.capacityQuantityUnit"
          input-type="text"
          :text="t('vehicle.add.fields.capacityUnit')"
          :tooltip="t('vehicle.add.fields.capacityUnit')"
          counter="50"
          :rules="[
            (value: string) =>
              VehicleAdd.externalValidateCapacityQuantityUnit(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateCapacityQuantityUnit"
        />
      </v-col>
    </v-row>

    <!-- Capacity time unit and cost per kilometer -->
    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.capacityTimeUnit"
          input-type="text"
          :text="t('vehicle.add.fields.timeUnit')"
          :tooltip="t('vehicle.add.fields.timeUnit')"
          counter="20"
          :rules="[
            (value: string) =>
              VehicleAdd.externalValidateCapacityTimeUnit(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateCapacityTimeUnit"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="vehicle.costPerKilometer.toString()"
          input-type="text"
          :text="t('vehicle.add.fields.costPerKilometer')"
          :tooltip="t('vehicle.add.fields.costPerKilometer')"
          counter="100"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: any) =>
              VehicleAdd.externalValidateCostPerKilometer(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(v) => updateCostPerKilometer(Number(v) || 0)"
        />
      </v-col>
    </v-row>

    <!-- Currency code -->
    <v-row>
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
 * Encapsulates the 6 reusable vehicle-specific form fields.
 * Provides a single component that can be used in Add, Edit, and Show views.
 * Supports read-only mode for displaying vehicle details.
 * 
 * Uses InputTooltip consistently for all form fields from ull-tfg-vue library.
 * This ensures visual consistency and includes tooltips for better UX.
 */

import { InputTooltip } from '@ull-tfg/ull-tfg-vue';
import { useI18n } from 'vue-i18n';
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
 * Update capacity value and emit change event
 * @param value - New capacity value as number
 */
const updateCapacityValue = (value: number) => {
  const updated = { ...props.vehicle, capacityValue: value };
  emit('update:vehicle', updated);
};

/**
 * Update capacity quantity unit and emit change event
 * @param value - New quantity unit as string
 */
const updateCapacityQuantityUnit = (value: string) => {
  const updated = { ...props.vehicle, capacityQuantityUnit: value };
  emit('update:vehicle', updated);
};

/**
 * Update capacity time unit and emit change event
 * @param value - New time unit as string
 */
const updateCapacityTimeUnit = (value: string) => {
  const updated = { ...props.vehicle, capacityTimeUnit: value };
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
</script>

<style scoped>
/* Additional styling can be added here if needed */
</style>
