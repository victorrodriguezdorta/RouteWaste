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
        <v-row class="show-entity-detail-layout ma-0" align="start">
          <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
            <v-form ref="facilityForm">
              <FacilityFormFields
                v-if="editFacility"
                :facility="editFacility"
                map-picker-aside
                @update:facility="editFacility = $event"
              />

              <v-alert type="info" variant="tonal" class="mt-4">
                {{ t('common.alerts.requiredFields') }}
              </v-alert>
            </v-form>
          </v-col>

          <v-col cols="12" md="6" lg="7" class="pa-0 pl-md-4">
            <LocationPickerMap
              v-if="editFacility"
              class="mt-6 mt-md-0"
              :latitude="editFacility.latitude"
              :longitude="editFacility.longitude"
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
import LocationPickerMap from '../../components/common/LocationPickerMap.vue';
import FacilityFormFields from '../../components/facility/FacilityFormFields.vue';
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

const onPickerLocation = (value: { latitude: number; longitude: number }) => {
  if (!editFacility.value) {
    return;
  }
  editFacility.value.latitude = value.latitude;
  editFacility.value.longitude = value.longitude;
};

const redirectToListAfterSuccess = () => {
  if (facilityNotification.value.color !== 'success') {
    return;
  }

  setTimeout(() => {
    router.push({ name: 'Facilities' });
  }, 1000);
};

const updateFacility = async () => {
  if (!editFacility.value) return;

  await facilityStore.updateFacility(
    facilityId.value,
    FacilityEdit.toFacility(editFacility.value)
  );
  redirectToListAfterSuccess();
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

</script>

<style scoped>
.v-card {
  border-radius: 8px;
}
</style>
