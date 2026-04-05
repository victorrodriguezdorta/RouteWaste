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
      <v-form ref="facilityForm">
        <FacilityFormFields
          :facility="newFacility"
          @update:facility="newFacility = $event"
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
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { FacilityStatus } from '../../../../domain/enumerate/facility-status';
import { FacilityType } from '../../../../domain/enumerate/facility-type';
import { TimeUnit } from '../../../../domain/enumerate/time-unit';
import CrudLayout from '../../components/common/CrudLayout.vue';
import FacilityFormFields from '../../components/facility/facility-form-fields.vue';
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
    0,
    'tons',
    TimeUnit.DAY,
    0,
    'EUR',
    FacilityStatus.CANDIDATE
  )
);

const facilityForm = ref();

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
