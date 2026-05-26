<template>
  <v-container>
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="t('common.loading')"
      :message="t('container.show.loading')"
      color="primary"
      persistent
    />

    <template v-else-if="containerInfo">
      <CrudLayout
        :title="title"
        icon="mdi-eye"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-row class="show-entity-detail-layout ma-0" align="start">
          <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
            <v-form>
              <ContainerFormFields
                :container="containerInfo"
                :readonly="true"
              />

              <v-text-field
                :model-value="containerInfo.id"
                :label="t('container.show.fields.containerId')"
                color="primary"
                prepend-icon="mdi-identifier"
                disabled
                readonly
                variant="outlined"
                class="mt-4"
              />
            </v-form>
          </v-col>

          <v-col cols="12" md="6" lg="7" class="pa-0 pl-md-4">
            <LocationMap
              class="mt-6 mt-md-0"
              :latitude="containerInfo.latitude"
              :longitude="containerInfo.longitude"
              :title="containerInfo.postalAddress"
              :subtitle="`GIS: ${containerInfo.gisReference}`"
              :eyebrow="t('container.show.map.eyebrow')"
            />
          </v-col>
        </v-row>

        <template #toolbar-append>
          <ButtonTooltip
            :eventclick="goToEdit"
            icon="mdi-pencil"
            variant="elevated"
            color="primary"
            :text="t('common.buttons.edit')"
            :tooltip="t('common.buttons.edit')"
            class="ml-2"
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
import LocationMap from '../../components/common/LocationMap.vue';
import ContainerFormFields from '../../components/container/ContainerFormFields.vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { ContainerInfo } from '../../dto/container/container-info';
import router from '../../router/router';
import { useContainerStore } from '../../stores/container-store';

const { t, locale } = useI18n();

const route = useRoute();
const containerStore = useContainerStore();
const { container } = storeToRefs(containerStore);

const loading = ref(true);
const title = ref(t('container.show.title'));
const containerId = ref(route.params.id.toString());
const containerInfo = ref<ContainerInfo>();

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
    title.value = t('container.show.title');
  }
});

const setContainer = () => {
  if (container.value) {
    containerInfo.value = ContainerInfo.fromContainer(container.value);
    title.value = t('container.show.title');
  }
};

const goToEdit = () => {
  router.push({ name: 'EditContainer', params: { id: containerId.value } });
};

const goBack = () => {
  router.push({ name: 'Containers' });
};
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}
</style>
