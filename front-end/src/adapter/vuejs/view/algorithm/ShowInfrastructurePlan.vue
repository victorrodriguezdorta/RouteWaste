<template>
  <v-container fluid>
    <LoaderDialog
      v-if="loading"
      :dialog="loading"
      :title="label('common.loading', 'Cargando')"
      :message="label('infrastructurePlan.show.loading', 'Cargando plan de infraestructura...')"
      color="primary"
      persistent
    />

    <v-alert
      v-else-if="loadFailed"
      type="error"
      variant="tonal"
      class="ma-4"
    >
      {{ label('infrastructurePlan.show.errors.notFound', 'No se pudo cargar el plan de infraestructura.') }}
      <ButtonTooltip
        color="primary"
        variant="text"
        icon="mdi-arrow-left"
        class="ml-2"
        :text="label('common.buttons.back', 'Volver')"
        :tooltip="label('common.buttons.back', 'Volver')"
        :eventclick="goBack"
      />
    </v-alert>

    <template v-else-if="infrastructurePlan">
      <CrudLayout
        :title="title"
        icon="mdi-eye"
        :show-go-back="true"
        :go-back="goBack"
      >
        <template #title-actions>
          <ButtonTooltip
            color="primary"
            icon="mdi-pencil"
            size="default"
            variant="elevated"
            :disabled="!canOpenExecuteWithReplay"
            :eventclick="goToExecuteWithReplay"
            :text="t('infrastructurePlan.show.actions.editAsNewRun')"
            :tooltip="t('infrastructurePlan.show.actions.editAsNewRunTooltip')"
            class="ml-2"
          />
        </template>
        <InfrastructurePlanGeneralInfo :plan="infrastructurePlan" />
        <DailyPlansNavigator :plan="infrastructurePlan" />
      </CrudLayout>
    </template>
  </v-container>
</template>

<script lang="ts" setup>
import { ButtonTooltip, LoaderDialog } from '@ull-tfg/ull-tfg-vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import DailyPlansNavigator from '../../components/algorithm/DailyPlansNavigator.vue';
import InfrastructurePlanGeneralInfo from '../../components/algorithm/InfrastructurePlanGeneralInfo.vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import router from '../../router/router';
import { useAlgorithmStore } from '../../stores/algorithm-store';
import { useInfrastructurePlanStore } from '../../stores/infrastructure-plan-store';

const { t, locale } = useI18n();

const route = useRoute();
const infrastructurePlanStore = useInfrastructurePlanStore();
const algorithmStore = useAlgorithmStore();
const { infrastructurePlan } = storeToRefs(infrastructurePlanStore);

const canOpenExecuteWithReplay = computed(
  () => Boolean(infrastructurePlan.value?.executionRequestJson?.trim().length),
);

const loading = ref(true);
const loadFailed = ref(false);
const title = ref(label('infrastructurePlan.show.title', 'Detalle del plan de infraestructura'));

const loadPlan = async (id: string) => {
  if (!id) {
    loadFailed.value = true;
    loading.value = false;
    return;
  }
  loading.value = true;
  loadFailed.value = false;
  await infrastructurePlanStore.getInfrastructurePlanById(id);
  loadFailed.value = !infrastructurePlan.value;
  setTitle();
  loading.value = false;
};

onMounted(() => {
  void loadPlan(String(route.params.id ?? ''));
});

watch(
  () => route.params.id,
  (id) => {
    void loadPlan(String(id ?? ''));
  },
);

watch(locale, () => {
  setTitle();
});

function setTitle(): void {
  title.value = label('infrastructurePlan.show.title', 'Detalle del plan de infraestructura');
}

function label(key: string, fallback: string): string {
  const translated = t(key);
  return translated === key ? fallback : translated;
}

function goBack(): void {
  router.push({ name: 'Algorithm' });
}

function goToExecuteWithReplay(): void {
  const json = infrastructurePlan.value?.executionRequestJson;
  if (!json?.trim()) {
    return;
  }
  algorithmStore.queueExecutionRequestReplay(json);
  void router.push({ name: 'ExecuteAlgorithm' });
}
</script>