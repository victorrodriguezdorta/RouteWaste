<!--
  LanguageSwitcher.vue
  Component for switching between available languages
  Allows users to change the application language dynamically
-->
<template>
  <v-select
    v-model="currentLocale"
    :items="languages"
    item-title="name"
    item-value="code"
    density="compact"
    variant="outlined"
    hide-details
    prepend-inner-icon="mdi-translate"
    style="max-width: 150px;"
    class="white-select"
    @update:model-value="changeLanguage"
  ></v-select>
</template>

<script lang="ts" setup>
/**
 * LanguageSwitcher Component
 * 
 * Provides a dropdown selector for changing the application language.
 * Uses vue-i18n's locale property to switch between available translations.
 */

import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

// Get i18n composable
const { locale } = useI18n();

// Current selected locale
const currentLocale = ref(locale.value);

// Available languages configuration
const languages = [
  { code: 'en', name: 'English' },
  { code: 'es', name: 'Español' },
];

/**
 * Change the application language
 * @param selectedLanguage - Language code (e.g., 'en', 'es')
 */
const changeLanguage = (selectedLanguage: string) => {
  locale.value = selectedLanguage;
};

/**
 * Watch locale changes to keep currentLocale in sync
 */
watch(locale, (newLocale) => {
  currentLocale.value = newLocale;
});
</script>

<style scoped>
/* Make all select elements white when used inside the primary app bar */
.white-select :deep(.v-field__input),
.white-select :deep(.v-select__selection-text),
.white-select :deep(.v-field-label),
.white-select :deep(input),
.white-select :deep(.v-icon) {
  color: white !important;
  caret-color: white !important;
}

.white-select :deep(.v-field__outline__start),
.white-select :deep(.v-field__outline__end),
.white-select :deep(.v-field__outline__notch) {
  border-color: rgba(255, 255, 255, 0.7) !important;
}

.white-select :deep(.v-field--focused .v-field__outline__start),
.white-select :deep(.v-field--focused .v-field__outline__end) {
  border-color: white !important;
}
</style>
