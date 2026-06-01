<template>

  <v-container fluid>

    <!-- Notification snackbar for user feedback -->

    <v-snackbar

      v-model="infrastructurePlanNotification.flag"

      :color="infrastructurePlanNotification.color"

      :timeout="infrastructurePlanNotification.timeout"

      location="top"

    >

      <v-icon class="mr-2">{{ infrastructurePlanNotification.icon }}</v-icon>

      <strong>{{ infrastructurePlanNotification.title }}</strong>

      <p class="mb-0">{{ infrastructurePlanNotification.msg }}</p>

    </v-snackbar>



    <CrudLayout

      :title="t('selection.types.algorithm.title')"

      icon="mdi-router"

    >

      <template #title-actions>

        <ButtonTooltip

          color="primary"

          icon="mdi-play"

          size="default"

          variant="elevated"

          :eventclick="runAlgorithm"

          :text="t('algorithm.list.runButton')"

          :tooltip="t('algorithm.list.runButton')"

          class="ml-2"

        />

      </template>



      <!-- Algorithms data table -->

      <v-data-table-server

        :headers="headers"

        :items="infrastructureItems"

        :loading="loading"

        :items-length="totalInfrastructurePlans"

        v-model:page="tablePage"

        :items-per-page="rowsPerPage"

        :items-per-page-options="[

          { value: 5, title: '5' },

          { value: 10, title: '10' },

          { value: 25, title: '25' },

          { value: 50, title: '50' }

        ]"

        @update:options="onTableOptionsUpdate"

        item-value="planId"

        hover

        class="elevation-2"

      >

        <template v-slot:item.executedAt="{ item }">

          {{ item.executedAt }}

        </template>



        <template v-slot:item.estimatedTotalCost="{ item }">

          {{ item.estimatedTotalCost }}

        </template>



        <template v-slot:item.numberOfDays="{ item }">

          {{ item.numberOfDays }}

        </template>



        <template v-slot:item.averagePickupTimeMinutes="{ item }">

          {{ item.averagePickupTimeMinutes }}

        </template>



        <template v-slot:item.planStatus="{ item }">

          <v-chip

            v-if="item.validityState === InfrastructurePlanValidityState.RUNNING"

            :color="item.planStatusColor"

            size="small"

            variant="flat"

            class="font-weight-medium"

          >

            {{ item.validityLabel }}

          </v-chip>

          <v-chip

            v-else-if="item.executionState === InfrastructurePlanExecutionState.FAILED"

            :color="item.planStatusColor"

            size="small"

            variant="flat"

            class="font-weight-medium"

            :title="item.failureReason ?? undefined"

          >

            {{ item.executionLabel }}

          </v-chip>

          <v-chip

            v-else

            :color="item.planStatusColor"

            size="small"

            variant="flat"

            class="font-weight-medium"

          >

            {{ item.validityLabel }}

          </v-chip>

        </template>



        <template v-slot:item.actions="{ item }">

          <div class="d-inline-flex align-center ga-1" @click.stop>

            <v-progress-circular

              v-if="item.isRunning"

              indeterminate

              color="warning"

              size="24"

              width="2"

              class="mx-1"

            />

            <ButtonTooltip

              v-else

              text=""

              icon="mdi-eye"

              :tooltip="item.canView

                ? t('algorithm.list.table.tooltips.view')

                : t('infrastructurePlan.list.table.tooltips.viewUnavailable')"

              color="info"

              size="small"

              variant="text"

              :disabled="!item.canView"

              :eventclick="() => showItem(item)"

            />

            <ButtonTooltip

              text=""

              icon="mdi-delete"

              :tooltip="t('algorithm.list.table.tooltips.delete')"

              color="error"

              size="small"

              variant="text"

              :eventclick="() => deleteItem(item.planId)"

            />

          </div>

        </template>



        <template v-slot:no-data>

          <v-alert type="info" variant="tonal" class="ma-4">

            {{ t('infrastructurePlan.list.table.noData') || t('algorithm.list.table.noData') }}

            <ButtonTooltip 

              color="primary" 

              variant="text" 

              icon="mdi-play"

              :eventclick="runAlgorithm"

              class="ml-2"

              :text="t('algorithm.list.table.runFirst')"

              :tooltip="t('algorithm.list.table.runFirst')"

            />

          </v-alert>

        </template>

      </v-data-table-server>

    </CrudLayout>

    <ErrorMessage

      :model-value="dialogDelete"

      :title="t('algorithm.list.deleteDialog.title')"

      :error-message="t('algorithm.list.deleteDialog.message')"

      :reason="''"

      :cancel-text="t('common.buttons.cancel')"

      :retry-text="t('common.buttons.delete')"

      @update:model-value="dialogDelete = $event"

      @cancel="dialogDelete = false"

      @retry="confirmDelete"

    />

  </v-container>

</template>



<script lang="ts" setup>

import { ButtonTooltip, ErrorMessage } from '@ull-tfg/ull-tfg-vue';

import { storeToRefs } from 'pinia';

import { computed, onMounted, ref } from 'vue';

import { useI18n } from 'vue-i18n';

import CrudLayout from '../../components/common/CrudLayout.vue';

import router from '../../router/router';

import { useInfrastructurePlanStore } from '../../stores/infrastructure-plan-store';

import { useInfrastructurePlanExecutionEvents } from '@/adapter/vuejs/composables/use-infrastructure-plan-execution-events';

import {

  InfrastructurePlanExecutionState,

  infrastructurePlanExecutionStateFromString,
  infrastructurePlanExecutionStateLabel,
  infrastructurePlanPlanStatusChipColor,

} from '@/domain/enumerate/infrastructure-plan-execution-state';

import {

  InfrastructurePlanValidityState,

  infrastructurePlanValidityStateFromString,
  infrastructurePlanValidityStateLabel,

} from '@/domain/enumerate/infrastructure-plan-validity-state';



const { t } = useI18n();



const infrastructurePlanStore = useInfrastructurePlanStore();

const { infrastructurePlans, infrastructurePlanNotification, loading, totalInfrastructurePlans, currentPage, rowsPerPage } = storeToRefs(infrastructurePlanStore);



const tablePage = ref(1);

const currentSortBy = ref<string | undefined>('executedAt');

const currentSortOrder = ref<'asc' | 'desc'>('desc');

const dialogDelete = ref(false);

const selectedPlanId = ref('');



const headers = computed(() => [

  {

    title: t('infrastructurePlan.list.table.headers.executedAt') || 'Executed At',

    align: 'start' as const,

    sortable: true,

    key: 'executedAt',

  },

  {

    title: t('infrastructurePlan.list.table.headers.estimatedTotalCost') || 'Estimated Cost',

    align: 'start' as const,

    sortable: true,

    key: 'estimatedTotalCost',

  },

  {

    title: t('infrastructurePlan.list.table.headers.numberOfDays') || 'Days',

    align: 'center' as const,

    sortable: true,

    key: 'numberOfDays',

  },

  {

    title: t('infrastructurePlan.list.table.headers.averagePickupTimeMinutes') || 'Avg Pickup (min)',

    align: 'center' as const,

    sortable: true,

    key: 'averagePickupTimeMinutes',

  },

  {

    title: t('infrastructurePlan.list.table.headers.planStatus') || 'Status',

    align: 'center' as const,

    sortable: false,

    key: 'planStatus',

  },

  {

    title: t('algorithm.list.table.headers.actions'),

    align: 'center' as const,

    sortable: false,

    key: 'actions',

  },

]);



const infrastructureItems = computed(() => {

  return infrastructurePlans.value.map((plan) => {

    const validityState = infrastructurePlanValidityStateFromString(plan.validityState);

    const executionState = infrastructurePlanExecutionStateFromString(plan.executionState);

    const validityLabel = infrastructurePlanValidityStateLabel(t, validityState);

    const executionLabel =
      infrastructurePlanExecutionStateLabel(t, executionState) ?? validityLabel;

    const isRunning = validityState === InfrastructurePlanValidityState.RUNNING;
    const isCompleted =
      !isRunning && executionState === InfrastructurePlanExecutionState.COMPLETED;

    return {

      planId: plan.id,

      executedAt: plan.executedAt ? new Date(plan.executedAt).toLocaleString() : '-',

      estimatedTotalCost: isCompleted && plan.estimatedTotalCost

        ? `${plan.estimatedTotalCost.amount} ${plan.estimatedTotalCost.currency ?? ''}`.trim()

        : '-',

      numberOfDays: plan.numberOfDays ?? '-',

      averagePickupTimeMinutes: plan.averagePickupTimeMinutes ?? '-',

      validityState,

      validityLabel,

      executionState,

      executionLabel,

      planStatusColor: infrastructurePlanPlanStatusChipColor(validityState, executionState),

      failureReason: plan.failureReason ?? null,

      isRunning,

      canView: isCompleted,

    };

  });

});



const loadPlans = async (page: number, size: number, sortBy?: string, sortOrder?: 'asc' | 'desc') => {

  currentSortBy.value = sortBy;

  currentSortOrder.value = sortOrder ?? 'desc';

  await infrastructurePlanStore.getInfrastructurePlans(page, size, sortBy, sortOrder);

};



useInfrastructurePlanExecutionEvents(() => {

  void infrastructurePlanStore.refreshCurrentPlans();

});



onMounted(async () => {

  await loadPlans(currentPage.value, rowsPerPage.value, 'executedAt', 'desc');

  tablePage.value = currentPage.value + 1;

});



const onTableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {

  const requestedSize = options.itemsPerPage;

  if (requestedSize <= 0) return;



  const requestedPage = Math.max(options.page - 1, 0);

  const newSortBy = options.sortBy[0]?.key;

  const newSortOrder = options.sortBy[0]?.order ?? 'desc';



  currentSortBy.value = newSortBy;

  currentSortOrder.value = newSortOrder;



  await loadPlans(requestedPage, requestedSize, newSortBy, newSortOrder);

  tablePage.value = currentPage.value + 1;

};



const runAlgorithm = async () => {

  try {

    await router.push({ name: 'ExecuteAlgorithm' });

  } catch (error) {

    console.error('Failed to open algorithm stepper:', error);

  }

};



const showItem = (item: { planId: string; canView: boolean }) => {

  if (!item.planId || !item.canView) {

    return;

  }

  void router.push({ name: 'ShowInfrastructurePlan', params: { id: item.planId } });

};



const deleteItem = (itemId: string) => {

  if (!itemId) {

    return;

  }

  selectedPlanId.value = itemId;

  dialogDelete.value = true;

};



const confirmDelete = async () => {

  await infrastructurePlanStore.deleteInfrastructurePlan(selectedPlanId.value);

  await loadPlans(currentPage.value, rowsPerPage.value, currentSortBy.value, currentSortOrder.value);

  if (infrastructurePlans.value.length === 0 && currentPage.value > 0) {

    await loadPlans(currentPage.value - 1, rowsPerPage.value, currentSortBy.value, currentSortOrder.value);

    tablePage.value = currentPage.value + 1;

  }

  dialogDelete.value = false;

  selectedPlanId.value = '';

};

</script>



<style scoped>

.v-card {

  border-radius: 8px;

}



.v-data-table {

  border-radius: 8px;

}

</style>


