<template>
  <v-container>
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="t('common.loading')"
      :message="t('container.edit.loading')"
      color="primary"
      persistent
    />

    <template v-else>
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
        :title="title"
        icon="mdi-pencil"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-row class="show-entity-detail-layout ma-0" align="start">
          <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
            <v-form ref="containerForm">
              <ContainerFormFields
                v-if="editContainer"
                :container="editContainer"
                map-picker-aside
                @update:container="editContainer = $event"
              />

              <v-alert type="info" variant="tonal" class="mt-4">
                {{ t('common.alerts.requiredFields') }}
              </v-alert>
            </v-form>
          </v-col>

          <v-col cols="12" md="6" lg="7" class="pa-0 pl-md-4">
            <LocationPickerMap
              v-if="editContainer"
              class="mt-6 mt-md-0"
              :latitude="editContainer.latitude"
              :longitude="editContainer.longitude"
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
import ContainerFormFields from '../../components/container/ContainerFormFields.vue';
import { ContainerEdit } from '../../dto/container/container-edit';
import router from '../../router/router';
import { useContainerStore } from '../../stores/container-store';

const { t, locale } = useI18n();

const route = useRoute();
const containerStore = useContainerStore();
const { container, containerNotification } = storeToRefs(containerStore);

const loading = ref(true);
const title = ref(t('container.edit.title'));
const containerId = ref(route.params.id.toString());
const containerForm = ref();
const editContainer = ref<ContainerEdit>();

onMounted(async () => {
  loading.value = true;
  await containerStore.getContainerById(containerId.value);
  setContainer();
  loading.value = false;
});

watch(locale, () => {
  if (container.value) {
    setContainer();
  } else {
    title.value = t('container.edit.title');
  }
});

const setContainer = () => {
  if (container.value) {
    editContainer.value = ContainerEdit.fromContainer(container.value);
    title.value = t('container.edit.title');
  }
};

const onPickerLocation = (value: { latitude: number; longitude: number }) => {
  if (!editContainer.value) {
    return;
  }
  editContainer.value.latitude = value.latitude;
  editContainer.value.longitude = value.longitude;
};

const updateContainer = async () => {
  if (!editContainer.value) return;

  await containerStore.updateContainer(
    containerId.value,
    ContainerEdit.toContainer(editContainer.value)
  );
};

const validate = async () => {
  const { valid } = await containerForm.value?.validate();

  if (valid) {
    await updateContainer();
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
