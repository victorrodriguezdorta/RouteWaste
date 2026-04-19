<template>
  <CrudLayout
    :title="t('selection.types.algorithm.title')"
    icon="mdi-router"
    :content-md="12"
    :content-lg="10"
  >
    <div class="d-flex flex-column align-center justify-center" style="height: 400px;">
      <v-icon size="80" color="primary" class="mb-4">
        mdi-router
      </v-icon>
      <h2 class="text-h5 text-primary">{{ t('selection.types.algorithm.title') }}</h2>
      <p class="text-body-1 text-grey-darken-2 mt-4">
        {{ t('selection.types.algorithm.description') }}
      </p>
    </div>
  </CrudLayout>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import CrudLayout from '../../components/common/CrudLayout.vue';

const { t } = useI18n();
</script>

<style scoped>
/* Empty for now - styles can be added later */
</style>
