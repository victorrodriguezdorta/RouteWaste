<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="12" :md="contentMd" :lg="contentLg">
        <v-card class="elevation-2 rounded-lg">
          
          <v-toolbar color="primary" dark>
            <v-icon v-if="icon" class="ml-4 mr-2">{{ icon }}</v-icon>
            <v-toolbar-title class="text-white font-weight-medium">
              {{ title }}
            </v-toolbar-title>
            
            <v-spacer />
            
            <v-btn
              v-if="showGoBack"
              class="ma-2"
              color="white"
              variant="outlined"
              @click="goBack"
            >
              <v-icon start icon="mdi-arrow-left" />
              {{ t('common.buttons.cancel') }}
            </v-btn>
            
            <slot name="toolbar-append" />
          </v-toolbar>

          <v-card-text class="pt-6 pb-6">
            <slot />
          </v-card-text>
          
        </v-card>
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
  contentMd: 10,
  contentLg: 10
});
</script>

<style scoped>
.rounded-lg { border-radius: 8px !important; }
</style>
