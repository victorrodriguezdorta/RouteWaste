<template>
  <div>
    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.latitude.toString()"
          input-type="text"
          :text="t('container.add.fields.latitude')"
          :tooltip="t('container.add.fields.latitude')"
          counter="15"
          :rules="[
            (value: string) => validateMaxDecimals(value, 6),
            (value: string) => ContainerAdd.externalValidateLatitude(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateLatitude(Number(value) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.longitude.toString()"
          input-type="text"
          :text="t('container.add.fields.longitude')"
          :tooltip="t('container.add.fields.longitude')"
          counter="15"
          :rules="[
            (value: string) => validateMaxDecimals(value, 6),
            (value: string) => ContainerAdd.externalValidateLongitude(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateLongitude(Number(value) || 0)"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.postalAddress"
          input-type="text"
          :text="t('container.add.fields.postalAddress')"
          :tooltip="t('container.add.fields.postalAddress')"
          counter="150"
          :rules="[
            (value: string) => ContainerAdd.externalValidatePostalAddress(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updatePostalAddress"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.gisReference"
          input-type="text"
          :text="t('container.add.fields.gisReference')"
          :tooltip="t('container.add.fields.gisReference')"
          counter="100"
          :rules="[
            (value: string) => ContainerAdd.externalValidateGISReference(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateGisReference"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="container.wasteType"
          :items="wasteTypeOptions"
          item-title="title"
          item-value="value"
          :rules="[
            (value: string) => ContainerAdd.externalValidateWasteType(value),
          ]"
          :label="t('container.add.fields.wasteType')"
          color="primary"
          prepend-icon="mdi-delete"
          required
          @update:model-value="updateWasteType"
        />
        <v-text-field
          v-else
          :model-value="translatedWasteType"
          :label="t('container.add.fields.wasteType')"
          color="primary"
          prepend-icon="mdi-delete"
          readonly
          disabled
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.wasteDemandValue.toString()"
          input-type="text"
          :text="t('container.add.fields.wasteDemandValue')"
          :tooltip="t('container.add.fields.wasteDemandValue')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => ContainerAdd.externalValidateWasteDemandValue(Number(value)),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateWasteDemandValue(Number(value) || 0)"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.wasteDemandQuantityUnit"
          input-type="text"
          :text="t('container.add.fields.wasteDemandQuantityUnit')"
          :tooltip="t('container.add.fields.wasteDemandQuantityUnit')"
          counter="50"
          :rules="[
            (value: string) => ContainerAdd.externalValidateWasteDemandQuantityUnit(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateWasteDemandQuantityUnit"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="container.wasteDemandTimeUnit"
          :items="timeUnitOptions"
          item-title="title"
          item-value="value"
          :rules="[
            (value: string) => ContainerAdd.externalValidateWasteDemandTimeUnit(value),
          ]"
          :label="t('container.add.fields.wasteDemandTimeUnit')"
          color="primary"
          prepend-icon="mdi-clock-outline"
          required
          @update:model-value="updateWasteDemandTimeUnit"
        />
        <v-text-field
          v-else
          :model-value="translatedTimeUnit"
          :label="t('container.add.fields.wasteDemandTimeUnit')"
          color="primary"
          prepend-icon="mdi-clock-outline"
          readonly
          disabled
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="container.serviceZone"
          :items="serviceZoneOptions"
          item-title="title"
          item-value="value"
          clearable
          :rules="[
            (value: string) => ContainerAdd.externalValidateServiceZone(value),
          ]"
          :label="t('container.add.fields.serviceZone')"
          color="primary"
          prepend-icon="mdi-map-marker-radius"
          @update:model-value="updateServiceZone"
        />
        <v-text-field
          v-else
          :model-value="translatedServiceZone"
          :label="t('container.add.fields.serviceZone')"
          color="primary"
          prepend-icon="mdi-map-marker-radius"
          readonly
          disabled
        />
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts" setup>
import { InputTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { serviceZoneToOptions } from '../../../../domain/enumerate/service-zone';
import { timeUnitToOptions } from '../../../../domain/enumerate/time-unit';
import { wasteTypeToOptions } from '../../../../domain/enumerate/waste-type';
import { ContainerAdd } from '../../dto/container/container-add';

const { t } = useI18n();

const props = defineProps<{
  container: any;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:container', value: any): void;
}>();

const wasteTypeOptions = computed(() => wasteTypeToOptions(t));
const timeUnitOptions = computed(() => timeUnitToOptions(t));
const serviceZoneOptions = computed(() => serviceZoneToOptions(t));

const translatedWasteType = computed(() => {
  if (!props.container.wasteType) return '';
  return t(`container.add.wasteTypes.${props.container.wasteType}`);
});

const translatedTimeUnit = computed(() => {
  if (!props.container.wasteDemandTimeUnit) return '';
  return t(`common.timeUnits.${props.container.wasteDemandTimeUnit}`);
});

const translatedServiceZone = computed(() => {
  if (!props.container.serviceZone) return '';
  return t(`container.add.serviceZones.${props.container.serviceZone}`);
});

const updateLatitude = (value: number) => {
  emit('update:container', { ...props.container, latitude: value });
};

const updateLongitude = (value: number) => {
  emit('update:container', { ...props.container, longitude: value });
};

const updatePostalAddress = (value: string) => {
  emit('update:container', { ...props.container, postalAddress: value });
};

const updateGisReference = (value: string) => {
  emit('update:container', { ...props.container, gisReference: value });
};

const updateWasteType = (value: string) => {
  emit('update:container', { ...props.container, wasteType: value });
};

const updateWasteDemandValue = (value: number) => {
  emit('update:container', { ...props.container, wasteDemandValue: value });
};

const updateWasteDemandQuantityUnit = (value: string) => {
  emit('update:container', { ...props.container, wasteDemandQuantityUnit: value });
};

const updateWasteDemandTimeUnit = (value: string) => {
  emit('update:container', { ...props.container, wasteDemandTimeUnit: value });
};

const updateServiceZone = (value: string | null) => {
  emit('update:container', { ...props.container, serviceZone: value ?? undefined });
};

const validateMaxDecimals = (value: string, maxDecimals: number): boolean | string => {
  if (!value || value === '') return true;

  if (value.includes('.')) {
    const decimalPart = value.split('.')[1];
    if (decimalPart.length > maxDecimals) {
      return t('common.validationMessages.maxDecimals', { maxDecimals });
    }
  }

  return true;
};
</script>
