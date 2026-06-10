<template>
  <v-container>
    <v-snackbar
      v-model="containerNotification.flag"
      :color="containerNotification.color"
      :timeout="containerNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ containerNotification.icon }}</v-icon>
      <strong>{{ containerNotification.title }}</strong>
      <p class="mb-0">{{ containerNotification.msg }}</p>
    </v-snackbar>

    <CrudLayout
      :title="t('container.add.cardTitle')"
      icon="mdi-delete"
      :show-go-back="true"
      :go-back="goBack"
    >
      <v-row class="show-entity-detail-layout ma-0" align="start">
        <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
          <v-form ref="containerForm">
            <ContainerFormFields
              :container="newContainer"
              map-picker-aside
              @update:container="newContainer = $event"
            />

            <v-alert type="info" variant="tonal" class="mt-4">
              {{ t('common.alerts.requiredFields') }}
            </v-alert>
          </v-form>
        </v-col>

        <v-col cols="12" md="6" lg="7" class="pa-0 pl-md-4">
          <LocationPickerMap
            class="mt-6 mt-md-0"
            :latitude="newContainer.latitude"
            :longitude="newContainer.longitude"
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
import { WasteType } from '../../../../domain/enumerate/waste-type';
import CrudLayout from '../../components/common/CrudLayout.vue';
import LocationPickerMap from '../../components/common/LocationPickerMap.vue';
import ContainerFormFields from '../../components/container/ContainerFormFields.vue';
import { ContainerAdd } from '../../dto/container/container-add';
import router from '../../router/router';
import { useContainerStore } from '../../stores/container-store';

const { t } = useI18n();

const containerStore = useContainerStore();
const { containerNotification } = storeToRefs(containerStore);

const newContainer = ref<ContainerAdd>(
  new ContainerAdd(
    0,
    0,
    '',
    '',
    'Container',
    WasteType.ORGANIC,
    100,
    50
  )
);

const containerForm = ref();

const onPickerLocation = (value: { latitude: number; longitude: number }) => {
  newContainer.value.latitude = value.latitude;
  newContainer.value.longitude = value.longitude;
};

const registerContainer = async () => {
  await containerStore.registerContainer(
    ContainerAdd.toContainer(newContainer.value as ContainerAdd)
  );
};

const validate = async () => {
  const { valid } = await containerForm.value?.validate();

  if (valid) {
    await registerContainer();
  } else {
    containerStore.setNotification(
      t('common.validationMessages.validationError'),
      t('common.validationMessages.checkFormFields'),
      'mdi-alert-circle',
      'error'
    );
  }
};

const goBack = () => {
  router.push({ name: 'Containers' });
};

watch(
  () => containerNotification.value,
  (newValue) => {
    if (newValue.color === 'success') {
      setTimeout(() => {
        router.push({ name: 'Containers' });
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
