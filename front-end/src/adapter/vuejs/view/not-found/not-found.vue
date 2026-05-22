<template>
  <v-container class="not-found fill-height" fluid>
    <v-row align="center" justify="center" class="fill-height">
      <v-col cols="12" sm="10" md="8" lg="6" class="text-center">
        <img :src="logo" alt="RouteWaste" class="not-found__logo mb-6" />
        <p class="not-found__code text-h1 font-weight-bold text-primary mb-2">
          {{ t('notFound.code') }}
        </p>
        <h1 class="text-h4 font-weight-bold mb-3">
          {{ t('notFound.title') }}
        </h1>
        <p class="text-body-1 text-theme-muted mb-8">
          {{ t('notFound.description') }}
        </p>
        <v-btn
          color="primary"
          variant="elevated"
          size="large"
          class="px-8 rounded-pill font-weight-bold"
          prepend-icon="mdi-home"
          @click="goHome"
        >
          {{ t('notFound.goHome') }}
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import logo from '../../../../assets/images/logo/BlackLogo.svg';

const { t } = useI18n();
const router = useRouter();

const goHome = () => {
  void router.push({ name: 'Selection' });
};
</script>

<style scoped>
.not-found {
  min-height: calc(100vh - 64px - 80px);
}

.not-found__logo {
  width: min(280px, 70vw);
  height: auto;
  object-fit: contain;
}

.not-found__code {
  line-height: 1;
  letter-spacing: -0.02em;
}

.text-theme-muted {
  color: rgb(var(--v-theme-text-muted));
}
</style>
