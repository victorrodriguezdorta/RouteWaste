<!--
  AddContainer.vue
  View component for creating new containers
  Displays a form with validation for all container properties
-->
<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <h2 class="text-h4 mb-4 text-center">
          <v-icon class="mr-2">mdi-plus-circle</v-icon>
          {{ t('container.add.title') }}
        </h2>
      </v-col>
    </v-row>

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

    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-delete-empty</v-icon>
            {{ t('container.add.cardTitle') }}
          </v-card-title>
          <v-card-text>
            <v-form ref="containerForm">
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="newContainer.latitude"
                    :rules="[(value: number) => ContainerAdd.externalValidateLatitude(value)]"
                    :label="t('container.add.fields.latitude')"
                    color="primary"
                    type="number"
                    prepend-icon="mdi-latitude"
                    min="-90"
                    max="90"
                    step="0.0001"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="newContainer.longitude"
                    :rules="[(value: number) => ContainerAdd.externalValidateLongitude(value)]"
                    :label="t('container.add.fields.longitude')"
                    color="primary"
                    type="number"
                    prepend-icon="mdi-longitude"
                    min="-180"
                    max="180"
                    step="0.0001"
                    required
                  />
                </v-col>
              </v-row>

              <v-text-field
                v-model="newContainer.postalAddress"
                :rules="[(value: string) => ContainerAdd.externalValidatePostalAddress(value)]"
                :label="t('container.add.fields.postalAddress')"
                color="primary"
                prepend-icon="mdi-map-marker"
                required
              />

              <v-text-field
                v-model="newContainer.gisReference"
                :rules="[(value: string) => ContainerAdd.externalValidateGISReference(value)]"
                :label="t('container.add.fields.gisReference')"
                color="primary"
                prepend-icon="mdi-crosshairs-gps"
                required
              />

              <v-select
                v-model="newContainer.wasteType"
                :items="wasteTypeOptions"
                item-title="title"
                item-value="value"
                :rules="[(value: string) => ContainerAdd.externalValidateWasteType(value)]"
                :label="t('container.add.fields.wasteType')"
                color="primary"
                prepend-icon="mdi-recycle"
                required
              />

              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="newContainer.wasteDemandValue"
                    :rules="[(value: number) => ContainerAdd.externalValidateWasteDemandValue(value)]"
                    :label="t('container.add.fields.wasteDemandValue')"
                    color="primary"
                    type="number"
                    prepend-icon="mdi-scale"
                    min="0"
                    step="0.1"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newContainer.wasteDemandQuantityUnit"
                    :rules="[(value: string) => ContainerAdd.externalValidateWasteDemandQuantityUnit(value)]"
                    :label="t('container.add.fields.wasteDemandQuantityUnit')"
                    color="primary"
                    prepend-icon="mdi-weight"
                    required
                  />
                </v-col>
              </v-row>

              <v-select
                v-model="newContainer.wasteDemandTimeUnit"
                :items="timeUnitOptions"
                item-title="title"
                item-value="value"
                :rules="[(value: string) => ContainerAdd.externalValidateWasteDemandTimeUnit(value)]"
                :label="t('container.add.fields.wasteDemandTimeUnit')"
                color="primary"
                prepend-icon="mdi-clock-outline"
                required
              />

              <v-select
                v-model="newContainer.serviceZone"
                :items="serviceZoneOptions"
                item-title="title"
                item-value="value"
                :rules="[(value: string) => ContainerAdd.externalValidateServiceZone(value)]"
                :label="t('container.add.fields.serviceZone')"
                color="primary"
                prepend-icon="mdi-map"
              />

              <v-alert type="info" variant="tonal" class="mt-4">
                {{ t('container.add.alerts.requiredFields') }}
              </v-alert>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn
              @click="goBack"
              prepend-icon="mdi-arrow-left"
              variant="text"
              color="grey"
            >
              {{ t('container.add.buttons.cancel') }}
            </v-btn>
            <v-btn
              @click="validate"
              prepend-icon="mdi-content-save"
              variant="elevated"
              color="success"
              :loading="loading"
            >
              {{ t('container.add.buttons.save') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { serviceZoneValues, type ServiceZone } from '../../../domain/enumerate/service-zone';
import { TimeUnit, timeUnitValues } from '../../../domain/enumerate/time-unit';
import { WasteType, wasteTypeValues } from '../../../domain/enumerate/waste-type';
import { ContainerAdd } from '../dto/container/container-add';
import router from '../router/router';
import { useContainerStore } from '../stores/container-store';

const { t, locale } = useI18n();

const containerStore = useContainerStore();
const { containerNotification, loading } = storeToRefs(containerStore);

const newContainer = ref<ContainerAdd>(
  new ContainerAdd(
    80.4682,
    -16.2546,
    'Calle Ejemplo 123',
    'REF-12345',
    WasteType.ORGANIC,
    150.5,
    'kg',
    TimeUnit.DAY,
    'NEIGHBORHOOD'
  )
);

const containerForm = ref();
const wasteTypeOptions = ref<{ title: string; value: string }[]>([]);
const timeUnitOptions = ref<{ title: string; value: string }[]>([]);
const serviceZoneOptions = ref<{ title: string; value: string }[]>([]);

onMounted(() => {
  setWasteTypeOptions();
  setTimeUnitOptions();
  setServiceZoneOptions();
});

watch(locale, () => {
  setWasteTypeOptions();
  setTimeUnitOptions();
  setServiceZoneOptions();
});

const setWasteTypeOptions = () => {
  wasteTypeOptions.value = wasteTypeValues().map((wasteType) => ({
    title: formatWasteType(wasteType),
    value: wasteType,
  }));
};

const setTimeUnitOptions = () => {
  timeUnitOptions.value = timeUnitValues().map((timeUnit) => ({
    title: formatTimeUnit(timeUnit),
    value: timeUnit,
  }));
};

const setServiceZoneOptions = () => {
  serviceZoneOptions.value = serviceZoneValues().map((serviceZone) => ({
    title: formatServiceZone(serviceZone),
    value: serviceZone,
  }));
};

const formatWasteType = (wasteType: WasteType): string => {
  return t(`container.add.wasteTypes.${wasteType}`);
};

const formatTimeUnit = (timeUnit: TimeUnit): string => {
  return t(`container.add.timeUnits.${timeUnit}`);
};

const formatServiceZone = (serviceZone: ServiceZone): string => {
  return t(`container.add.serviceZones.${serviceZone}`);
};

const registerContainer = async () => {
  await containerStore.registerContainer(ContainerAdd.toContainer(newContainer.value as ContainerAdd));
};

const validate = async () => {
  const { valid } = await containerForm.value?.validate();

  if (valid) {
    await registerContainer();
  } else {
    containerStore.setNotification(
      'Error de Validación',
      'Por favor revisa los campos del formulario',
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
