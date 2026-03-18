import '@mdi/font/css/materialdesignicons.css';
import { createPinia } from 'pinia';
import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import 'vuetify/styles';
import App from './App.vue';
import en from './adapter/vuejs/locales/en.json';
import es from './adapter/vuejs/locales/es.json';
import router from './adapter/vuejs/router/router';
import './style.css';

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
  },
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
