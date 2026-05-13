<template>
  <div>
    <v-row>
      <v-col cols="12">
        <InputTooltip
          :data="container.name"
          input-type="text"
          :text="t('container.add.fields.name')"
          :tooltip="t('container.add.fields.name')"
          counter="120"
          :rules="[
            (value: string) => ContainerAdd.externalValidateName(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateName"
        />
      </v-col>
    </v-row>

    <v-row v-if="!readonly">
      <v-col cols="12">
        <v-btn
          color="primary"
          variant="tonal"
          prepend-icon="mdi-map-marker-plus"
          class="mb-4"
          @click="showMapSelector = !showMapSelector"
        >
          {{ showMapSelector ? t('common.map.selector.hideButton') : t('common.map.selector.showButton') }}
        </v-btn>

        <LocationPickerMap
          v-if="showMapSelector"
          class="mb-4"
          :latitude="container.latitude"
          :longitude="container.longitude"
          @update:location="updateCoordinates"
        />
      </v-col>
    </v-row>

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
            (value: string) => validateLatitude(value),
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
            (value: string) => validateLongitude(value),
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
            (value: string) => validatePostalAddress(value),
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
            (value: string) => validateGISReference(value),
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

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.capacityLiters.toString()"
          input-type="text"
          :text="t('container.add.fields.capacityLiters')"
          :tooltip="t('container.add.fields.capacityLiters')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => validateCapacityLiters(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateCapacityLiters(Number(value) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="container.dailyDemandLitersPerDay.toString()"
          input-type="text"
          :text="t('container.add.fields.dailyDemandLitersPerDay')"
          :tooltip="t('container.add.fields.dailyDemandLitersPerDay')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => validateDailyDemandLitersPerDay(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateDailyDemandLitersPerDay(Number(value) || 0)"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts" setup>
import { InputTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { serviceZoneToOptions } from '../../../../domain/enumerate/service-zone';
import { wasteTypeToOptions } from '../../../../domain/enumerate/waste-type';
import { ContainerAdd } from '../../dto/container/container-add';
import LocationPickerMap from '../common/LocationPickerMap.vue';

const { t } = useI18n();

const props = defineProps<{
  container: any;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:container', value: any): void;
}>();

const wasteTypeOptions = computed(() => wasteTypeToOptions(t));
const serviceZoneOptions = computed(() => serviceZoneToOptions(t));
const showMapSelector = ref(false);

const translatedWasteType = computed(() => {
  if (!props.container.wasteType) return '';
  return t(`container.add.wasteTypes.${props.container.wasteType}`);
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

const updateCoordinates = (value: { latitude: number; longitude: number }) => {
  emit('update:container', {
    ...props.container,
    latitude: value.latitude,
    longitude: value.longitude,
  });
};

const updatePostalAddress = (value: string) => {
  emit('update:container', { ...props.container, postalAddress: value });
};

const updateGisReference = (value: string) => {
  emit('update:container', { ...props.container, gisReference: value });
};

const updateName = (value: string) => {
  emit('update:container', { ...props.container, name: value });
};

const updateWasteType = (value: string) => {
  emit('update:container', { ...props.container, wasteType: value });
};

const updateCapacityLiters = (value: number) => {
  emit('update:container', { ...props.container, capacityLiters: value });
};

const updateDailyDemandLitersPerDay = (value: number) => {
  emit('update:container', { ...props.container, dailyDemandLitersPerDay: value });
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

const validateLatitude = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue < -90 || numericValue > 90) {
    return t('common.validationMessages.latitudeRange');
  }

  return ContainerAdd.externalValidateLatitude(numericValue);
};

const validateLongitude = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue < -180 || numericValue > 180) {
    return t('common.validationMessages.longitudeRange');
  }

  return ContainerAdd.externalValidateLongitude(numericValue);
};

const validatePostalAddress = (value: string): boolean | string => {
  if (!value || value === '') {
    return t('common.validationMessages.postalAddressEmpty');
  }

  return ContainerAdd.externalValidatePostalAddress(value);
};

const validateGISReference = (value: string): boolean | string => {
  if (!value || value === '') {
    return t('common.validationMessages.gisReferenceEmpty');
  }

  return ContainerAdd.externalValidateGISReference(value);
};

const validateCapacityLiters = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue <= 0) {
    return t('common.validationMessages.capacityLitersPositive');
  }

  return ContainerAdd.externalValidateCapacityLiters(numericValue);
};

const validateDailyDemandLitersPerDay = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue <= 0) {
    return t('common.validationMessages.dailyDemandLitersPositive');
  }

  return ContainerAdd.externalValidateDailyDemandLitersPerDay(numericValue);
};
</script>
