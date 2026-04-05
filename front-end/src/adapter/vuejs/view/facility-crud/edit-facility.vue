<template>
  <v-container>
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="t('common.loading')"
      :message="t('facility.edit.loading')"
      color="primary"
      persistent
    />

    <template v-else>
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
        :title="title"
        icon="mdi-pencil"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-form ref="facilityForm">
          <FacilityFormFields
            v-if="editFacility"
            :facility="editFacility"
            @update:facility="editFacility = $event"
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
            :text="t('common.buttons.update')"
            :tooltip="t('common.buttons.update')"
            class="ml-2 mr-4"
          />
        </template>
      </CrudLayout>
    </template>
  </v-container>
</template>

<script lang="ts" setup>
import { ButtonTooltip, LoaderDialog } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import CrudLayout from '../../components/common/CrudLayout.vue';
import FacilityFormFields from '../../components/facility/facility-form-fields.vue';
import { FacilityEdit } from '../../dto/facility/facility-edit';
import router from '../../router/router';
import { useFacilityStore } from '../../stores/facility-store';

const { t, locale } = useI18n();

const route = useRoute();
const facilityStore = useFacilityStore();
const { facility, facilityNotification } = storeToRefs(facilityStore);

const loading = ref(true);
const title = ref(t('facility.edit.title'));
const facilityId = ref(route.params.id.toString());
const facilityForm = ref();
const editFacility = ref<FacilityEdit>();

onMounted(async () => {
  loading.value = true;
  await facilityStore.getFacilityById(facilityId.value);
  setFacility();
  loading.value = false;
});

watch(locale, () => {
  if (facility.value) {
    setFacility();
  } else {
    title.value = t('facility.edit.title');
  }
});

const setFacility = () => {
  if (facility.value) {
    editFacility.value = FacilityEdit.fromFacility(facility.value);
    title.value = t('facility.edit.title');
  }
};

const updateFacility = async () => {
  if (!editFacility.value) return;

  await facilityStore.updateFacility(
    facilityId.value,
    FacilityEdit.toFacility(editFacility.value)
  );
};

const validate = async () => {
  const { valid } = await facilityForm.value?.validate();

  if (valid) {
    await updateFacility();
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
