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
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useLocale } from 'vuetify';

const { locale } = useI18n();
const vuetifyLocale = useLocale();

const currentLocale = ref(locale.value);

const languages = [
  { code: 'en', name: 'English' },
  { code: 'es', name: 'Español' },
];

function changeLanguage(selectedLanguage: string): void {
  locale.value = selectedLanguage;
  vuetifyLocale.current.value = selectedLanguage;
}

watch(locale, (newLocale) => {
  currentLocale.value = newLocale;
  vuetifyLocale.current.value = newLocale;
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
  border-color: rgb(var(--v-theme-surface-border-light)) !important;
}

.white-select :deep(.v-field--focused .v-field__outline__start),
.white-select :deep(.v-field--focused .v-field__outline__end) {
  border-color: rgb(var(--v-theme-surface)) !important;
}
</style>
