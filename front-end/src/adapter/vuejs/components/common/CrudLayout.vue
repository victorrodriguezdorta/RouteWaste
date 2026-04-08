<template>
  <v-container fluid class="pa-0">
    <v-row justify="center" class="ma-0">
      <v-col cols="12" :md="contentMd" :lg="contentLg" class="pa-4">
        <!-- Header Section -->
        <v-row align="center" class="mb-6 mt-2">
          <v-col class="d-flex align-center">
            <v-icon v-if="icon" size="48" class="mr-4" color="primary">{{ icon }}</v-icon>
            <h1 class="text-h4 font-weight-bold text-primary">{{ title }}</h1>
            
            <div class="ml-6">
              <slot name="title-actions" />
            </div>
          </v-col>
          
          <v-spacer />
          
          <v-col cols="auto" class="d-flex align-center">
            <slot name="toolbar-append" />
            
            <v-btn
              v-if="showGoBack"
              class="ml-2"
              color="primary"
              variant="outlined"
              @click="goBack"
            >
              <v-icon start icon="mdi-arrow-left" />
              {{ t('common.buttons.cancel') }}
            </v-btn>
          </v-col>
        </v-row>

        <!-- Content Section -->
        <div>
          <slot />
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

withDefaults(defineProps<{
  title: string;
  icon?: string;
  showGoBack?: boolean;
  goBack?: () => void;
  contentMd?: number | string;
  contentLg?: number | string;
}>(), {
  showGoBack: false,
  contentMd: 12,
  contentLg: 12
});
</script>

<style scoped>
.rounded-lg { border-radius: 8px !important; }
</style>
