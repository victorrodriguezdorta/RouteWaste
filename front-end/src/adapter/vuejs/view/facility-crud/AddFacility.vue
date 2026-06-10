<template>
  <v-container>
    <v-snackbar
      v-model="facilityNotification.flag"
      :color="facilityNotification.color"
      :timeout="facilityNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ facilityNotification.icon }}</v-icon>
      <strong>{{ facilityNotification.title }}</strong>
      <p class="mb-0">{{ facilityNotification.msg }}</p>
    </v-snackbar>

    <CrudLayout
      :title="t('facility.add.cardTitle')"
      icon="mdi-factory"
      :show-go-back="true"
      :go-back="goBack"
    >
      <v-row class="show-entity-detail-layout ma-0" align="start">
        <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
          <v-form ref="facilityForm">
            <FacilityFormFields
              :facility="newFacility"
              map-picker-aside
              @update:facility="newFacility = $event"
            />

            <v-alert type="info" variant="tonal" class="mt-4">
              {{ t('common.alerts.requiredFields') }}
            </v-alert>
          </v-form>
        </v-col>

        <v-col cols="12" md="6" lg="7" class="pa-0 pl-md-4">
          <LocationPickerMap
            class="mt-6 mt-md-0"
            :latitude="newFacility.latitude"
            :longitude="newFacility.longitude"
            @update:location="onPickerLocation"
          />
        </v-col>
      </v-row>

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
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { FacilityStatus } from '../../../../domain/enumerate/facility-status';
import { FacilityType } from '../../../../domain/enumerate/facility-type';
import CrudLayout from '../../components/common/CrudLayout.vue';
import LocationPickerMap from '../../components/common/LocationPickerMap.vue';
import FacilityFormFields from '../../components/facility/FacilityFormFields.vue';
import { FacilityAdd } from '../../dto/facility/facility-add';
import router from '../../router/router';
import { useFacilityStore } from '../../stores/facility-store';

const { t } = useI18n();

const facilityStore = useFacilityStore();
const { facilityNotification } = storeToRefs(facilityStore);

const newFacility = ref<FacilityAdd>(
  new FacilityAdd(
    FacilityType.OPERATIONAL_BASE,
    0,
    0,
    '',
    '',
    'Facility',
    0,
    0,
    0,
    0,
    'EUR',
    FacilityStatus.CANDIDATE
  )
);

const facilityForm = ref();

const onPickerLocation = (value: { latitude: number; longitude: number }) => {
  newFacility.value.latitude = value.latitude;
  newFacility.value.longitude = value.longitude;
};

const registerFacility = async () => {
  await facilityStore.registerFacility(
    FacilityAdd.toFacility(newFacility.value as FacilityAdd)
  );
};

const validate = async () => {
  const { valid } = await facilityForm.value?.validate();

  if (valid) {
    await registerFacility();
  } else {
    facilityStore.setNotification(
      t('common.validationMessages.validationError'),
      t('common.validationMessages.checkFormFields'),
      'mdi-alert-circle',
      'error'
    );
  }
};

const goBack = () => {
  router.push({ name: 'Facilities' });
};

watch(
  () => facilityNotification.value,
  (newValue) => {
    if (newValue.color === 'success') {
      setTimeout(() => {
        router.push({ name: 'Facilities' });
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
