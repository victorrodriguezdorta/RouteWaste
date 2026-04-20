<template>
  <CrudLayout
    :title="t('algorithm.execute.title')"
    icon="mdi-play"
  >
    <v-container fluid>
      <div class="d-flex flex-column align-center justify-center" style="height: 400px;">
        <v-icon size="80" color="primary" class="mb-4">
          mdi-play
        </v-icon>
        <h2 class="text-h5 text-primary">{{ t('algorithm.execute.title') }}</h2>
        <p class="text-body-1 text-grey-darken-2 mt-4">
          {{ t('algorithm.execute.description') }}
        </p>
      </div>
    </v-container>
  </CrudLayout>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import CrudLayout from '../../components/common/CrudLayout.vue';

const { t } = useI18n();
</script>

<style scoped>
/* Styles to be added later */
</style>
