<script setup lang="ts">
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { useRouter } from 'vue-router';
import logo from '../../../../assets/images/logo/WhiteLogo.svg';

const emit = defineEmits<{
  toggleDrawer: [];
}>();

const router = useRouter();

function navigateToSelection(): void {
  router.push({ name: 'Selection' });
}
</script>

<template>
  <v-app-bar color="primary" prominent elevation="2">
    <button-tooltip
      icon="mdi-menu"
      text=""
      tooltip="Main Menu"
      color="on-primary"
      variant="text"
      :eventclick="() => emit('toggleDrawer')"
    />

    <img
      :src="logo"
      alt="Logo"
      style="max-height: 60px; max-width: 150px; object-fit: contain; cursor: pointer;"
      class="ml-2"
      @click="navigateToSelection"
    />

    <v-spacer />
  </v-app-bar>
</template>
