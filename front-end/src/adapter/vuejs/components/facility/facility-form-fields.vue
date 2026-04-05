<template>
  <div>
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
          :latitude="facility.latitude"
          :longitude="facility.longitude"
          @update:location="updateCoordinates"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="facility.facilityType"
          :items="facilityTypeOptions"
          item-title="title"
          item-value="value"
          :rules="[(value: string) => FacilityAdd.externalValidateFacilityType(value)]"
          :label="t('facility.add.fields.facilityType')"
          color="primary"
          prepend-icon="mdi-factory"
          required
          @update:model-value="updateFacilityType"
        />
        <v-text-field
          v-else
          :model-value="translatedFacilityType"
          :label="t('facility.add.fields.facilityType')"
          color="primary"
          prepend-icon="mdi-factory"
          readonly
          disabled
        />
      </v-col>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="facility.status"
          :items="facilityStatusOptions"
          item-title="title"
          item-value="value"
          :rules="[(value: string) => FacilityAdd.externalValidateStatus(value)]"
          :label="t('facility.add.fields.status')"
          color="primary"
          prepend-icon="mdi-list-status"
          required
          @update:model-value="updateStatus"
        />
        <v-text-field
          v-else
          :model-value="translatedStatus"
          :label="t('facility.add.fields.status')"
          color="primary"
          prepend-icon="mdi-list-status"
          readonly
          disabled
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.latitude.toString()"
          input-type="text"
          :text="t('facility.add.fields.latitude')"
          :tooltip="t('facility.add.fields.latitude')"
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
          :data="facility.longitude.toString()"
          input-type="text"
          :text="t('facility.add.fields.longitude')"
          :tooltip="t('facility.add.fields.longitude')"
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
          :data="facility.postalAddress"
          input-type="text"
          :text="t('facility.add.fields.postalAddress')"
          :tooltip="t('facility.add.fields.postalAddress')"
          counter="150"
          :rules="[(value: string) => validatePostalAddress(value)]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updatePostalAddress"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.gisReference"
          input-type="text"
          :text="t('facility.add.fields.gisReference')"
          :tooltip="t('facility.add.fields.gisReference')"
          counter="100"
          :rules="[(value: string) => validateGisReference(value)]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateGisReference"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.capacityValue.toString()"
          input-type="text"
          :text="t('facility.add.fields.capacityValue')"
          :tooltip="t('facility.add.fields.capacityValue')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => validateCapacityValue(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateCapacityValue(Number(value) || 0)"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.capacityQuantityUnit"
          input-type="text"
          :text="t('facility.add.fields.capacityQuantityUnit')"
          :tooltip="t('facility.add.fields.capacityQuantityUnit')"
          counter="50"
          :rules="[(value: string) => FacilityAdd.externalValidateCapacityQuantityUnit(value)]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="updateCapacityQuantityUnit"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <v-select
          v-if="!readonly"
          :model-value="facility.capacityTimeUnit"
          :items="timeUnitOptions"
          item-title="title"
          item-value="value"
          :rules="[(value: string) => FacilityAdd.externalValidateCapacityTimeUnit(value)]"
          :label="t('facility.add.fields.capacityTimeUnit')"
          color="primary"
          prepend-icon="mdi-clock-outline"
          required
          @update:model-value="updateCapacityTimeUnit"
        />
        <v-text-field
          v-else
          :model-value="translatedTimeUnit"
          :label="t('facility.add.fields.capacityTimeUnit')"
          color="primary"
          prepend-icon="mdi-clock-outline"
          readonly
          disabled
        />
      </v-col>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.openingFixedCost.toString()"
          input-type="text"
          :text="t('facility.add.fields.openingFixedCost')"
          :tooltip="t('facility.add.fields.openingFixedCost')"
          counter="10"
          :rules="[
            (value: string) => validateMaxDecimals(value, 2),
            (value: string) => validateOpeningFixedCost(value),
          ]"
          :required="!readonly"
          :readonly="readonly"
          :disabled="readonly"
          @updateData="(value) => updateOpeningFixedCost(Number(value) || 0)"
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" sm="6">
        <InputTooltip
          :data="facility.currencyCode"
          input-type="text"
          :text="t('facility.add.fields.currencyCode')"
          :tooltip="t('facility.add.fields.currencyCode')"
          counter="10"
          :rules="[(value: string) => FacilityAdd.externalValidateCurrencyCode(value)]"
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
import { InputTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { FacilityStatus, facilityStatusValues } from '../../../../domain/enumerate/facility-status';
import { FacilityType, facilityTypeValues } from '../../../../domain/enumerate/facility-type';
import { timeUnitToOptions } from '../../../../domain/enumerate/time-unit';
import LocationPickerMap from '../common/LocationPickerMap.vue';
import { FacilityAdd } from '../../dto/facility/facility-add';

const { t } = useI18n();

const props = defineProps<{
  facility: any;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:facility', value: any): void;
}>();

const showMapSelector = ref(false);
const timeUnitOptions = computed(() => timeUnitToOptions(t));
const facilityTypeOptions = computed(() =>
  facilityTypeValues().map((value) => ({
    title: t(`facility.add.facilityTypes.${value}`),
    value,
  }))
);
const facilityStatusOptions = computed(() =>
  facilityStatusValues().map((value) => ({
    title: t(`facility.add.statuses.${value}`),
    value,
  }))
);

const translatedFacilityType = computed(() => {
  if (!props.facility.facilityType) return '';
  return t(`facility.add.facilityTypes.${props.facility.facilityType}`);
});

const translatedStatus = computed(() => {
  if (!props.facility.status) return '';
  return t(`facility.add.statuses.${props.facility.status}`);
});

const translatedTimeUnit = computed(() => {
  if (!props.facility.capacityTimeUnit) return '';
  return t(`common.timeUnits.${props.facility.capacityTimeUnit}`);
});

const updateFacilityType = (value: FacilityType) => {
  emit('update:facility', { ...props.facility, facilityType: value });
};

const updateStatus = (value: FacilityStatus) => {
  emit('update:facility', { ...props.facility, status: value });
};

const updateLatitude = (value: number) => {
  emit('update:facility', { ...props.facility, latitude: value });
};

const updateLongitude = (value: number) => {
  emit('update:facility', { ...props.facility, longitude: value });
};

const updateCoordinates = (value: { latitude: number; longitude: number }) => {
  emit('update:facility', {
    ...props.facility,
    latitude: value.latitude,
    longitude: value.longitude,
  });
};

const updatePostalAddress = (value: string) => {
  emit('update:facility', { ...props.facility, postalAddress: value });
};

const updateGisReference = (value: string) => {
  emit('update:facility', { ...props.facility, gisReference: value });
};

const updateCapacityValue = (value: number) => {
  emit('update:facility', { ...props.facility, capacityValue: value });
};

const updateCapacityQuantityUnit = (value: string) => {
  emit('update:facility', { ...props.facility, capacityQuantityUnit: value });
};

const updateCapacityTimeUnit = (value: string) => {
  emit('update:facility', { ...props.facility, capacityTimeUnit: value });
};

const updateOpeningFixedCost = (value: number) => {
  emit('update:facility', { ...props.facility, openingFixedCost: value });
};

const updateCurrencyCode = (value: string) => {
  emit('update:facility', { ...props.facility, currencyCode: value });
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

  return FacilityAdd.externalValidateLatitude(numericValue);
};

const validateLongitude = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue < -180 || numericValue > 180) {
    return t('common.validationMessages.longitudeRange');
  }

  return FacilityAdd.externalValidateLongitude(numericValue);
};

const validatePostalAddress = (value: string): boolean | string => {
  if (!value || value === '') {
    return t('common.validationMessages.postalAddressEmpty');
  }

  return FacilityAdd.externalValidatePostalAddress(value);
};

const validateGisReference = (value: string): boolean | string => {
  if (!value || value === '') {
    return t('common.validationMessages.gisReferenceEmpty');
  }

  return FacilityAdd.externalValidateGISReference(value);
};

const validateCapacityValue = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue < 0) {
    return t('common.validationMessages.capacityNegative');
  }

  return FacilityAdd.externalValidateCapacityValue(numericValue);
};

const validateOpeningFixedCost = (value: string): boolean | string => {
  if (!value || value === '') return true;

  const numericValue = Number(value);
  if (Number.isNaN(numericValue) || numericValue < 0) {
    return t('common.validationMessages.openingFixedCostNegative');
  }

  return FacilityAdd.externalValidateOpeningFixedCost(numericValue);
};
</script>
