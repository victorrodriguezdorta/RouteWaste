<script setup lang="ts">
import { ref } from 'vue';
import type { Link } from '@ull-tfg/ull-tfg-typescript';
import { Footer } from '@ull-tfg/ull-tfg-vue';
import AppHeader from './adapter/vuejs/components/common/AppHeader.vue';
import NavigationDrawer from './adapter/vuejs/components/common/navigation-drawer.vue';

const drawerOpen = ref(false);

const footerLinks: Link[] = [
  { href: 'https://www.ull.es', text: 'ULL' },
  { href: 'https://www.ull.es', text: 'RouteWaste · TFG Víctor Rodríguez Dorta' },
];

/** Theme token from `main.ts` (`professionalLight.colors`): light gray bar */
const footerThemeColor = 'surface-soft';
const FOOTER_LAYOUT_PX = 80;
const footerLayoutCss = `${FOOTER_LAYOUT_PX}px`;
</script>

<template>
  <v-app>
    <navigation-drawer v-model="drawerOpen" />
    <app-header @toggle-drawer="drawerOpen = !drawerOpen" />

    <v-main scrollable class="app-main">
      <div class="app-main-scroll-inner">
        <div class="app-main-body">
          <router-view />
        </div>
        <div class="app-footer-spacer" aria-hidden="true" />
        <div class="app-footer-zone">
          <Footer
            :links="footerLinks"
            :color="footerThemeColor"
            :height="FOOTER_LAYOUT_PX"
            :app="false"
          />
        </div>
      </div>
    </v-main>
  </v-app>
</template>

<style scoped>
/*
 * Column: content grows; footer sits after the last route content (scroll to reveal).
 * Body stacks above the footer so maps/overlays stay on top of that “floor”.
 */
.app-main :deep(.v-main__scroller) {
  display: flex;
  flex-direction: column;
}

.app-main-scroll-inner {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  /* At least one viewport + footer: short pages still require scrolling to see the bar */
  min-height: calc(100% + v-bind(footerLayoutCss));
  box-sizing: border-box;
}

.app-main-body {
  flex: 0 0 auto;
  min-height: 0;
  position: relative;
  z-index: 1;
}

.app-footer-spacer {
  flex: 1 1 auto;
  min-height: 0;
}

.app-footer-zone {
  flex: 0 0 auto;
  position: relative;
  z-index: 0;
}

.app-footer-zone :deep(.v-footer) {
  position: relative;
}

/*
 * Library footer uses `text-white`; on `surface-soft` we need readable text.
 */
.app-footer-zone :deep(.text-white) {
  color: rgba(var(--v-theme-neutral-base), 0.72) !important;
}
</style>
