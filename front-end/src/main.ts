import '@mdi/font/css/materialdesignicons.css';
import { createPinia } from 'pinia';
import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
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
    VTextField,
    VToolbar,
    VToolbarTitle,
    VTooltip,
} from 'vuetify/components';
import { Ripple } from 'vuetify/directives';
import { en as vuetifyEn, es as vuetifyEs } from 'vuetify/locale';
import 'vuetify/styles';
import en from './adapter/vuejs/locales/en.json';
import es from './adapter/vuejs/locales/es.json';
import router from './adapter/vuejs/router/router';
import App from './App.vue';
import './style.css';

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
    VTextField,
    VToolbar,
    VToolbarTitle,
    VTooltip,
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
        colors: {
          primary: '#31572c',      // Slate oscuro elegante
          secondary: '#4f772d',    // Slate oscuro medio
          accent: '#90a955',       // Azul moderno para resaltar
          error: '#880d1e',        // Rojo claro
          info: '#4f772d',         // Azul claro
          success: '#90a955',      // Verde esmeralda
          warning: '#ffca3a',      // Ámbar 
          background: '#ffffffef',   // Gris muy claro con tono frío
          surface: '#FFFFFF',      // Color de las tarjetas (blanco)
        }
      }
    }
  }
})

// Configure vue-i18n
const i18n = createI18n({
  legacy: false, // Use Composition API
  locale: 'es', // Default language
  fallbackLocale: 'en', // Fallback language
  messages: {
    en,
    es,
  },
})

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(vuetify)
app.use(i18n)
app.mount('#app')       
