import { i18n } from './adapter/vuejs/i18n';
import router from './adapter/vuejs/router/router';
import App from './App.vue';
import './style.css';
/** Colores del tema: edítalos en `src/theme/professional-light-colors.ts` */
import { professionalLightColors } from './theme/professional-light-colors';
import '@mdi/font/css/materialdesignicons.css';
import { createPinia } from 'pinia';
import { createApp } from 'vue';
import { createVuetify } from 'vuetify';
import {
  VAlert,
  VApp,
  VAppBar,
  VAppBarTitle,
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VChip,
  VCol,
  VContainer,
  VDataTableServer,
  VDivider,
  VExpandTransition,
  VFooter,
  VForm,
  VIcon,
  VList,
  VListItem,
  VListItemTitle,
  VMain,
  VNavigationDrawer,
  VProgressCircular,
  VRow,
  VSelect,
  VSnackbar,
  VSpacer,
  VStepper,
  VStepperHeader,
  VStepperItem,
  VStepperWindow,
  VStepperWindowItem,
  VTab,
  VTabs,
  VTextarea,
  VTextField,
  VToolbar,
  VToolbarTitle,
  VTooltip,
  VWindow,
  VWindowItem
} from 'vuetify/components';
import { Ripple } from 'vuetify/directives';
import { en as vuetifyEn, es as vuetifyEs } from 'vuetify/locale';
import 'vuetify/styles';

const vuetify = createVuetify({
  components: {
    VAlert,
    VApp,
    VAppBar,
    VAppBarTitle,
    VBtn,
    VCard,
    VCardActions,
    VCardText,
    VCardTitle,
    VCheckbox,
    VChip,
    VCol,
    VContainer,
    VDataTableServer,
    VDivider,
    VExpandTransition,
    VFooter,
    VForm,
    VIcon,
    VList,
    VListItem,
    VListItemTitle,
    VMain,
    VNavigationDrawer,
    VProgressCircular,
    VRow,
    VSelect,
    VSnackbar,
    VSpacer,
    VStepper,
    VStepperHeader,
    VStepperItem,
    VStepperWindow,
    VStepperWindowItem,
    VTab,
    VTabs,
    VTextarea,
    VTextField,
    VToolbar,
    VToolbarTitle,
    VTooltip,
    VWindow,
    VWindowItem,
  },
  directives: {
    Ripple,
  },
  icons: {
    defaultSet: 'mdi',
  },
  locale: {
    locale: 'es',
    fallback: 'en',
    messages: { vuetifyEn, vuetifyEs },
  },
  theme: {
    defaultTheme: 'professionalLight',
    themes: {
      professionalLight: {
        dark: false,
        colors: professionalLightColors,
      }
    }
  }
})

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(vuetify)
app.use(i18n)
app.mount('#app')       
