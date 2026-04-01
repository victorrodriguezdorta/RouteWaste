import en from './adapter/vuejs/locales/en.json';
import es from './adapter/vuejs/locales/es.json';
import router from './adapter/vuejs/router/router';
import App from './App.vue';
import './style.css';
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
  VChip,
  VCol,
  VContainer,
  VDataTableServer,
  VDivider,
  VForm,
  VIcon,
  VList,
  VListItem,
  VMain,
  VNavigationDrawer,
  VRow,
  VSelect,
  VSnackbar,
  VSpacer,
  VTextField,
  VToolbar,
  VToolbarTitle,
} from 'vuetify/components';
import { Ripple } from 'vuetify/directives';
import 'vuetify/styles';

const vuetify = createVuetify({
  components: {
    VAlert,
    VApp,
    VAppBar,
    VAppBarTitle,
    VBtn,
    VCard,
    VChip,
    VCol,
    VContainer,
    VDataTableServer,
    VDivider,
    VForm,
    VIcon,
    VList,
    VListItem,
    VMain,
    VNavigationDrawer,
    VRow,
    VSelect,
    VSnackbar,
    VSpacer,
    VTextField,
    VToolbar,
    VToolbarTitle,
  },
  directives: {
    Ripple,
  },
  icons: {
    defaultSet: 'mdi',
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
