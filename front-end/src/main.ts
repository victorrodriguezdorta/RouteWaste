import { createPinia } from 'pinia'
import { createApp } from 'vue'
import router from './adapter/vuejs/router/router'
import App from './App.vue'
import './style.css'

// Vuetify
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'

// Vue I18n
import { createI18n } from 'vue-i18n'
import en from './adapter/vuejs/locales/en.json'
import es from './adapter/vuejs/locales/es.json'

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
