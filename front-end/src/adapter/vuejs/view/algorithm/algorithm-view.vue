<template>
  <v-container fluid>
    <!-- Notification snackbar for user feedback -->
    <v-snackbar
      v-model="algorithmNotification.flag"
      :color="algorithmNotification.color"
      :timeout="algorithmNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ algorithmNotification.icon }}</v-icon>
      <strong>{{ algorithmNotification.title }}</strong>
      <p class="mb-0">{{ algorithmNotification.msg }}</p>
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
        :items="algorithmItems"
        :loading="loading"
        :items-length="totalAlgorithms"
        v-model:page="tablePage"
        :items-per-page="itemsPerPage"
        :items-per-page-options="[
          { value: 5, title: '5' },
          { value: 10, title: '10' },
          { value: 25, title: '25' },
          { value: 50, title: '50' }
        ]"
        @update:options="onTableOptionsUpdate"
        item-value="id"
        hover
        class="elevation-2"
      >
        <!-- Empty state when no algorithms exist -->
        <template v-slot:no-data>
          <v-alert type="info" variant="tonal" class="ma-4">
            {{ t('algorithm.list.table.noData') }}
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
  </v-container>
</template>

<script lang="ts" setup>
/**
 * AlgorithmView Component
 * 
 * Composition API component for displaying all algorithms.
 * Currently displays empty state as entities are not yet available.
 * Uses Vuetify data table with pagination structure ready for future data.
 */

import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import CrudLayout from '../../components/common/CrudLayout.vue';
import router from '../../router/router';

// Vue I18n composable for translations
const { t } = useI18n();

// Local component state
const tablePage = ref(1);
const itemsPerPage = ref(10);
const loading = ref(false);
const totalAlgorithms = ref(0);

// Notification state
const algorithmNotification = ref({
  flag: false,
  color: 'info',
  timeout: 3000,
  icon: 'mdi-information',
  title: '',
  msg: '',
});

// Table column headers configuration
const headers = computed(() => [
  {
    title: t('algorithm.list.table.headers.name'),
    align: 'start' as const,
    sortable: true,
    key: 'name',
  },
  {
    title: t('algorithm.list.table.headers.description'),
    align: 'start' as const,
    sortable: false,
    key: 'description',
  },
  {
    title: t('algorithm.list.table.headers.actions'),
    align: 'center' as const,
    sortable: false,
    key: 'actions',
  },
]);

/**
 * Computed table items - empty for now as entities don't exist yet
 * @returns Empty array (no algorithms available)
 */
const algorithmItems = computed(() => {
  return [];
});

/**
 * Load algorithms when component mounts
 */
onMounted(async () => {
  // Placeholder for future algorithm loading
  // await loadAlgorithms(0, itemsPerPage.value);
});

/**
 * Handle pagination and sort changes from Vuetify table
 */
const onTableOptionsUpdate = async (options: { page: number; itemsPerPage: number; sortBy: { key: string; order: 'asc' | 'desc' }[] }) => {
  // Placeholder for future implementation
};

/**
 * Navigate to algorithm execution view
 */
const runAlgorithm = () => {
  router.push({ name: 'ExecuteAlgorithm' });
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
