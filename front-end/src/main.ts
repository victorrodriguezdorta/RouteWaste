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
  VTab,
  VTabs,
  VTextField,
  VToolbar,
  VToolbarTitle,
  VTooltip,
  VWindow,
  VWindowItem,
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
    VTabs,
    VTab,
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
        colors: {
          primary: '#31572c',      // Slate oscuro elegante
          secondary: '#4f772d',    // Slate oscuro medio
          accent: '#90a955',       // Azul moderno para resaltar
          error: '#880d1e',        // Rojo claro
          'danger-dark': '#5c0f1b', // Rojo oscuro para estados críticos
          info: '#4f772d',         // Azul claro
          success: '#90a955',      // Verde esmeralda
          warning: '#ffca3a',      // Ámbar
          'on-primary': '#ffffff',
          'text-muted': '#757575',
          'neutral-base': '#000000',
          'surface-soft': '#f5f5f5',
          'surface-variant': '#eeeeee',
          'surface-border-light': 'rgba(255, 255, 255, 0.7)',
          'border-light': 'rgba(0, 0, 0, 0.08)',
          background: '#ffffffef',   // Gris muy claro con tono frío
          surface: '#FFFFFF',      // Color de las tarjetas (blanco)
          /** UI neutrals (sombras, bordes, texto secundario vía rgba sobre neutral-base) */
          'shadow-slate': '#0f172a',
          /** Mapas Leaflet / pins secundarios */
          'map-pin-muted': '#9e9e9e',
          'route-return-leg': '#b00020',
          'route-departure-leg': '#1e88e5',
          'route-progression-leg': '#00a83a',
          'facility-marker-fill': '#d32f2f',
          /** Contenedores visuales (mapa, drawer móvil) */
          'panel-gradient-start': '#e8eef5',
          'panel-gradient-end': '#dfe7f0',
          'drawer-backdrop-gradient-start': '#f4f6f9',
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
