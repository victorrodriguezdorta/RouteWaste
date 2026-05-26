<template>
  <v-navigation-drawer
    v-model="drawer"
    app
    permanent 
    color="primary"
  >
    <v-list-item class="pa-4">
      <v-list-item-title class="text-h6 text-on-primary font-weight-bold">
        {{ $t('nav.menu') }}
      </v-list-item-title>
    </v-list-item>

    <v-divider color="on-primary" class="mb-2"></v-divider>

    <v-list density="compact" nav>
      <v-list-item
        prepend-icon="mdi-home"
        :title="t('nav.menu')"
        value="selection"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('Selection')"
      ></v-list-item>
      
      <v-divider color="on-primary" class="my-2" opacity="0.3"></v-divider>
      
      <v-list-item
        prepend-icon="mdi-truck"
        :title="t('selection.types.vehicles.title')"
        value="vehicles"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('Vehicles')"
      ></v-list-item>
      
      <v-list-item
        prepend-icon="mdi-trash-can"
        :title="t('selection.types.containers.title')"
        value="containers"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('Containers')"
      ></v-list-item>
      
      <v-list-item
        prepend-icon="mdi-office-building"
        :title="t('selection.types.facilities.title')"
        value="facilities"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('Facilities')"
      ></v-list-item>
      
      <v-divider color="on-primary" class="my-2" opacity="0.3"></v-divider>
      
      <v-list-item
        prepend-icon="mdi-router"
        :title="t('selection.types.algorithm.title')"
        value="algorithm"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('Algorithm')"
      ></v-list-item>

      <v-list-item
        prepend-icon="mdi-play-circle-outline"
        :title="t('algorithm.list.runButton')"
        value="execute-algorithm"
        base-color="on-primary"
        class="text-on-primary"
        @click="navigateTo('ExecuteAlgorithm')"
      ></v-list-item>
    </v-list>

    <template v-slot:append>
      <div class="pa-4">
        <language-switcher />
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import LanguageSwitcher from '../common/LanguageSwitcher.vue';

const { t } = useI18n();
const router = useRouter();

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue']);

/**
 * Navigate to a specific route and close the drawer on mobile
 * @param routeName - Name of the route to navigate to
 */
const navigateTo = (routeName: string) => {
  router.push({ name: routeName });
};

/**
 * Computed property to handle v-model binding with the parent component.
 */
const drawer = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);
  },
});
</script>

<style scoped>
/* Ensure the language switcher looks good on the primary background */
:deep(.white-select .v-field__input),
:deep(.white-select .v-select__selection-text),
:deep(.white-select .v-field-label),
:deep(.white-select input),
:deep(.white-select .v-icon) {
  color: white !important;
}

:deep(.white-select .v-field__outline__start),
:deep(.white-select .v-field__outline__end),
:deep(.white-select .v-field__outline__notch) {
  border-color: rgb(var(--v-theme-surface-border-light)) !important;
}
</style>
