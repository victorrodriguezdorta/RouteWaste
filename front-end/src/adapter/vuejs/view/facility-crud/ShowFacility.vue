<template>
  <v-container>
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="t('common.loading')"
      :message="t('facility.show.loading')"
      color="primary"
      persistent
    />

    <template v-else-if="facilityInfo">
      <CrudLayout
        :title="title"
        icon="mdi-eye"
        :show-go-back="true"
        :go-back="goBack"
      >
        <v-row class="show-entity-detail-layout ma-0" align="start">
          <v-col cols="12" md="6" lg="5" class="pa-0 pr-md-4">
            <v-form>
              <FacilityFormFields
                :facility="facilityInfo"
                :readonly="true"
              />

              <v-text-field
                :model-value="facilityInfo.id"
                :label="t('facility.show.fields.facilityId')"
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
              :latitude="facilityInfo.latitude"
              :longitude="facilityInfo.longitude"
              :title="facilityInfo.postalAddress"
              :subtitle="`GIS: ${facilityInfo.gisReference}`"
              :eyebrow="t('facility.show.map.eyebrow')"
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
import CrudLayout from '../../components/common/CrudLayout.vue';
import LocationMap from '../../components/common/LocationMap.vue';
import FacilityFormFields from '../../components/facility/FacilityFormFields.vue';
import { FacilityInfo } from '../../dto/facility/facility-info';
import router from '../../router/router';
import { useFacilityStore } from '../../stores/facility-store';

const { t, locale } = useI18n();

const route = useRoute();
const facilityStore = useFacilityStore();
const { facility } = storeToRefs(facilityStore);

const loading = ref(true);
const title = ref(t('facility.show.title'));
const facilityId = ref(route.params.id.toString());
const facilityInfo = ref<FacilityInfo>();

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
    title.value = t('facility.show.title');
  }
});

const setFacility = () => {
  if (facility.value) {
    facilityInfo.value = FacilityInfo.fromFacility(facility.value);
    title.value = t('facility.show.title');
  }
};

const goToEdit = () => {
  router.push({ name: 'EditFacility', params: { id: facilityId.value } });
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
