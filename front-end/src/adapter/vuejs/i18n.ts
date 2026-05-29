import en from './locales/en.json';
import es from './locales/es.json';
import { createI18n } from 'vue-i18n';

/**
 * Shared i18n instance for use outside Vue components (e.g. Pinia stores).
 */
export const i18n = createI18n({
  legacy: false,
  locale: 'es',
  fallbackLocale: 'en',
  messages: {
    en,
    es,
  },
});
