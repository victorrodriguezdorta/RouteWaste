<script setup lang="ts">
import { ref } from 'vue';
import AppFooter from './adapter/vuejs/components/common/AppFooter.vue';
import AppHeader from './adapter/vuejs/components/common/AppHeader.vue';
import NavigationDrawer from './adapter/vuejs/components/common/navigation-drawer.vue';

const drawerOpen = ref(false);
</script>

<template>
  <v-app>
    <navigation-drawer v-model="drawerOpen" />
    <app-header @toggle-drawer="drawerOpen = !drawerOpen" />

    <v-main>
      <router-view />
    </v-main>

    <app-footer />
  </v-app>
</template>
